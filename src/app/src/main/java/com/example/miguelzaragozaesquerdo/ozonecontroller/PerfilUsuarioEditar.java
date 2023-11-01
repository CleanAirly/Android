package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class PerfilUsuarioEditar extends AppCompatActivity {

    private EditText editarNombre;
    private EditText editarEmail;
    private EditText editarTelefono;
    private TextView txtErrorNombre;
    private TextView txtErrorEmail;
    private TextView txtErrorTelf;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario_editar_activity);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        editarNombre = findViewById(R.id.nombreEditarPerfil);
        editarEmail = findViewById(R.id.emailEditarPerfil);
        editarTelefono = findViewById(R.id.telefonoEditarPerfil);

        txtErrorNombre = findViewById(R.id.txtErrorNombreEditarPerfil);
        txtErrorEmail = findViewById(R.id.txtErrorEmailEditarPerfil);
        txtErrorTelf = findViewById(R.id.txtErrorTelfEditarPerfil);

        txtErrorNombre.setVisibility(View.INVISIBLE);
        txtErrorEmail.setVisibility(View.INVISIBLE);
        txtErrorTelf.setVisibility(View.INVISIBLE);
    }

    public void botonEditarPerfilPerfil(View view) {
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}