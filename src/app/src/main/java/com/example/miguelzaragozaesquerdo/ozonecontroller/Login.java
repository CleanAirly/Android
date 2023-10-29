package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class Login extends AppCompatActivity {

    private SwitchCompat switchOnOff;

    private TextView tvLogin;
    private TextView tvRegistrarse;
    private TextView ConfContrasenya;
    private TextView txtErrorNombre;
    private TextView txtErrorContrasenya;
    private TextView txtErrorConfContrasenya;

    private EditText InputNombre;
    private EditText InputContrasenya;
    private EditText InputConfContrasenya;

    private Servicios servicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        servicios = new Servicios();

        switchOnOff = findViewById(R.id.switchLogin);

        tvLogin = findViewById(R.id.txtLogin);
        tvRegistrarse = findViewById(R.id.txtRegistrarse);
        ConfContrasenya = findViewById(R.id.txtConfContrasenya);
        txtErrorNombre = findViewById(R.id.txtErrorNombreLogin);
        txtErrorContrasenya = findViewById(R.id.txtErrorContrasenyaLogin);
        txtErrorConfContrasenya = findViewById(R.id.txtErrorConfContrasenyaLogin);

        InputNombre = findViewById(R.id.nombreUsuarioLogin);
        InputContrasenya = findViewById(R.id.ContrasenyaUsuarioLogin);
        InputConfContrasenya = findViewById(R.id.confirmarContrasenyaUsuarioLogin);

        ConfContrasenya.setVisibility(View.GONE);
        InputConfContrasenya.setVisibility(View.GONE);
        txtErrorNombre.setVisibility(View.INVISIBLE);
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
            } else {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                ConfContrasenya.startAnimation(fadeOut);
                InputConfContrasenya.startAnimation(fadeOut);

                tvLogin.setTextColor(ContextCompat.getColor(this, R.color.colorFondo));
                tvRegistrarse.setTextColor(ContextCompat.getColor(this, R.color.colortxt));
                ConfContrasenya.setVisibility(View.GONE);
                InputConfContrasenya.setVisibility(View.GONE);
            }
        });
    }

    public void botonLoginLanding(View view) {
        String username = InputNombre.getText().toString();
        String password = InputContrasenya.getText().toString();

        if(username.equals("") || password.equals("")){
            Log.d("TEST - VACIO", "");
        }
        else{
            servicios.login(username, password);
        }

        //Intent intent = new Intent(this, Home.class);
        //startActivity(intent);
    }
}
