package org.evergreen_ils.ui.offline;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

public class OrgUnit {
    String name;
    String shortname;
    String label;
    int id;
    int depth;
    boolean canHaveUsers;
    List<OrgUnit> children;

    public OrgUnit(JSONObject org) {

        name = org.getString("name");
        shortname = org.getString("shortname");
        id = org.getInt("id");

        JSONObject ouType = org.getJSONObject("ou_type");
        depth = ouType.getInt("depth");
        canHaveUsers = "t".equals(ouType.getString("can_have_users"));

        String padding = new String(new char[depth]).replace("\0", "  ");
        label = padding + name + " (" + shortname + ")";
    }

    public int toValue() {
        return id;
    }

    @Override
    public String toString() {
        return label;
    }
}

