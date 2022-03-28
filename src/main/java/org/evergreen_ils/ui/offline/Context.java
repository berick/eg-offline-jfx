package org.evergreen_ils.ui.offline;

/**
 * Collection of data related to all transactions.
 */
public class Context {

    String hostname;
    String workstation;
    int orgUnitId;
    boolean isDefault;
    String username;    // in-memory only
    String password;    // in-memory only

    @Override public String toString() {
        return String.format("%s@%s", workstation, hostname);
    }
}
