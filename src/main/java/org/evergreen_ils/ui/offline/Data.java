package org.evergreen_ils.ui.offline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Data {

    final String OFFLINE_DB_URL = "jdbc:sqlite:offline.db";

    Connection conn;
    
    void connect() throws java.sql.SQLException {
        conn = DriverManager.getConnection(OFFLINE_DB_URL);
    }

    void createDatabase() {
        /*
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        */
    }
}

