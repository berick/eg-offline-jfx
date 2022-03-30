package org.evergreen_ils.ui.offline;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.time.Duration;

import java.util.function.Consumer;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class Net {


    static final int HTTP_CONNECT_TIMEOUT = 10;
    static final int HTTP_REQUEST_TIMEOUT = 60;
    static final int HTTP_MAX_CONNECTIONS = 4;
    static final String HTTP_OFFLINE_PATH = "offline-data";

    class NetStatus {
        boolean isOnline;
        LinkedBlockingQueue<Boolean> queue;

        public NetStatus() {
            queue = new LinkedBlockingQueue<>();
        }

        void stateChange(boolean isOnline) {
            App.logger.info("Setting online state to online=" + isOnline);
            this.isOnline = isOnline;
            queue.offer(isOnline);
        }
    }

    NetStatus status = new NetStatus();

    // Dedicated thread pool for our HTTP communications.
    // Generally, there will only be one active network request
    // active at a time, but this provides a backstop.
    ExecutorService executor;

    public Net() {
        executor = Executors.newFixedThreadPool(HTTP_MAX_CONNECTIONS);
    }

    // Bit of a shortcut
    String encode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
    }

    CompletableFuture<String> getUrlBody(String url) {

        App.logger.info("Getting URL: " + url);

        CompletableFuture<String> future = new CompletableFuture<>();

        if (url == null || "".equals(url)) {
            future.cancel(true);
            return future;
        }

        // Create a new client with each url lookup so we can leverage
        // the shorter connect timeout to see if we are in fact online.
        HttpClient client = HttpClient.newBuilder()
            .executor(executor)
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
                    "Error fetching data: status=" + code + " : " + request);

                status.stateChange(false);
                future.cancel(true);
            }

            App.logger.info("Completed retrieval of " + url);
            future.complete((String) response.body());
        };

        try {

            client.sendAsync(request, BodyHandlers.ofString())
                .thenAccept(consumer);

        } catch (Exception e) {

            // Force ourselves offline since something failed.
            status.stateChange(false);

            App.logger.info("Cannot load URL: " + url + " " + e);

            future.cancel(true);
        }

        return future;
    }

    CompletableFuture<Boolean> testConnection() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String url = String.format(
            "https://%s/%s?ping=1", App.data.context.hostname, HTTP_OFFLINE_PATH);

        getUrlBody(url)
            .thenAccept(body -> {
                boolean isOnline = "\"pong\"".equals(body); // JSON
                status.stateChange(isOnline);
                future.complete(isOnline);
            });

        return future;
    }

    CompletableFuture<String> getOrgUnits() {
        CompletableFuture<String> future = new CompletableFuture<>();

        String url = String.format(
           "https://%s/%s?org_units=1", App.data.context.hostname, HTTP_OFFLINE_PATH);

        getUrlBody(url)
            .thenAccept(body -> future.complete(body))
            .exceptionally(e -> { future.cancel(true); return null; });

        return future;
    }

    CompletableFuture<String> getOfflineData() {

        String url = null;
        Context ctx = App.data.context;

        try {

            url = String.format(
                "https://%s/%s?workstation=%s&username=%s&password=%s",
                encode(ctx.hostname),
                HTTP_OFFLINE_PATH,
                encode(ctx.workstation),
                encode(ctx.username),
                encode(ctx.password)
            );

        } catch (UnsupportedEncodingException e) {
			Error.alertAndExit(e, "Cannot create server URL: " + url);
        }

        return getUrlBody(url);
    }
}
