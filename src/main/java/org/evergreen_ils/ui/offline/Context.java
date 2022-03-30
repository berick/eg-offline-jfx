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
    String authtoken;   // in-memory only

    @Override public String toString() {
        if (hostname == null) {
            return "";
        } else {
            if (workstation == null) {
                return String.format("@%s", hostname);
            } else {
                return String.format("%s@%s", workstation, hostname);
            }
        }
    }
}
