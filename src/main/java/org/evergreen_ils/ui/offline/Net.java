package org.evergreen_ils.ui.offline;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class Net {

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    // True if the last call to canConnect returned true;
    boolean isOnline = false;

    /**
     * Returns true if we can successfully ping the EG server.
     */
    boolean canConnect(String hostname) {

        String url = String.format("https://%s/offline-data?ping=1", hostname);
 
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse response;

        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            logger.info("Cannot connect to " + hostname + ": " + e);
            return false;
        }

        String body = (String) response.body();

        isOnline = "\"pong\"".equals(body); // JSON

        return isOnline;
    }
}


