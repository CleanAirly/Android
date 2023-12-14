package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**192
 * Clase que representa la pantalla de inicio de sesión y registro de usuario.
 * Autor: Mario Merenciano
 */
public class Login extends AppCompatActivity {

    private static final String RUTA = "192.168.1.47";
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
    private boolean textoConfContrasenya;
    private DatosUsuario datosUsuario;
    private Button botonIniciar;
    private String codigoVerificacionRegistro;
    private CheckBox checkBoxPrivacidad;
    private TextView txtOlvidadoContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datosUsuario = new DatosUsuario();
        checkLogedUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        switchOnOff = findViewById(R.id.switchLogin);

        botonIniciar = findViewById(R.id.botonAccederLogin);

        txtOlvidadoContrasenya = findViewById(R.id.txtOlvidadoContrasenya);

        tvLogin = findViewById(R.id.txtLogin);
        tvRegistrarse = findViewById(R.id.txtRegistrarse);
        ConfContrasenya = findViewById(R.id.txtConfContrasenya);
        txtErrorContrasenya = findViewById(R.id.txtErrorContrasenyaLogin);
        txtErrorConfContrasenya = findViewById(R.id.txtErrorConfContrasenyaLogin);
        checkBoxPrivacidad = findViewById(R.id.checkBoxPrivacidad);

        InputNombre = findViewById(R.id.nombreUsuarioLogin);
        InputContrasenya = findViewById(R.id.ContrasenyaUsuarioLogin);
        InputConfContrasenya = findViewById(R.id.confirmarContrasenyaUsuarioLogin);

        ConfContrasenya.setVisibility(View.GONE);
        InputConfContrasenya.setVisibility(View.GONE);
        txtErrorContrasenya.setVisibility(View.INVISIBLE);
        txtErrorConfContrasenya.setVisibility(View.GONE);
        checkBoxPrivacidad.setVisibility(View.GONE);

        String texto = "He olvidado mi contraseña";
        SpannableString subrayado = new SpannableString(texto);
        subrayado.setSpan(new UnderlineSpan(), 0, texto.length(), 0);

        txtOlvidadoContrasenya.setText(subrayado);
        txtOlvidadoContrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://"+RUTA+"/Sprint%201/web-sprint2/Web/src/introduccirEmailContr.html";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchOnOff.isChecked()) {
                Log.d("TEST","REGISTRO");
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                ConfContrasenya.startAnimation(fadeIn);
                InputConfContrasenya.startAnimation(fadeIn);
                botonIniciar.setText("CONTINUAR");

                tvLogin.setTextColor(ContextCompat.getColor(this, R.color.colortxt));
                tvRegistrarse.setTextColor(ContextCompat.getColor(this, R.color.colorFondo));
                ConfContrasenya.setVisibility(View.VISIBLE);
                InputConfContrasenya.setVisibility(View.VISIBLE);
                txtErrorContrasenya.setVisibility(View.GONE);
                checkBoxPrivacidad.setVisibility(View.VISIBLE);
                txtOlvidadoContrasenya.setVisibility(View.GONE);
            } else {
                botonIniciar.setText("ACCEDER");
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                ConfContrasenya.startAnimation(fadeOut);
                InputConfContrasenya.startAnimation(fadeOut);

                tvLogin.setTextColor(ContextCompat.getColor(this, R.color.colorFondo));
                tvRegistrarse.setTextColor(ContextCompat.getColor(this, R.color.colortxt));
                ConfContrasenya.setVisibility(View.GONE);
                InputConfContrasenya.setVisibility(View.GONE);
                checkBoxPrivacidad.setVisibility(View.GONE);
                txtOlvidadoContrasenya.setVisibility(View.VISIBLE);
                if(textoContrasenya) txtErrorContrasenya.setVisibility(View.VISIBLE);
            }

            checkBoxPrivacidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        RegistroPopUpPrivacidad dialog = new RegistroPopUpPrivacidad();
                        dialog.show(getSupportFragmentManager(), "RegistroPopUpPrivacidad");
                        txtErrorConfContrasenya.setVisibility(View.GONE);
                    }
                }
            });
        });

        InputNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(textoContrasenya) txtErrorContrasenya.setVisibility(View.GONE);
                if(textoConfContrasenya) {
                    txtErrorConfContrasenya.setVisibility(View.GONE);
                    txtErrorConfContrasenya.setText("Las contraseñas no coinciden");
                }
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
                if(textoConfContrasenya) {
                    txtErrorConfContrasenya.setVisibility(View.GONE);
                    txtErrorConfContrasenya.setText("Las contraseñas no coinciden");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        InputConfContrasenya.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textoConfContrasenya) txtErrorConfContrasenya.setVisibility(View.GONE);
                if(textoContrasenya) {
                    txtErrorContrasenya.setVisibility(View.GONE);
                    txtErrorConfContrasenya.setText("Las contraseñas no coinciden");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Maneja el evento de inicio de sesión o registro.
     */
    public void botonLoginLanding(View view) {
        if(!InputNombre.getText().toString().equals("")|| !InputContrasenya.getText().toString().equals("")){
            String email = InputNombre.getText().toString();
            String password = Utilidades.hashPassword(InputContrasenya.getText().toString());

            // REGISTRO SELECCIONADO
            if(switchOnOff.isChecked()){
               if(InputConfContrasenya.getText().toString().equals("") || !InputConfContrasenya.getText().toString().equals(InputContrasenya.getText().toString())){
                    // CONTRASEÑSA NO COINCIDEN
                   txtErrorConfContrasenya.setText("Las contraseñas deben ser iguales");
                   txtErrorConfContrasenya.setVisibility(View.VISIBLE);
                   textoConfContrasenya = true;
                } else if(!checkBoxPrivacidad.isChecked()){
                    txtErrorConfContrasenya.setText("Acepte la Política de privacidad");
                   txtErrorConfContrasenya.setVisibility(View.VISIBLE);
                   textoConfContrasenya = true;
                } else {
                   PeticionarioREST elPeticionario = new PeticionarioREST();
                   elPeticionario.hacerPeticionREST("POST", "http://" + RUTA + ":3001/api/sensor/registrate",
                           "{\"email\": \"" + InputNombre.getText().toString() + "\", \"verificacion\": \"" + true + "\"}",
                           new PeticionarioREST.RespuestaREST () {
                               @Override
                               public void callback(int codigo, String cuerpo) {

                                   // SI EL CORREO EXISTE EN LA BASE DE DATOS
                                   if(cuerpo.replace("\"", "").equals("existe")){
                                       txtErrorConfContrasenya.setText("Correo intrododucido ya existe");
                                       txtErrorConfContrasenya.setVisibility(View.VISIBLE);
                                   }
                                   // SI NO EXISTE EL CORREO EN LA BASE DE DATOS
                                   else {
                                       Log.d("TEST","ENVIAR CORREO");
                                       codigoVerificacionRegistro = Utilidades.codigoAleatorio();
                                       Utilidades.enviarConGMail(email, "Código de registro", codigoVerificacionRegistro);
                                       intent(codigoVerificacionRegistro, email, password);
                                   }
                               }
                           });
               }
            }

            // LOGIN SELECCIONADO
            else {
                PeticionarioREST elPeticionario = new PeticionarioREST();
                elPeticionario.hacerPeticionREST("POST", "http://" + RUTA + ":3001/api/sensor/login/",
                        "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}",
                        new PeticionarioREST.RespuestaREST () {
                            @Override
                            public void callback(int codigo, String cuerpo) {
                                Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                                if(cuerpo.equals("true")){
                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("auth_token", password);
                                    editor.putString("email", email);
                                    editor.apply();
                                    redireccion(email);
                                } else if(cuerpo.equals("false")){
                                    txtErrorContrasenya.setText("Credenciales incorrectas");
                                    cambiarVisibilidad();
                                }
                            }
                        });
            }
        }
        else{
            if(switchOnOff.isChecked()){
                txtErrorConfContrasenya.setText("Rellene los campos");
                txtErrorConfContrasenya.setVisibility(View.VISIBLE);
                textoConfContrasenya = true;
            } else {
                txtErrorContrasenya.setText("Rellene los campos");
                cambiarVisibilidad();
            }

        }
    }

    /**
     * Inicia una nueva actividad para el registro de usuario.
     * Este método crea un Intent para hacer la transición a la clase RegistroActivity.
     * Pasa datos adicionales necesarios para el proceso de registro, incluyendo un código
     * de verificación, el correo electrónico del usuario y la contraseña.
     *
     * @param codigo    El código de verificación necesario para el registro.
     * @param email     El correo electrónico del usuario.
     * @param password  La contraseña del usuario.
     */
    private void intent(String codigo, String email, String password){
        Intent intent = new Intent(this, RegistroActivity.class);
        intent.putExtra("codigoVerificacionRegistro", codigo);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    /**
     * Verifica si el usuario ya ha iniciado sesión previamente y redirige a la pantalla de inicio.
     */
    public void checkLogedUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginAuth", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);
        if(authToken != null){
            redireccion(sharedPreferences.getString("email", null));
        }
    }

    /**
     * Redirige a la pantalla de inicio con los datos del usuario.
     * @param email El correo electrónico del usuario.
     */
    public void redireccion(String email){
        Intent intent = new Intent(this, Home.class);
        datosUsuario.setEmail(email);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Cambia la visibilidad de un mensaje de error.
     */
    private void cambiarVisibilidad(){
        txtErrorContrasenya.setVisibility(View.VISIBLE);
        textoContrasenya = true;
    }
}