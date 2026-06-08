package com.example.msi.ui.dispositivos;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.msi.databinding.FragmentDispositivosBinding;
import com.example.msi.R;

public final class DispositivosFragment extends Fragment {
    private FragmentDispositivosBinding binding;

    public DispositivosFragment() {
        super(R.layout.fragment_dispositivos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentDispositivosBinding.bind(view);

        DispositivosViewModel vm = new ViewModelProvider(this).get(DispositivosViewModel.class);
        vm.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == null || binding == null) return;
            binding.textStatusPulseiraPrincipal.setText(state.getMensagem());
        });

        binding.buttonLedPulseiraPrincipal.setOnClickListener(v -> vm.enviarLed());
        binding.buttonVibrarPulseiraPrincipal.setOnClickListener(v -> vm.enviarVibrar());
        binding.buttonLixeira.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Botao ainda nao ligado", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
