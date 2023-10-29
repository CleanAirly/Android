package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuario extends AppCompatActivity {

    private TextView nombreUsuario;
    private TextView emailUsuario;
    private TextView telfUsuario;
    private TextView restablecerContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario_activity);

        nombreUsuario = findViewById(R.id.txtNombrePerfil);
        emailUsuario = findViewById(R.id.txtEmailPerfil);
        telfUsuario = findViewById(R.id.txtNumeroPerfil);
        restablecerContrasenya = findViewById(R.id.txtRestablecerPerfil);
        restablecerContrasenya.setPaintFlags(restablecerContrasenya.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

    }

    public void botonPerfilEditarPerfil(View view) {
        Intent intent = new Intent(this, PerfilUsuarioEditar.class);
        startActivity(intent);
    }
    public void botonPerfilLanding(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
