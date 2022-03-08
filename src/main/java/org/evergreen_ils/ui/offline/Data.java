package org.evergreen_ils.ui.offline;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Data {

    final static String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";
    final static String RESOURCE_DIR = "org/evergreen_ils/ui/offline/";
    final static String SCHEMA_FILE = "offline-schema.sql";
    final static String OFFLINE_EXPORT = "offline-export.txt";

    private static Connection conn;

    static void connect() throws SQLException {

        //try {
            conn = DriverManager.getConnection(OFFLINE_DB_URL);
        //} catch (SQLException e) {
            //e.printStackTrace();
            //throw e;
        //}
    }

    static void createDatabase()
        throws URISyntaxException, IOException, SQLException {

        File file = getResourceFile(SCHEMA_FILE);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String sql = "";

        while ((line = reader.readLine()) != null) {
            sql += line + "\n";
        }

        System.out.println(sql);

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }

    static File getResourceFile(String fileName)
        throws URISyntaxException, IOException {

        ClassLoader classLoader = Data.class.getClassLoader();

        URL resource = classLoader.getResource(RESOURCE_DIR);

        return Files.walk(Paths.get(resource.toURI()))
            .filter(Files::isRegularFile)
            .map(x -> x.toFile())
            .filter(x -> x.getName().equals(fileName))
            .findFirst().get();
    }

    static void saveTransactions(List<Transaction> xacts) throws SQLException {
        for (Transaction xact: xacts) {
            saveTransaction(xact);
        }
    }

    static void saveTransaction(Transaction xact) throws SQLException {

        String sql =
            "INSERT INTO xact (action, due_date, backdate, "
            + "item_barcode, patron_barcode, noncat_type, noncat_count)"
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, xact.getAction());
        stmt.setString(2, xact.getDueDate());
        stmt.setString(3, xact.getBackDate());
        stmt.setString(4, xact.getItemBarcode());
        stmt.setString(5, xact.getPatronBarcode());
        stmt.setString(6, xact.getNonCatType());
        stmt.setString(7, xact.getNonCatCount());

        stmt.executeUpdate();
    }



    /*
    CREATE TABLE IF NOT EXISTS xact (
        id INTEGER PRIMARY KEY,
        realtime TEXT DEFAULT (DATETIME()) NOT NULL,
        action TEXT,
        due_date TEXT,
        backdate TEXT,
        item_barcode TEXT,
        noncat_type TEXT,
        noncat_count TEXT,
        patron_barcode TEXT
    );
    */

    static void saveAllTransactionsToFile() throws IOException, SQLException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(OFFLINE_EXPORT));

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM xact");
        ResultSet set = stmt.executeQuery();

        while (set.next()) {

            Transaction xact = new Transaction();
            xact.setRealTime(set.getString(1));
            xact.setAction(set.getString(2));
            xact.setDueDate(set.getString(3));
            xact.setBackDate(set.getString(4));
            xact.setItemBarcode(set.getString(5));
            xact.setPatronBarcode(set.getString(6));
            xact.setNonCatType(set.getString(7));
            xact.setNonCatCount(set.getString(8));

            writer.write(xact.toJson());
            writer.newLine();
        }

        writer.close();
    }
}


