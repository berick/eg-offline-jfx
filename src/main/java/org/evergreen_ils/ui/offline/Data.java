package org.evergreen_ils.ui.offline;

import org.json.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class Data {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    final static String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";
    final static String OFFLINE_EXPORT = "offline-export.txt";
    final static String OFFLINE_DATA_FILE = "offline-data.json";

    static ArrayList<NonCatType> nonCatTypes = new ArrayList<NonCatType>();

    // These are kept in memory for the duration of the running process
    // so we can refresh the offline data and upload transactions
    // in the background w/o prompting for a login.
    static String username;
    static String password;
    static String authtoken;

    // Points to our offline DB schema definition file.
    static URL schemaUrl;

    static Config activeConfig;
    static List<Config> configList = new ArrayList<Config>();

    // sqlite3 connection.
    private static Connection conn;

    // All transactions in our offline database
    static ObservableList<Transaction> xactsList;

    // All pending (non-exported) transactions in our offline database
    static ObservableList<Transaction> pendingXactsList;

    /**
     * Connect to the offline database
     */
    static void connect() throws SQLException {
        conn = DriverManager.getConnection(OFFLINE_DB_URL);
        xactsList = FXCollections.observableArrayList();
        pendingXactsList = FXCollections.observableArrayList();
    }

    /**
     * Create the offline database
     */
    static void createDatabase()
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
    static void saveTransactions(List<Transaction> xacts) throws SQLException {
        for (Transaction xact: xacts) {
            saveTransaction(xact);
        }
    }

    /**
     * Store a Transaction in the offline database
     */
    static Transaction saveTransaction(Transaction xact) throws SQLException {

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
            return Data.getXactFromDatabase(id);
        }

        return null;
    }

    static List<Config> getConfigOptions() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM config");

        ResultSet set = stmt.executeQuery();

        while (set.next()) {
            Config config = new Config();
            config.setHostname(set.getString("hostname"));
            config.setWorkstation(set.getString("workstation"));
            config.setIsDefault(set.getInt("is_default") == 1);

            if (config.getIsDefault()) {
                activeConfig = config;
            }

            logger.info("Found config: " + config);

            configList.add(config);
        }

        return configList;
    }

    static Config getConfigByWorkstation(String ws) {
        
        for (Config config: configList) {
            if (config.getWorkstation().equals(ws)) {
                return config;
            }
        }

        return null;
    }

    /**
     * Write pending transactions to a file
     */
    static void exportPendingXactsToFile() throws IOException, SQLException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(OFFLINE_EXPORT));

        PreparedStatement stmt = conn.prepareStatement(
            "SELECT * FROM xact WHERE export_time IS NULL");

        ResultSet set = stmt.executeQuery();

        while (set.next()) {
            Transaction xact = Data.getXactFromDbRow(set);
            writer.write(xact.toJson());
            writer.newLine();
        }

        writer.close();
    }


    /**
     * Maps a database row to an in-memory Transaction object.
     */
    static Transaction getXactFromDbRow(ResultSet set) throws SQLException {
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
    static Transaction getXactFromDatabase(String id) throws SQLException {

        String sql = "SELECT * FROM xact WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);

        ResultSet set = stmt.executeQuery();

        if (!set.next()) { return null; }

        return Data.getXactFromDbRow(set);
    }

    // Bit of a shortcut
    static String encode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
    }

    static void loadServerValues() {

        // TODO
        // If we are connected to the network, fetch updated data
        // Otherwise, load the JSON file

        if (activeConfig == null) { return; }

        String url = "";

        try {

            url = String.format(
                "https://%s/offline-data?workstation=%s&username=%s&password=%s",
                Data.encode(activeConfig.getHostname()),
                Data.encode(activeConfig.getWorkstation()),
                Data.encode(Data.username),
                Data.encode(Data.password)
            );

        } catch (UnsupportedEncodingException e) {

            logger.severe("Cannot create server URL: " + url);
            e.printStackTrace();
            return;
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpClient client = HttpClient.newHttpClient();

        client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(new Consumer<String>() {
                public void accept(String body) {
                    Data.absorbOfflineData(body);
                }
            })
            .join();
    }

    static void absorbOfflineData(String data) {

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

        JSONObject obj = new JSONObject(data);

        JSONArray ncTypes = obj.getJSONArray("cnct");

        for (int i = 0; i < ncTypes.length(); i++) {
            JSONObject nct = ncTypes.getJSONObject(i);

            // TODO check for string version if ID
            // handle exceptions, etc.

            nonCatTypes.add(
                new NonCatType(
                    nct.getInt("id"),
                    nct.getString("name")
                )
            );
        }

        /*
        nonCatTypes.add(new NonCatType(1, "Paperback"));
        nonCatTypes.add(new NonCatType(2, "Newspaper"));
        */
    }
}


