package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    private SwitchCompat switchOnOff;

    private TextView tvLogin;
    private TextView tvRegistrarse;
    private TextView ConfContrasenya;
    private TextView txtErrorContrasenya;
    private TextView txtErrorConfContrasenya;

    private EditText InputNombre;
    private EditText InputContrasenya;
    private EditText InputConfContrasenya;

    private boolean textoContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        checkLogedUser();

        switchOnOff = findViewById(R.id.switchLogin);

        tvLogin = findViewById(R.id.txtLogin);
        tvRegistrarse = findViewById(R.id.txtRegistrarse);
        ConfContrasenya = findViewById(R.id.txtConfContrasenya);
        txtErrorContrasenya = findViewById(R.id.txtErrorContrasenyaLogin);
        txtErrorConfContrasenya = findViewById(R.id.txtErrorConfContrasenyaLogin);

        InputNombre = findViewById(R.id.nombreUsuarioLogin);
        InputContrasenya = findViewById(R.id.ContrasenyaUsuarioLogin);
        InputConfContrasenya = findViewById(R.id.confirmarContrasenyaUsuarioLogin);

        ConfContrasenya.setVisibility(View.GONE);
        InputConfContrasenya.setVisibility(View.GONE);
        txtErrorContrasenya.setVisibility(View.INVISIBLE);
        txtErrorConfContrasenya.setVisibility(View.GONE);

        switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchOnOff.isChecked()) {
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                ConfContrasenya.startAnimation(fadeIn);
                InputConfContrasenya.startAnimation(fadeIn);

                tvLogin.setTextColor(ContextCompat.getColor(this, R.color.colortxt));
                tvRegistrarse.setTextColor(ContextCompat.getColor(this, R.color.colorFondo));
                ConfContrasenya.setVisibility(View.VISIBLE);
                InputConfContrasenya.setVisibility(View.VISIBLE);
                txtErrorContrasenya.setVisibility(View.GONE);
            } else {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                ConfContrasenya.startAnimation(fadeOut);
                InputConfContrasenya.startAnimation(fadeOut);

                tvLogin.setTextColor(ContextCompat.getColor(this, R.color.colorFondo));
                tvRegistrarse.setTextColor(ContextCompat.getColor(this, R.color.colortxt));
                ConfContrasenya.setVisibility(View.GONE);
                InputConfContrasenya.setVisibility(View.GONE);
                if(textoContrasenya) txtErrorContrasenya.setVisibility(View.VISIBLE);
            }
        });

        InputNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(textoContrasenya) txtErrorContrasenya.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        InputContrasenya.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(textoContrasenya) txtErrorContrasenya.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void botonLoginLanding(View view) {
        String username = InputNombre.getText().toString();

        // HASH de la contraseña
        String password = hashPassword(InputContrasenya.getText().toString());

        if(username.equals("") || password.equals("")){
            Log.d("TEST - VACIO", "");
        }
        else{
            PeticionarioREST elPeticionario = new PeticionarioREST();

            elPeticionario.hacerPeticionREST("POST", "http://10.236.40.117:3001/api/sensor/login/",
                    "{\"email\": \"" + username + "\", \"password\": \"" + password + "\"}",
                    new PeticionarioREST.RespuestaREST () {
                        @Override
                        public void callback(int codigo, String cuerpo) {
                            Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                            if(cuerpo.equals("true")){
                                SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("auth_token", "TEST");
                                editor.putString("email", username);
                                editor.apply();
                                redireccion(username);
                            } else if(cuerpo.equals("false")){
                                txtErrorContrasenya.setVisibility(View.VISIBLE);
                                textoContrasenya = true;
                            }
                        }
                    });
        }
    }



    private String hashPassword(String password){
        try{
            // Crea una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convierte la contraseña en un arreglo de bytes
            byte[] passwordBytes = password.getBytes();

            // Calcula el hash
            byte[] hashBytes = digest.digest(passwordBytes);

            // Convierte el hash en una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            Log.d("TEST - HASH", hexString.toString());
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkLogedUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);
        if(authToken != null){
            redireccion(sharedPreferences.getString("email", null));
        } else {
            Log.d("TEST - NO LOGIN", "No hay sesion iniciada");
        }
    }

    public void redireccion(String username){
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
