package org.evergreen_ils.ui.offline;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;


public class Net {

    static final int CONNECT_TIMEOUT = 60;
    static final int REQUEST_TIMEOUT = 5;
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

        App.logger.info("Getting URL: " + url);

        // Create a new client with each url lookup so we can leverage
        // the shorter connect timeout to see if we are in fact online.
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
            .build();

        try {

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            int code = response.statusCode();

            App.logger.fine("HTTP request returned code = " + code);

            if (code != 200) {
                App.logger.severe("Error fetching resource: status=" + code + " : " + request);
                return null;
            }

            return (String) response.body();

        } catch (Exception e) {
            isOnline = false;
            logger.info("Cannot load URL: " + url + " " + e);
            return null;
        }
    }

    /**
     * Returns true if we can successfully ping the EG server.
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


