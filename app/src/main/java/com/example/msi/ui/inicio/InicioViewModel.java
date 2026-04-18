package com.example.msi.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.msi.R;
import java.util.ArrayList;
import java.util.List;

public final class InicioViewModel extends ViewModel {
    private final MutableLiveData<InicioUiState> state = new MutableLiveData<>();

    public InicioViewModel() {
        // No backend yet. Keep UI working with defaults and expose a single state object.
        state.setValue(new InicioUiState(
                "Online",
                "Atualizado 2 min atrás",
                "12",
                "2",
                "8h",
                new ArrayList<>()
        ));
    }

    public LiveData<InicioUiState> getState() {
        return state;
    }

    // Later: backend can call this.
    public void setHistory(List<HistoryEvent> history) {
        InicioUiState cur = state.getValue();
        if (cur == null) return;
        state.setValue(new InicioUiState(
                cur.getStatusLabel(),
                cur.getStatusLastUpdated(),
                cur.getEventosHoje(),
                cur.getAlertasAtivos(),
                cur.getTempoOnline(),
                history
        ));
    }
}

