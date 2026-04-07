package com.example.msi;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new InicioFragment())
                    .commit();
        }

        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        if (nav != null) {
            nav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, new InicioFragment())
                            .commit();
                    return true;
                } else if (id == R.id.nav_dispositivos) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, new DispositivosFragment())
                            .commit();
                    return true;
                } else if (id == R.id.nav_config) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, new ConfigFragment())
                            .commit();
                    return true;
                }
                return false;
            });
        }
    }
}
