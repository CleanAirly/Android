package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuarioEditar extends AppCompatActivity {

    private static final String RUTA = "192.168.136.129";
    private EditText editarNombre;
    private TextView editarEmail;
    private EditText editarTelefono;
    private TextView txtErrorNombre;
    private TextView txtErrorEmail;
    private TextView txtErrorTelf;
    private DatosUsuario datosUsuario;

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

        txtErrorNombre.setVisibility(View.INVISIBLE);
        txtErrorTelf.setVisibility(View.INVISIBLE);

        editarNombre.setHint(datosUsuario.getNombre());
        editarTelefono.setHint(datosUsuario.getTelefono());

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

    public void botonVolverAtras(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Descartar Cambios")
                .setMessage("¿Estás seguro de que quieres descartar los cambios?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PerfilUsuarioEditar.this, PerfilUsuario.class);
                        intent.putExtra("datosUsuario", datosUsuario);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void guardarDatos(String nombre, String telefono){

        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("PUT", "http://" + RUTA + ":3001/api/sensor/usuarioUpdate",
                "{\"email\": \"" + datosUsuario.getEmail() + "\", \"nombre\": \"" + nombre + "\", \"telefono\": \"" + telefono + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        if(cuerpo.equals("true")){
                            redireccion();
                        } else{
                            Log.d("TEST - ERROR GUARDAR DATOS", ":"+codigo);
                        }
                    }
                });
    }

    private void redireccion(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}