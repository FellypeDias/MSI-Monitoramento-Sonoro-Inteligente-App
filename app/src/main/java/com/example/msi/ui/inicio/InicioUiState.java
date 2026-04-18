package com.example.msi.ui.inicio;

import java.util.List;

public final class InicioUiState {
    private final String statusLabel;
    private final String statusLastUpdated;
    private final String eventosHoje;
    private final String alertasAtivos;
    private final String tempoOnline;
    private final List<HistoryEvent> history;

    public InicioUiState(String statusLabel, String statusLastUpdated, String eventosHoje, String alertasAtivos, String tempoOnline, List<HistoryEvent> history) {
        this.statusLabel = statusLabel;
        this.statusLastUpdated = statusLastUpdated;
        this.eventosHoje = eventosHoje;
        this.alertasAtivos = alertasAtivos;
        this.tempoOnline = tempoOnline;
        this.history = history;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getStatusLastUpdated() {
        return statusLastUpdated;
    }

    public String getEventosHoje() {
        return eventosHoje;
    }

    public String getAlertasAtivos() {
        return alertasAtivos;
    }

    public String getTempoOnline() {
        return tempoOnline;
    }

    public List<HistoryEvent> getHistory() {
        return history;
    }
}
