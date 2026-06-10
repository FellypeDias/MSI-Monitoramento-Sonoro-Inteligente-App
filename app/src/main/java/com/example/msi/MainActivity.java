package com.example.msi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import com.example.msi.ui.config.ConfigFragment;
import com.example.msi.ui.dispositivos.DispositivosFragment;
import com.example.msi.ui.inicio.InicioFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_POST_NOTIFICATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configuração para ocultar as barras de navegação (Imersivo)
        hideSystemUI();

        requestNotificationPermissionIfNeeded();

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

    private void hideSystemUI() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        // Configura o comportamento para "esconder" ao interagir
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        
        // Oculta a barra de navegação (voltar, home, recentes)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        
        // Se quiser ocultar a de status (bateria, hora) também descomente a linha abaixo:
        // windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        requestPermissions(
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                REQUEST_POST_NOTIFICATIONS
        );
    }
}
