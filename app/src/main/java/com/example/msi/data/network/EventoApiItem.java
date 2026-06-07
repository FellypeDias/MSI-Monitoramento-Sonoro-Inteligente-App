package com.example.msi.data.network;

public final class EventoApiItem {
    private final long id;
    private final String tipoEvento;
    private final Integer intensidade;
    private final Integer dispositivoId;
    private final String dataHora;

    public EventoApiItem(long id, String tipoEvento, Integer intensidade, Integer dispositivoId, String dataHora) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.intensidade = intensidade;
        this.dispositivoId = dispositivoId;
        this.dataHora = dataHora;
    }

    public long getId() {
        return id;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public Integer getIntensidade() {
        return intensidade;
    }

    public Integer getDispositivoId() {
        return dispositivoId;
    }

    public String getDataHora() {
        return dataHora;
    }
}

