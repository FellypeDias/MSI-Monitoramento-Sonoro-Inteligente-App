package com.example.msi.data.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EventosApiRepository {

    public interface Callback {
        void onSuccess(List<EventoApiItem> eventos);
        void onError(Exception error);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void buscarEventos(Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(normalizeBaseUrl(ApiConfig.BASE_URL) + "/eventos");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                InputStream stream = responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                String body = readFully(stream);
                if (responseCode < 200 || responseCode >= 300) {
                    throw new IllegalStateException("HTTP " + responseCode + ": " + body);
                }

                JSONArray array = new JSONArray(body);
                List<EventoApiItem> eventos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    eventos.add(new EventoApiItem(
                            obj.optLong("id", i),
                            obj.optString("tipo_evento", ""),
                            obj.has("intensidade") && !obj.isNull("intensidade") ? obj.optInt("intensidade") : null,
                            obj.has("dispositivo_id") && !obj.isNull("dispositivo_id") ? obj.optInt("dispositivo_id") : null,
                            obj.optString("data_hora", "")
                    ));
                }

                callback.onSuccess(eventos);
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
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

