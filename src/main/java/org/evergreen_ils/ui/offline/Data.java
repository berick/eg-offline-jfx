package org.evergreen_ils.ui.offline;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.HttpClient;
import java.net.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    static ArrayList<NonCatType> nonCatTypes = new ArrayList<NonCatType>();

    static URL schemaUrl;

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

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
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

    /*
    CREATE TABLE IF NOT EXISTS xact (
        id INTEGER PRIMARY KEY,
        real_time TEXT DEFAULT (DATETIME()) NOT NULL,
        export_time TEXT,
        action TEXT,
        due_date TEXT,
        backdate TEXT,
        item_barcode TEXT,
        noncat_type TEXT,
        noncat_count TEXT,
        patron_barcode TEXT
    );
    */

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


    // Pulls cached server data from our database into memory.
    static void loadServerValues() {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("http://openjdk.java.net/"))
              .build();

        client.sendAsync(request, BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenAccept(System.out::println)
              .join();

        // TODO
        nonCatTypes.add(new NonCatType(1, "Paperback"));
        nonCatTypes.add(new NonCatType(2, "Newspaper"));
    }
}


