package org.evergreen_ils.ui.offline;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Data retrieval and storage manager.
 */
public class Data {

    Net net;
    Files files;
    Database database;
    OrgUnit orgTree;
    Context context;
    List<Context> knownContexts;

    public Data() {
        orgTree = null;
        net = new Net();
        files = new Files();
        database = new Database();
        context = new Context();
        knownContexts = new ArrayList<>();
    }

    void setup() throws java.io.FileNotFoundException,
        java.io.IOException, java.sql.SQLException {

        App.data.database.connect();
        App.data.database.createDatabase();
    }

    /**
     * Grab OrgUnits from the server when possible and store locally,
     * grabbing from the local if found.
     */
     CompletableFuture<Boolean> getOrgUnits() {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (net.isOnline) {

            net.getOrgUnits().thenAccept(jsonText -> {
                orgTree = buildOrgTree(new JSONObject(jsonText));
                files.writeOrgUnitFile(jsonText);
                future.complete(true);
            });

        } else {

            String jsonText = files.readOrgUnitFile();
            orgTree = buildOrgTree(new JSONObject(jsonText));
            future.complete(true);
        }

        return future;
    }


    OrgUnit buildOrgTree(JSONObject jsonOrg) {

        OrgUnit org = new OrgUnit(jsonOrg);
        org.children = new ArrayList<OrgUnit>();

        JSONArray children = jsonOrg.getJSONArray("children");

        for (int i = 0; i < children.length(); i++) {
            JSONObject childJsonOrg = children.getJSONObject(i);
            org.children.add(buildOrgTree(childJsonOrg));
        }

        return org;
    }
}
