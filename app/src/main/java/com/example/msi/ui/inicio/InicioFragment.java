package com.example.msi.ui.inicio;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.msi.R;
import com.example.msi.databinding.FragmentInicioBinding;
import java.util.List;

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
        binding.rvHistorico.setNestedScrollingEnabled(false);
        binding.rvHistorico.setAdapter(historyAdapter);

        View historicoCard = binding.getRoot().findViewById(R.id.HistoricoCard);
        if (historicoCard != null && historicoCard.getLayoutParams() != null) {
            historicoCard.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            historicoCard.requestLayout();
        }

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
        binding.texthistorico.setText("Historico Recente");

        binding.imgCheckStatus.setImageResource(R.drawable.ic_circulo_24);
        binding.imgCheckStatus.setColorFilter(ContextCompat.getColor(
                requireContext(),
                state.isStatusOnline() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));

        historyAdapter.submitList(state.getHistoryEvents());
        renderChart(state.getChartPoints());
    }

    private void renderChart(List<ChartPoint> points) {
        if (binding == null || points == null) return;

        View[] bars = new View[] {
                binding.chartBar1,
                binding.chartBar2,
                binding.chartBar3,
                binding.chartBar4,
                binding.chartBar5,
                binding.chartBar6,
                binding.chartBar7
        };

        TextView[] labels = new TextView[] {
                binding.chartLabel1,
                binding.chartLabel2,
                binding.chartLabel3,
                binding.chartLabel4,
                binding.chartLabel5,
                binding.chartLabel6,
                binding.chartLabel7
        };

        TextView[] values = new TextView[] {
                binding.chartValue1,
                binding.chartValue2,
                binding.chartValue3,
                binding.chartValue4,
                binding.chartValue5,
                binding.chartValue6,
                binding.chartValue7
        };

        int count = Math.min(points.size(), bars.length);
        for (int i = 0; i < count; i++) {
            ChartPoint point = points.get(i);
            labels[i].setText(point.getLabel());
            values[i].setText(String.valueOf(point.getCount()));
            setBarHeight(bars[i], point.getValue());
        }

        for (int i = count; i < bars.length; i++) {
            values[i].setText("0");
            setBarHeight(bars[i], 0);
        }
    }

    private void setBarHeight(View bar, int value) {
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        if (!(params instanceof LinearLayout.LayoutParams)) return;

        int minHeight = dpToPx(12);
        int maxHeight = dpToPx(92);
        int safeValue = Math.max(0, Math.min(100, value));
        params.height = minHeight + ((maxHeight - minHeight) * safeValue / 100);
        bar.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                requireContext().getResources().getDisplayMetrics()
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
