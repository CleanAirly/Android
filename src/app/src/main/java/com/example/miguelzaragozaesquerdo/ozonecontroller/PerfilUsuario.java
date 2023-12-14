package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase que representa la pantalla de perfil de usuario.
 * Autor: Mario Merenciano
 */
public class PerfilUsuario extends AppCompatActivity {
    private static final String RUTA = "192.168.1.47";
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
        restablecerContrasenya = findViewById(R.id.txtRestablecerPass);
        restablecerContrasenya.setPaintFlags(restablecerContrasenya.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        nombreUsuario.setText(datosUsuario.getNombre());
        emailUsuario.setText(datosUsuario.getEmail());
        telfUsuario.setText(datosUsuario.getTelefono());

        restablecerContrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://"+RUTA+"/biometriaSprint2/src/restablecerContrasenya.html";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
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
    public void botonCerrarSesion(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesion();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Cierra la sesión del usuario actual.
     * Esta función realiza las siguientes acciones:
     * 1. Accede a las preferencias compartidas para la autenticación.
     * 2. Elimina el token de autenticación almacenado.
     * 3. Aplica los cambios en las preferencias compartidas.
     * 4. Inicia una nueva actividad de inicio de sesión.
     * Nota: Al llamar a esta función, el usuario actual se desconectará y será redirigido
     * a la pantalla de inicio de sesión.
     */
    private void cerrarSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}