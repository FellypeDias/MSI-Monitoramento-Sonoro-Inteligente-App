package com.example.msi.ui.inicio;

import java.util.List;

public final class InicioUiState {
    private final String statusLabel;
    private final String statusLastUpdated;
    private final String eventosHoje;
    private final String alertasAtivos;
    private final String tempoOnline;

    public InicioUiState(String statusLabel, String statusLastUpdated, String eventosHoje, String alertasAtivos, String tempoOnline) {
        this.statusLabel = statusLabel;
        this.statusLastUpdated = statusLastUpdated;
        this.eventosHoje = eventosHoje;
        this.alertasAtivos = alertasAtivos;
        this.tempoOnline = tempoOnline;
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

}
