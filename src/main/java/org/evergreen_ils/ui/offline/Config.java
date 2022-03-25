package org.evergreen_ils.ui.offline;

public class Config {
    boolean isDefault;
    String hostname;
    String workstation;
    String username;    // in-memory only
    String password;    // in-memory only
    String authtoken;   // in-memory only
    int orgUnit;

    public Config(String hostname) {
        this.hostname = hostname;
    }

    void setHostname(String h) {
        hostname = h;
    }

    void setWorkstation(String w) {
        workstation = w;
    }

    void setIsDefault(boolean d) {
        this.isDefault = d;
    }

    void setUsername(String u) {
        username = u;
    }

    void setAuthtoken(String u) {
        authtoken = u;
    }

    void setPassword(String p) {
        password = p;
    }

    void setOrgUnit(int id) {
        orgUnit = id;
    }

    int getOrgUnit() {
        return orgUnit;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getHostname() {
        return hostname;
    }

    String getWorkstation() {
        return workstation;
    }

    String getAuthtoken() {
        return authtoken;
    }


    boolean getIsDefault() {
        return isDefault;
    }

    @Override
    public String toString() {
        String s = workstation + "@" + hostname;

        if (isDefault) {
            s = s + " (default)";
        }

        return s;
    }
}

