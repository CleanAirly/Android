package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * La clase PerfilUsuarioEditar extiende AppCompatActivity y representa la pantalla de edición de perfil de usuario.
 * Permite al usuario modificar su nombre y número de teléfono, con validación y confirmación antes de realizar cambios.
 *
 * Esta clase utiliza AlertDialogs para confirmar la acción de guardar o descartar cambios.
 * También realiza peticiones REST para actualizar la información del usuario en el servidor.
 *
 * @see DatosUsuario Clase que representa la información del usuario.
 * @see PeticionarioREST Clase que realiza peticiones REST al servidor.
 * @see android.app.AlertDialog Componente para mostrar mensajes de confirmación.
 * @see PerfilUsuario Actividad que muestra el perfil del usuario.
 * @see Login Actividad de inicio de sesión.
 */
public class PerfilUsuarioEditar extends AppCompatActivity {
    private static final String RUTA = "192.168.1.47";
    private EditText editarNombre;
    private TextView emailUsuario;
    private EditText editarTelefono;
    private TextView txtErrorNombre;
    private TextView txtErrorEmail;
    private TextView txtErrorTelf;
    private DatosUsuario datosUsuario;
    private TextView restablecerContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario_editar_activity);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");

        editarNombre = findViewById(R.id.nombreEditarPerfil);
        editarTelefono = findViewById(R.id.telefonoEditarPerfil);

        txtErrorNombre = findViewById(R.id.txtErrorNombreEditarPerfil);
        txtErrorTelf = findViewById(R.id.txtErrorTelfEditarPerfil);

        restablecerContrasenya = findViewById(R.id.txtRestablecerPass);
        restablecerContrasenya = findViewById(R.id.txtRestablecerPass);
        restablecerContrasenya.setPaintFlags(restablecerContrasenya.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        emailUsuario = findViewById(R.id.txtEmail);

        txtErrorNombre.setVisibility(View.INVISIBLE);
        txtErrorTelf.setVisibility(View.INVISIBLE);

        emailUsuario.setText(datosUsuario.getEmail());
        editarNombre.setText(datosUsuario.getNombre());
        editarTelefono.setText(datosUsuario.getTelefono());

        restablecerContrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://"+RUTA+"/biometriaSprint2/src/restablecerContrasenya.html";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        editarNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtErrorNombre.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editarTelefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtErrorTelf.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Maneja el evento de clic en el botón para guardar cambios en la información del usuario.
     * Muestra un cuadro de diálogo de confirmación antes de realizar la acción.
     * Si el usuario confirma, verifica y guarda los cambios introducidos, o muestra errores si los campos están vacíos.
     *
     * @param view La vista que activa esta función (generalmente un botón).
     *             Se utiliza para cumplir con la firma de un método onClick en el archivo de diseño XML.
     */
    public void botonGuardar(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Cambios")
                .setMessage("¿Estás seguro de que quieres guardar los cambios?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nuevoNombre = editarNombre.getText().toString();
                        String nuevoTlf = editarTelefono.getText().toString();
                        if (nuevoNombre.equals("") && nuevoTlf.equals("")) {
                            txtErrorNombre.setVisibility(View.VISIBLE);
                            txtErrorTelf.setVisibility(View.VISIBLE);
                        } else if (nuevoNombre.equals("")) {
                            txtErrorNombre.setVisibility(View.VISIBLE);
                        } else if (nuevoTlf.equals("")) {
                            txtErrorTelf.setVisibility(View.VISIBLE);
                        } else {
                            guardarDatos(nuevoNombre, nuevoTlf);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Maneja el evento de clic en el botón para volver atrás/descartar cambios en la información del usuario.
     * Muestra un cuadro de diálogo de confirmación antes de realizar la acción.
     * Si el usuario confirma, vuelve a la pantalla de perfil del usuario, descartando los cambios.
     *
     * La vista que activa esta función (generalmente un botón).
     *             Se utiliza para cumplir con la firma de un método onClick en el archivo de diseño XML.
     */
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Descartar Cambios")
                .setMessage("¿Estás seguro de que quieres descartar los cambios?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PerfilUsuarioEditar.this, Home.class);
                        intent.putExtra("datosUsuario", datosUsuario);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Guarda los datos actualizados del usuario en el servidor mediante una petición REST de tipo PUT.
     *
     * @param nombre    El nuevo nombre del usuario.
     * @param telefono  El nuevo número de teléfono del usuario.
     */
    private void guardarDatos(String nombre, String telefono){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("PUT", "http://" + RUTA + ":3001/api/sensor/usuarioUpdate",
                "{\"email\": \"" + datosUsuario.getEmail() + "\", \"nombre\": \"" + nombre + "\", \"telefono\": \"" + telefono + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        if(cuerpo.equals("true")){
                            datosUsuario.setNombre(nombre);
                            datosUsuario.setTelefono(telefono);
                            redireccion();
                        } else{
                            Log.d("TEST - ERROR GUARDAR DATOS", ":"+codigo);
                        }
                    }
                });
    }

    /**
     * Redirige a la pantalla de inicio de sesión después de guardar exitosamente los cambios en la información del usuario.
     * Se llama cuando la petición REST para guardar datos es exitosa.
     */
    private void redireccion(){
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