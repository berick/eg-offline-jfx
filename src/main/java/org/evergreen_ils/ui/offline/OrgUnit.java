package org.evergreen_ils.ui.offline;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

import javafx.util.Callback;

public class OrgUnit {
    static OrgUnit orgTree = null;

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

    static OrgUnit buildOrgTree(JSONObject jsonOrg) {
        OrgUnit org = new OrgUnit(jsonOrg);
        org.children = new ArrayList<OrgUnit>();

        if (OrgUnit.orgTree == null) {
            OrgUnit.orgTree = org;
        }

        JSONArray children = jsonOrg.getJSONArray("children");

        for (int i = 0; i < children.length(); i++) {
            JSONObject childJsonOrg = children.getJSONObject(i);
            org.children.add(OrgUnit.buildOrgTree(childJsonOrg));
        }

        return org;
    }

    static void getOrgUnits(Context ctx, Callback<Void, Void> callback) {

        if (App.net.isOnline) {
            App.net.getOrgUnits(ctx, json -> {
                buildOrgTree(new JSONObject(json));
                // TODO write to file
                callback.call(null);
                return null;
            });
        } else {
            // TODO load from file
            callback.call(null);
        }

    }
}

