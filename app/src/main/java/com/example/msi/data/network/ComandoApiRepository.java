package com.example.msi.data.network;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ComandoApiRepository {

    public interface Callback {
        void onSuccess(String responseBody);
        void onError(Exception error);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void enviarComando(String acao, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(normalizeBaseUrl(ApiConfig.BASE_URL) + "/comandos");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("acao", acao);
                body.put("dispositivo", "Pulseira Principal");

                try (OutputStream os = connection.getOutputStream();
                     OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                     BufferedWriter writer = new BufferedWriter(osw)) {
                    writer.write(body.toString());
                    writer.flush();
                }

                int responseCode = connection.getResponseCode();
                InputStream stream = responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                String responseBody = readFully(stream);
                if (responseCode < 200 || responseCode >= 300) {
                    throw new IllegalStateException("HTTP " + responseCode + ": " + responseBody);
                }

                callback.onSuccess(responseBody);
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String readFully(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder builder = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, read);
            }
        }
        return builder.toString();
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) return "";
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
