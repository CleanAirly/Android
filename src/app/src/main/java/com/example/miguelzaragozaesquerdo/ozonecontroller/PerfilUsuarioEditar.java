package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuarioEditar extends AppCompatActivity {

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
        String nuevoNombre = editarNombre.getText().toString();
        String nuevoTlf = editarTelefono.getText().toString();
        Log.d("TEST - BOTON", "1");
        if(nuevoNombre.equals("") && nuevoTlf.equals("")){
            txtErrorNombre.setVisibility(View.VISIBLE);
            txtErrorTelf.setVisibility(View.VISIBLE);
        } else if(nuevoNombre.equals("")){
            txtErrorNombre.setVisibility(View.VISIBLE);
        } else if(nuevoTlf.equals("")){
            txtErrorTelf.setVisibility(View.VISIBLE);
        } else {
            Log.d("TEST - BOTON", "2");
            guardarDatos(nuevoNombre, nuevoTlf);
        }
    }

    public void botonVolverAtras(View view){
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    private void guardarDatos(String nombre, String telefono){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("PUT", "http://192.168.1.47:3001/api/sensor/usuarioUpdate",
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