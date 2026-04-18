package com.example.msi;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.msi.ui.config.ConfigFragment;
import com.example.msi.ui.dispositivos.DispositivosFragment;
import com.example.msi.ui.inicio.InicioFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView navegação = findViewById(R.id.bottomNavigationView);

        if (navegação != null) {
            navegação.setOnItemSelectedListener(item -> {
                int idItem = item.getItemId();
                Fragment fragmentoSelecionado = null;

                if (idItem == R.id.nav_inicio) {
                    fragmentoSelecionado = new InicioFragment();
                } else if (idItem == R.id.nav_dispositivos) {
                    fragmentoSelecionado = new DispositivosFragment();
                } else if (idItem == R.id.nav_config) {
                    fragmentoSelecionado = new ConfigFragment();
                }

                if (fragmentoSelecionado != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, fragmentoSelecionado)
                            .commit();
                    return true;
                }
                return false;
            });
            if (savedInstanceState == null) {
                navegação.setSelectedItemId(R.id.nav_inicio);
            }
        }
    }
}
