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
import java.nio.charset.StandardCharsets;

public class Net {

    static final String OFFLINE_PATH="offline-data";

    static final Logger logger =
        Logger.getLogger(App.class.getPackage().getName());

    // True if the last call to canConnect returned true;
    boolean isOnline = false;

    // Bit of a shortcut
    String encode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
    }

    String getUrlBody(String url) {

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse response;

        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            logger.info("Cannot load URL: " + url + " " + e);
            return null;
        }

        return (String) response.body();
    }

    /**
     * Returns true if we can successfully ping the EG server.
     * TODO add a timeout
     */
    boolean canConnect(String hostname) {

        String url = String.format("https://%s/%s?ping=1", hostname, OFFLINE_PATH);

        String body = getUrlBody(url);

        isOnline = "\"pong\"".equals(body); // JSON

        return isOnline;
    }

    String getOrgUnits(Config config) {
        String url = String.format(
           "https://%s/%s?org_units=1", config.getHostname(), OFFLINE_PATH);
        return getUrlBody(url);
    }

    String getServerData(Config config) {

        String url = null;

        try {

            url = String.format(
                "https://%s/%s?workstation=%s&username=%s&password=%s",
                encode(config.getHostname()),
                OFFLINE_PATH,
                encode(config.getWorkstation()),
                encode(config.getUsername()),
                encode(config.getPassword())
            );

        } catch (UnsupportedEncodingException e) {

            logger.severe("Cannot create server URL: " + url);
            e.printStackTrace();
            return null;
        }

        return getUrlBody(url);
    }
}


