package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class testRegistro extends AppCompatActivity {

    EditText inputCodigoRegistro;
    private String codigoVerificacionRegistro;
    private DatosUsuario datosUsuario;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_registro);

        Intent intent = getIntent();
        codigoVerificacionRegistro = (String) intent.getSerializableExtra("codigoVerificacionRegistro");
        inputCodigoRegistro = findViewById(R.id.inputCodigoRegistro);
    }

    public void botonAceptarRegistro(View view){
        if(codigoVerificacionRegistro.equals(inputCodigoRegistro.getText().toString())){
            Log.d("TEST - REGISTRO", "VERIFICADO");

            // REALIZAR POST EN LA BASE DE DATOS
            insertarUsuarioBD(datosUsuario.getEmail(), datosUsuario.getPassword(), datosUsuario.getNombre(), datosUsuario.getTelefono());

        } else{
            Log.d("TEST - REGISTRO", "CODIGO INCORRECTO");
        }
    }

    private void insertarUsuarioBD(String email, String password, String nombre, String telefono){

    }
}
