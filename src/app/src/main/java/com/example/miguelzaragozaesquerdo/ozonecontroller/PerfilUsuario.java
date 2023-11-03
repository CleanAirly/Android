package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase que representa la pantalla de perfil de usuario.
 * Autor: Mario Merenciano
 */
public class PerfilUsuario extends AppCompatActivity {

    private TextView nombreUsuario;
    private TextView emailUsuario;
    private TextView telfUsuario;
    private TextView restablecerContrasenya;
    private DatosUsuario datosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario_activity);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");

        nombreUsuario = findViewById(R.id.txtNombrePerfil);
        emailUsuario = findViewById(R.id.txtEmailPerfil);
        telfUsuario = findViewById(R.id.txtNumeroPerfil);
        restablecerContrasenya = findViewById(R.id.txtRestablecerPerfil);
        restablecerContrasenya.setPaintFlags(restablecerContrasenya.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        nombreUsuario.setText(datosUsuario.getNombre());
        emailUsuario.setText(datosUsuario.getEmail());
    }

    /**
     * Maneja el evento de edición de perfil.
     */
    public void botonPerfilEditarPerfil(View view) {
        Intent intent = new Intent(this, PerfilUsuarioEditar.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Maneja el evento de regreso a la pantalla de inicio.
     */
    public void botonPerfilLanding(View view) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Maneja el evento de cierre de sesión.
     */
    public void botonCerrarSesion(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}