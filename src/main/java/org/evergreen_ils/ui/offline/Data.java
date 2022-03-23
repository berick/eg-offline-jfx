package org.evergreen_ils.ui.offline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Data {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    final static String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";
    final static String OFFLINE_EXPORT = "offline-export.txt";
    final static String OFFLINE_DATA_FILE = "offline-data.json";

    ArrayList<NonCatType> nonCatTypes = new ArrayList<NonCatType>();

    // Points to our offline DB schema definition file.
    URL schemaUrl;

    JSONObject orgUnits;

    // Host chosen in the initial host select page.
    String startupHost;

    Config activeConfig;
    List<Config> configList = new ArrayList<Config>();

    // sqlite3 connection.
    private Connection conn;

    // All transactions in our offline database
    ObservableList<Transaction> xactsList;

    // All pending (non-exported) transactions in our offline database
    ObservableList<Transaction> pendingXactsList;

    // TODO: Cleaning code copied from Hatch.  Use in a shared spot?
    // routine for scrubbing invalid chars from file names / paths
    // http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars
    final static int[] illegalChars = {
        34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
        58, 42, 63, 92, 47
    };

    static { Arrays.sort(illegalChars); }

    /**
     * Connect to the offline database
     */
    void connect() throws SQLException {
        conn = DriverManager.getConnection(OFFLINE_DB_URL);
        xactsList = FXCollections.observableArrayList();
        pendingXactsList = FXCollections.observableArrayList();
    }

    /**
     * Create the offline database
     */
    void createDatabase()
        throws URISyntaxException, IOException, SQLException {

        BufferedReader reader =
            new BufferedReader(new FileReader(schemaUrl.getFile()));
        String line;
        String sql = "";

        while ((line = reader.readLine()) != null) {
            sql += line + "\n";
        }


        // No way to execute multiple statements from a single file in
        // Java, short of executing a bare sqlite3 command.  Split on
        // statement separator and feed them.
        // TODO: break these into per-table files?
        // https://stackoverflow.com/questions/2071682/how-to-execute-sql-script-file-in-java
        String[] inserts = sql.split(";");

        for (String insert: sql.split(";")) {
            insert = insert.trim();
            if (insert.length() > 0) {
                Statement stmt = conn.createStatement();
                stmt.execute(insert + ";");
            }
        }
    }

    /**
     * Store a list of Transaction in the offline database
     */
    void saveTransactions(List<Transaction> xacts) throws SQLException {
        for (Transaction xact: xacts) {
            saveTransaction(xact);
        }
    }

    /**
     * Store a Transaction in the offline database
     */
    Transaction saveTransaction(Transaction xact) throws SQLException {

        String sql =
            "INSERT INTO xact (action, due_date, backdate, "
            + "item_barcode, patron_barcode, noncat_type, noncat_count)"
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt =
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, xact.getAction());
        stmt.setString(2, xact.getDueDate());
        stmt.setString(3, xact.getBackDate());
        stmt.setString(4, xact.getItemBarcode());
        stmt.setString(5, xact.getPatronBarcode());
        stmt.setString(6, xact.getNonCatType());
        stmt.setString(7, xact.getNonCatCount());

        int count = stmt.executeUpdate();

        if (count == 0) { return null; }

        ResultSet generatedKeys = stmt.getGeneratedKeys();

        if (generatedKeys.next()) {
            String id = generatedKeys.getString(1);
            return getXactFromDatabase(id);
        }

        return null;
    }

    List<Config> getConfigOptions() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM config");

        ResultSet set = stmt.executeQuery();

        while (set.next()) {
            Config config = new Config();
            config.setHostname(set.getString("hostname"));
            config.setWorkstation(set.getString("workstation"));
            config.setOrgUnit(set.getInt("org_unit"));
            config.setIsDefault(set.getInt("is_default") == 1);

            if (config.getIsDefault()) {
                activeConfig = config;
            }

            logger.info("Found config: " + config);

            configList.add(config);
        }

        return configList;
    }

    /**
     * Write pending transactions to a file
     */
    void exportPendingXactsToFile() throws IOException, SQLException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(OFFLINE_EXPORT));

        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM xact WHERE export_time IS NULL");

        ResultSet set = stmt.executeQuery();

        while (set.next()) {
            Transaction xact = getXactFromDbRow(set);
            writer.write(xact.toJson());
            writer.newLine();
        }

        writer.close();
    }


    /**
     * Maps a database row to an in-memory Transaction object.
     */
    Transaction getXactFromDbRow(ResultSet set) throws SQLException {
        Transaction xact = new Transaction();

        xact.setId(set.getString("id"));
        xact.setRealTime(set.getString("real_time"));
        xact.setExportTime(set.getString("export_time"));
        xact.setAction(set.getString("action"));
        xact.setDueDate(set.getString("due_date"));
        xact.setBackDate(set.getString("backdate"));
        xact.setItemBarcode(set.getString("item_barcode"));
        xact.setPatronBarcode(set.getString("patron_barcode"));
        xact.setNonCatType(set.getString("noncat_type"));
        xact.setNonCatCount(set.getString("noncat_count"));

        if (xact.getItemBarcode() != null) {
            xact.setItemLabel(xact.getItemBarcode());
        } else {
            for (NonCatType nct: nonCatTypes) {
                if (nct.getId().toString().equals(xact.getNonCatType())) {
                    xact.setItemLabel(nct.getLabel());
                }
            }
        }

        return xact;
    }

    /**
     * Returns the Transaction with the provided id.
     */
    Transaction getXactFromDatabase(String id) throws SQLException {

        String sql = "SELECT * FROM xact WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);

        ResultSet set = stmt.executeQuery();

        if (!set.next()) { return null; }

        return getXactFromDbRow(set);
    }

    // Bit of a shortcut
    String encode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
    }

    public static String cleanFileName(String badFileName) {
        char lastChar = 0;
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < badFileName.length(); i++) {
            int c = (int)badFileName.charAt(i);
            if (Arrays.binarySearch(illegalChars, c) < 0) {
                cleanName.append((char)c);
                lastChar = (char) c;
            } else {
                // avoid dupe-ing the placeholder chars, since that
                // relays no useful information (apart from the number
                // of illegal characters)
                // This is usefuf or things like https:// with dupe "/" chars
                if (lastChar != '_')
                    cleanName.append('_');
                lastChar = '_';
            }
        }
        return cleanName.toString();
    }
    // --------------------------------------------------


    /**
     * Returns the path to the data directory as a String
     */
    String createDataDir() {

        String hostname = Data.cleanFileName(activeConfig.getHostname());

        String dirPath = String.format("data/%s/%d",
            Data.cleanFileName(activeConfig.getHostname()),
            activeConfig.getOrgUnit());

        // This will not ovewrite existing files.
        new File(dirPath).mkdirs();

        return dirPath;
    }

    void loadOrgUnits() {

        // Try to pull from the network if possible.

    }

    void loadServerData() {

        if (activeConfig == null) { return; }

        String dataPath = createDataDir();

        // TODO if not connected, we need to know what org unit
        // is linked to the selected workstatation so we can
        // load the correct offline file.

        if (!App.net.canConnect(activeConfig.getHostname())) {
            readOfflineDataFile();
            return;
        }

        String body = App.net.getServerData(activeConfig);
        absorbOfflineData(body);
    }

    void readOfflineDataFile() {

        BufferedReader reader;
        String json = "";

        try {
            reader = new BufferedReader(new FileReader(OFFLINE_DATA_FILE));

            String line;
            while ( (line = reader.readLine()) != null) {
                json += line;
            }

        } catch (Exception e) {
            logger.info("No offline data file to read: " + e);
            return;
        }

        absorbOfflineData(json);
    }

    void absorbOfflineData(String data) {

        try {
            BufferedWriter writer =
                new BufferedWriter(new FileWriter(OFFLINE_DATA_FILE));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            logger.severe("Cannot write data file " + OFFLINE_DATA_FILE);
            e.printStackTrace();
            return;
        }

        App.logger.info("Offline Data: \n" + data);

        JSONObject obj;

        try {
            obj = new JSONObject(data);
        } catch (org.json.JSONException e) {
            App.logger.severe("Cannot parse offline data file as JSON: " + e + "\n" + data);
            throw e;
        }

        JSONArray ncTypes = obj.getJSONArray("cnct");

        for (int i = 0; i < ncTypes.length(); i++) {
            JSONObject nct = ncTypes.getJSONObject(i);

            logger.info("loading noncat type " + nct);

            nonCatTypes.add(
                new NonCatType(
                    nct.getInt("id"),
                    nct.getString("name")
                )
            );
        }
    }
}


