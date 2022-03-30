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

    class NonCatType {
        int id;
        String name;

        public NonCatType(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    Net net;
    Files files;
    Database database;
    OrgUnit orgTree;
    Context context;
    List<Context> knownContexts;
    List<NonCatType> nonCatTypes;

    public Data() {
        orgTree = null;
        net = new Net();
        files = new Files();
        database = new Database();
        context = new Context();
        knownContexts = new ArrayList<>();
        nonCatTypes = new ArrayList<>();
    }

    void setup() throws java.io.FileNotFoundException,
        java.io.IOException, java.sql.SQLException {

        App.data.database.connect();
        App.data.database.createDatabase();

        loadContexts();
    }

    /**
     * Grab OrgUnits from the server when possible and store locally,
     * grabbing from the local if found.
     */
     CompletableFuture<Boolean> getOrgUnits() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (net.status.isOnline) {

            net.getOrgUnits().thenAccept(jsonText -> {

                orgTree = buildOrgTree(new JSONObject(jsonText));
                files.writeOrgUnitFile(jsonText);
                future.complete(true);

            }).exceptionally(e -> {
                // Failed fetching org units from server.
                // That's OK if we can load them from our local file.

                loadOrgUnitsFromFile();
                future.complete(true);
                return null;
            });

        } else {
            loadOrgUnitsFromFile();
            future.complete(true);
        }

        return future;
    }

    void loadOrgUnitsFromFile() {
        String jsonText = files.readOrgUnitFile();

        orgTree = buildOrgTree(new JSONObject(jsonText));

        App.logger.info("Successfully loaded org units from file");
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

    void loadContexts() {
        try {
            knownContexts = database.loadKnownContexts();
        } catch (java.sql.SQLException e) {
            Error.alertAndExit(e,
                "Error reading configuration from local database");
        }
    }

    CompletableFuture<Boolean> loadServerData() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (net.status.isOnline) {

            net.getOfflineData().thenAccept(json -> {
                absorbOfflineData(json);
                future.complete(true);
            });

        } else {

            String json = files.readOfflineDataFile();
            absorbOfflineData(json);
            future.complete(true);
        }

        return future;
    }

    void absorbOfflineData(String json) {

        JSONObject obj;

        try {
            obj = new JSONObject(json);
        } catch (org.json.JSONException e) {
            Error.alertAndExit(e, "Cannot parse offline data file");
            return;
        }

        JSONArray ncTypes = obj.getJSONArray("cnct");

        for (int i = 0; i < ncTypes.length(); i++) {
            JSONObject nct = ncTypes.getJSONObject(i);

            nonCatTypes.add(
                new NonCatType(
                    nct.getInt("id"),
                    nct.getString("name")
                )
            );
        }
    }
}
