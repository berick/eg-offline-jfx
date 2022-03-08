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

public class Data {

    final static String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";
    final static String RESOURCE_DIR = "org/evergreen_ils/ui/offline/";
    final static String SCHEMA_FILE = "offline-schema.sql";

    private static Connection conn;

    static void connect() throws java.sql.SQLException {

        //try {
            conn = DriverManager.getConnection(OFFLINE_DB_URL);
        //} catch (java.sql.SQLException e) {
            //e.printStackTrace();
            //throw e;
        //}
    }

    static void createDatabase()
        throws URISyntaxException, IOException, java.sql.SQLException {

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

    static void saveCheckouts(List<CheckoutEntry> entries) throws java.sql.SQLException {

        for (CheckoutEntry entry: entries) {

            String sql =
                "INSERT INTO xact (action, due_date, item_barcode, patron_barcode)"
                + "VALUES ('checkout', ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, entry.getDueDate());
            stmt.setString(2, entry.getItemBarcode());
            stmt.setString(3, entry.getPatronBarcode());

            stmt.executeUpdate();
        }
    }
}

