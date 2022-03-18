package org.evergreen_ils.ui.offline;

public class Config {
    boolean isDefault;
    String hostname;
    String workstation;

    void setHostname(String h) {
        hostname = h;
    }

    void setWorkstation(String w) {
        workstation = w;
    }

    void setIsDefault(boolean d) {
        this.isDefault = d;
    }

    String getHostname() {
        return hostname;
    }

    String getWorkstation() {
        return workstation;
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

