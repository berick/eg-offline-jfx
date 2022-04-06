package org.evergreen_ils.ui.offline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class Database {

    final static String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";
    final static String OFFLINE_DB_SCHEMA = "offline-schema.sql";

    // sqlite3 connection.
    private Connection conn;

    //ObservableList<Transaction> xactsList;

    void connect() {
        try {
            conn = DriverManager.getConnection(OFFLINE_DB_URL);
        } catch (Exception e) {
            Error.alertAndExit(e,
                "Cannot connect to SQLITE database: " + OFFLINE_DB_URL);
        }

        //xactsList = new FXCollections.observableArrayList();
    }

    /**
     * Create the offline database
     * @throws IOException
     * @throws FileNotFoundException
     * @throws SQLException
     */
    void createDatabase() throws FileNotFoundException, IOException, SQLException {

        BufferedReader reader = new BufferedReader(
            new FileReader(App.getResource(OFFLINE_DB_SCHEMA).getFile()));

        String line;
        String sql = "";

        while ((line = reader.readLine()) != null) {
            sql += line + "\n";
        }


        // No way to execute multiple statements from a single file in
        // Java, short of executing a bare sqlite3 command.  Split on
        // statement separator and feed them.
        // NOTE: break these into per-table files?
        // https://stackoverflow.com/questions/2071682/how-to-execute-sql-script-file-in-java

        for (String insert: sql.split(";")) {
            insert = insert.trim();
            if (insert.length() > 0) {
                Statement stmt = conn.createStatement();
                stmt.execute(insert + ";");
            }
        }
    }

    List<Context> loadKnownContexts() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM config");

        ResultSet set = stmt.executeQuery();
        List<Context> ctxList = new ArrayList<>();

        while (set.next()) {
            Context ctx = new Context();
            ctx.hostname = set.getString("hostname");
            ctx.workstation = set.getString("workstation");
            ctx.orgUnitId = set.getInt("org_unit");
            ctx.isDefault = set.getInt("is_default") == 1;

            ctxList.add(ctx);
        }

        return ctxList;
    }

    void addHostConfig(String hostname) throws SQLException {

        App.logger.info("Adding config entry for host " + hostname);

        PreparedStatement stmt =
            conn.prepareStatement("INSERT INTO config (hostname) VALUES (?)");

        stmt.setString(1, hostname);

        stmt.executeUpdate();
    }

    // Assumes the current context at least has a host and a workstation;
    void saveContext(Context context) throws SQLException {

        // First try to update by matching on host and workstation
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE config SET org_unit = ?, is_default = TRUE WHERE hostname = ? AND workstation = ?");

        stmt.setInt(1, context.orgUnitId);
        stmt.setString(2, context.hostname);
        stmt.setString(3, context.workstation);

        int count = stmt.executeUpdate();

        if (count > 0) {
            // Success
            return;
        }

        // Next assume we have an entry for the hostname and we are just
        // now applying its workstation value.
        stmt = conn.prepareStatement(
            "UPDATE config SET org_unit = ?, workstation = ?, is_default = TRUE WHERE hostname = ? AND workstation IS NULL");

        stmt.setInt(1, context.orgUnitId);
        stmt.setString(2, context.workstation);
        stmt.setString(3, context.hostname);

        count = stmt.executeUpdate();

        if (count > 0) { return; }

        // Add a new host config with no workstation, then update it.
        addHostConfig(context.hostname);

        stmt = conn.prepareStatement(
            "UPDATE config SET org_unit = ?, workstation = ?, is_default = TRUE WHERE hostname = ? AND workstation IS NULL");

        stmt.setInt(1, context.orgUnitId);
        stmt.setString(2, context.workstation);
        stmt.setString(3, context.hostname);

        count = stmt.executeUpdate();

        if (count == 0) {
            Error.alertAndExit(null, "Could not register workstation");
            return;
        }
    }
}
