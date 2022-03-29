package org.evergreen_ils.ui.offline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Files {

    final static String OFFLINE_DATA_FILE = "offline-data.json";
    final static String ORG_UNITS_FILE = "org-units.json";

    // TODO: This code copied from Hatch.  Use in a shared spot?
    // Routine for scrubbing invalid chars from file names / paths
    // http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars
    final static int[] illegalChars = {
        34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
        58, 42, 63, 92, 47
    };

    static { Arrays.sort(illegalChars); }

    String cleanFileName(String badFileName) {
        char lastChar = 0;
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < badFileName.length(); i++) {
            int c = (int)badFileName.charAt(i);
            if (Arrays.binarySearch(illegalChars, c) < 0) {
                cleanName.append((char)c);
                lastChar = (char) c;
            } else {
                // avoid dupe-ing the placeholder chars, since that
                // relays no useful information (apart from the number
                // of illegal characters)
                // This is usefuf or things like https:// with dupe "/" chars
                if (lastChar != '_')
                    cleanName.append('_');
                lastChar = '_';
            }
        }

        return cleanName.toString();
    }


    String getHostDataDir() {
        String dirPath = String.format(
            "data/%s", cleanFileName(App.data.context.hostname));

        // Creates the directory when needed.
        new File(dirPath).mkdirs();

        return dirPath;
    }

    void writeOrgUnitFile(String json) {
        String orgFile = getHostDataDir() + "/" + ORG_UNITS_FILE;

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(orgFile));

            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Ui.alertAndExit("Cannot write org units file: " + orgFile);
        }
    }

    String readOrgUnitFile() {

        String orgFile = getHostDataDir() + "/" + ORG_UNITS_FILE;

        String json = "";

        File f = new File(orgFile);

        if (!f.exists() || f.isDirectory()) {
            // If we're reading the org unit file, it means we need them
            Ui.alertAndExit(App.string("error.data.no_orgs"));
            return null;
        }

        try {

            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;
            while ( (line = reader.readLine()) != null) { json += line; }

        } catch (Exception e) {
            e.printStackTrace();
            Ui.alertAndExit("Error reading org unit file: " + orgFile);
        }

        return json;
    }
}

