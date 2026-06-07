package com.example.msi.ui.inicio;

import java.util.ArrayList;
import java.util.List;

public final class InicioUiState {
    private final boolean statusOnline;
    private final String statusLabel;
    private final String statusLastUpdated;
    private final String eventosHoje;
    private final String alertasAtivos;
    private final String tempoOnline;
    private final List<ChartPoint> chartPoints;
    private final List<HistoryEvent> historyEvents;

    public InicioUiState(
            boolean statusOnline,
            String statusLabel,
            String statusLastUpdated,
            String eventosHoje,
            String alertasAtivos,
            String tempoOnline,
            List<ChartPoint> chartPoints,
            List<HistoryEvent> historyEvents
    ) {
        this.statusOnline = statusOnline;
        this.statusLabel = statusLabel;
        this.statusLastUpdated = statusLastUpdated;
        this.eventosHoje = eventosHoje;
        this.alertasAtivos = alertasAtivos;
        this.tempoOnline = tempoOnline;
        this.chartPoints = chartPoints == null ? new ArrayList<>() : new ArrayList<>(chartPoints);
        this.historyEvents = historyEvents == null ? new ArrayList<>() : new ArrayList<>(historyEvents);
    }

    public boolean isStatusOnline() {
        return statusOnline;
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

    public List<ChartPoint> getChartPoints() {
        return new ArrayList<>(chartPoints);
    }

    public List<HistoryEvent> getHistoryEvents() {
        return new ArrayList<>(historyEvents);
    }

}
