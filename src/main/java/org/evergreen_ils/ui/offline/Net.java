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
import java.util.function.Consumer;

import javafx.util.Callback;

public class Net {

    static final int HTTP_CONNECT_TIMEOUT = 10;
    static final int HTTP_REQUEST_TIMEOUT = 120;
    static final String HTTP_OFFLINE_PATH = "offline-data";

    boolean isOnline;

    // Bit of a shortcut
    String encode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
    }

    void getUrlBody(String url, Callback<String, Void> callback) {

        App.logger.info("Getting URL: " + url);

        // Create a new client with each url lookup so we can leverage
        // the shorter connect timeout to see if we are in fact online.
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(HTTP_CONNECT_TIMEOUT))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(HTTP_REQUEST_TIMEOUT))
            .build();

        Consumer<HttpResponse<String>> consumer = response -> {
            int code = response.statusCode();
            App.logger.info("HTTP request returned code = " + code);

            if (code != 200) {
                App.logger.severe(
                    "Error fetching resource: status=" + code + " : " + request);

                callback.call(null);
            }

            callback.call((String) response.body());
        };

        try {

            client.sendAsync(request, BodyHandlers.ofString()).thenAccept(consumer);

        } catch (Exception e) {

            // Force ourselves offline since something failed.
            isOnline = false;

            App.logger.info("Cannot load URL: " + url + " " + e);
        }
    }

    void testConnection(Context ctx, Callback<Boolean, Void> callback) {

        String url = String.format(
            "https://%s/%s?ping=1", ctx.hostname, HTTP_OFFLINE_PATH);

        getUrlBody(url, body -> {
            isOnline = "\"pong\"".equals(body); // JSON
            App.logger.info("Set isOnline to " + isOnline);
            callback.call(isOnline);
            return null;
        });
    }

    void getOrgUnits(Context ctx, Callback<String, Void> callback) {

        String url = String.format(
           "https://%s/%s?org_units=1", ctx.hostname, HTTP_OFFLINE_PATH);

        getUrlBody(url, callback);
    }
}
