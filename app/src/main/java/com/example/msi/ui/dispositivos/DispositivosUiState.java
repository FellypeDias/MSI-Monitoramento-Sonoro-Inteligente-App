package com.example.msi.ui.dispositivos;

public final class DispositivosUiState {
    private final boolean enviando;
    private final String mensagem;

    public DispositivosUiState(boolean enviando, String mensagem) {
        this.enviando = enviando;
        this.mensagem = mensagem;
    }

    public boolean isEnviando() {
        return enviando;
    }

    public String getMensagem() {
        return mensagem;
    }
}
