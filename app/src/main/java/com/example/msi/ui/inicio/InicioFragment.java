package com.example.msi.ui.inicio;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.msi.R;
import com.example.msi.databinding.FragmentInicioBinding;

public final class InicioFragment extends Fragment {
    private FragmentInicioBinding binding;
    private HistoryAdapter historyAdapter;

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentInicioBinding.bind(view);

        historyAdapter = new HistoryAdapter();
        binding.rvHistorico.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistorico.setAdapter(historyAdapter);

        InicioViewModel vm = new ViewModelProvider(this).get(InicioViewModel.class);
        vm.getState().observe(getViewLifecycleOwner(), this::render);
    }

    private void render(InicioUiState state) {
        if (state == null || binding == null) return;
        binding.textStatusOnlineOffline.setText(state.getStatusLabel());
        binding.textAtualizacaoStatus.setText(state.getStatusLastUpdated());
        binding.textREventosHoje.setText(state.getEventosHoje());
        binding.textRAlertasAtivos.setText(state.getAlertasAtivos());
        binding.textRTempoOnline.setText(state.getTempoOnline());
        historyAdapter.submitList(state.getHistory());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

