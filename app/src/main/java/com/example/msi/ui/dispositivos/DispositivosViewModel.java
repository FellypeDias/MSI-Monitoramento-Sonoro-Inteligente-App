package com.example.msi.ui.dispositivos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.msi.data.network.ComandoApiRepository;

public final class DispositivosViewModel extends ViewModel {
    private final MutableLiveData<DispositivosUiState> state = new MutableLiveData<>();
    private final ComandoApiRepository repository = new ComandoApiRepository();

    public DispositivosViewModel() {
        state.setValue(new DispositivosUiState(false, "Conectado"));
    }

    public LiveData<DispositivosUiState> getState() {
        return state;
    }

    public void enviarLed() {
        enviarComando("LED");
    }

    public void enviarVibrar() {
        enviarComando("VIBRAR");
    }

    private void enviarComando(String acao) {
        state.setValue(new DispositivosUiState(true, "Enviando " + acao + "..."));
        repository.enviarComando(acao, new ComandoApiRepository.Callback() {
            @Override
            public void onSuccess(String responseBody) {
                state.postValue(new DispositivosUiState(false, acao + " enviado com sucesso"));
            }

            @Override
            public void onError(Exception error) {
                state.postValue(new DispositivosUiState(false, "Erro ao enviar " + acao));
            }
        });
    }
}
