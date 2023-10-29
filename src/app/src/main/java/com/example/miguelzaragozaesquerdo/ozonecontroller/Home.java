package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    ProgressBar progressBar;
    TextView saludoUsuario;
    TextView valorPpm;
    TextView estadoAire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(50);

        saludoUsuario = findViewById(R.id.txtNombreLanding);
        valorPpm = findViewById(R.id.txtPpmLanding);
        estadoAire = findViewById(R.id.txtEstadoAireLanding);
    }

    public void botonLandingPerfil(View view) {
        Intent intent = new Intent(this, PerfilUsuario.class);
        startActivity(intent);
    }

}
