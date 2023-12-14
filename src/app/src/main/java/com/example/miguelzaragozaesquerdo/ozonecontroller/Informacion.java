package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * La clase Informacion extiende AppCompatActivity y representa la pantalla de información del usuario.
 * Esta actividad muestra detalles específicos sobre el usuario, como su perfil y otra información relevante.
 * La información del usuario se recibe a través de un Intent, que se procesa en el método onCreate.
 * Además de mostrar información, esta clase también gestiona eventos de clic en botones para la navegación
 * entre diferentes actividades de la aplicación.
 *
 * @see DatosUsuario Clase que representa la información del usuario.
 * @see Home Actividad principal de la aplicación.
 * @see PerfilUsuario Actividad que muestra el perfil detallado del usuario.
 */
public class Informacion extends AppCompatActivity {

    private DatosUsuario datosUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_activity);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");
    }

    /**
     * Maneja el evento de clic en el botón para volver a la pantalla principal (Home).
     * Este método crea un Intent para navegar desde la actividad actual a la actividad Home.
     * Además, pasa información adicional del usuario a la actividad Home.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void botonVolverHome(View view) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Maneja el evento de clic en el botón para navegar a la pantalla de perfil del usuario.
     * Este método crea un Intent para transitar desde la actividad actual a la actividad PerfilUsuario.
     * Además, transfiere información adicional del usuario a la actividad PerfilUsuario.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void botonLandingPerfil(View view) {
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }
}