package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RegistroActivity extends AppCompatActivity {

    // Declaro todas las variables que tenemos en el xml
    public TextView textoBienvenida;
    public TextView textoVamosAEmpezar;
    public TextView textoIntroduceTuNombre;
    public EditText inputNombre;
    public TextView textoGenial;
    public TextView textoIntroduceTuTelefono;
    public EditText inputTelefono;
    public TextView textoEnlazaSensor;
    public Button btnGuardarNombre;
    public Button btnGuardarTelefono;
    private Button btnFinalizarRegistro;

    // DATOS DEL USUARIO
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String codigoSensor;


    // CODIGO
    private TextView textoCodigo;
    private EditText inputCodigo;
    private Button btnContinuarCodigo;
    private Button btnReenviarCodigo;
    private Button btnVolverCodigo;
    private String codigoVerificacionRegistro;

    // OBJETO USUARIO
    private DatosUsuario datosUsuario;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        datosUsuario = new DatosUsuario();

        Intent intent = getIntent();
        codigoVerificacionRegistro = (String) intent.getSerializableExtra("codigoVerificacionRegistro");
        email = (String) intent.getSerializableExtra("email");
        password = (String) intent.getSerializableExtra("password");

        // Variables para los degradados y la animación
        // Declaro los fadeIn y fadeOut para que el texto no aparezca bruscamente
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);
        fadeIn.setFillBefore(true);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);

        AnimationSet animationIn = new AnimationSet(false);
        animationIn.addAnimation(fadeIn);

        AnimationSet animationOut = new AnimationSet(true);
        animationOut.addAnimation(fadeOut);

        // Enlazo todas las variables creadas con las declaraciones del xml
        textoBienvenida = findViewById(R.id.texto_bienvenido);
        textoVamosAEmpezar = findViewById(R.id.texto_vamos_a_empezar);
        textoIntroduceTuNombre = findViewById(R.id.texto_introduce_tu_nombre);
        inputNombre = findViewById(R.id.input_nombre);
        textoGenial = findViewById(R.id.texto_genial);
        textoIntroduceTuTelefono = findViewById(R.id.texto_introduce_tu_numero_de_teléfono);
        inputTelefono = findViewById(R.id.input_telefono);
        textoEnlazaSensor = findViewById(R.id.texto_enlaza_sensor);
        btnGuardarNombre = findViewById(R.id.btnNombre);
        btnGuardarTelefono = findViewById(R.id.btnTelefono);
        btnFinalizarRegistro = findViewById(R.id.btnFinRegistro);

        // CODIGO
        inputCodigo = findViewById(R.id.input_CodigoVerificar);
        textoCodigo = findViewById(R.id.texto_CodigoVerificar);
        btnContinuarCodigo = findViewById(R.id.btnContinuarCodigoVerificar);
        btnVolverCodigo = findViewById(R.id.btnVolverCodigoVerificar);
        btnReenviarCodigo = findViewById(R.id.btnReenviarCodigoVerificar);

        //Creo una lista con todos los text views
        List<TextView> elementosTextView = new ArrayList<>(Arrays.asList(
                textoBienvenida,
                textoVamosAEmpezar,
                textoIntroduceTuNombre,
                textoGenial,
                textoIntroduceTuTelefono,
                textoEnlazaSensor,
                textoCodigo
        ));

        // Creo una lista con todos los botones
        List<Button> elementosButtons = new ArrayList<>(Arrays.asList(
                btnGuardarNombre,
                btnGuardarTelefono,
                btnContinuarCodigo,
                btnReenviarCodigo,
                btnVolverCodigo,
                btnFinalizarRegistro
        ));

        // Creo una lista con todos los edit text
        List<EditText> elementosEditText = new ArrayList<>(Arrays.asList(
                inputNombre,
                inputTelefono,
                inputCodigo
        ));

        // Coloco en la parte de abajo todos los elementos dependiendo de su tipo
        colocarTextViewsEnlaParteDeAbajo(elementosTextView);
        colocarButtonsEnlaParteDeAbajo(elementosButtons);
        colocarEditTextEnLaParteDeAbajo(elementosEditText);

        // Inicio el proceso de registro
        animacionCodigoVerificacion();
    }

    private void animacionBienvenidaNombre(){
        Handler A = new Handler();
        A.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoBienvenida, 0);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                despedirTexto(textoBienvenida);
                mostrarTexto(textoIntroduceTuNombre, -200);
            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputNombre, 0);
                mostrarButton(btnGuardarNombre, 300);
            }
        }, 5500);
    }

    public void guardarNombre(View view){
        if(!inputNombre.getText().toString().equals("")){
            nombre = inputNombre.getText().toString();
            despedirTexto(textoIntroduceTuNombre);
            despedirEditText(inputNombre);
            despedirButton(btnGuardarNombre);
            animacionTelefono();
        }
    }

    private void animacionCodigoVerificacion(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoCodigo, -200);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputCodigo, 0);
                mostrarButton(btnContinuarCodigo, 300);
                mostrarButton(btnReenviarCodigo, 300);
                mostrarButton(btnVolverCodigo, 300);
            }
        }, 2000);
    }

    public void verificarCodigo(View view){
        if(inputCodigo.getText().toString().equals(codigoVerificacionRegistro)){
            despedirTexto(textoCodigo);
            despedirEditText(inputCodigo);
            despedirButton(btnContinuarCodigo);
            despedirButton(btnVolverCodigo);
            despedirButton(btnReenviarCodigo);
            animacionBienvenidaNombre();
        }
    }

    public void reenviarCodigo(View view){
        codigoVerificacionRegistro = Utilidades.codigoAleatorio();
        Utilidades.enviarConGMail(email, "Completa tu registro", "CleanAirly - Tu código de registro es "+codigoVerificacionRegistro+". Introducelo para comprobar que este es tu correo.");
    }

    private void animacionTelefono(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoIntroduceTuTelefono, -200);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputTelefono, 0);
                mostrarButton(btnGuardarTelefono, 300);
            }
        }, 2000);
    }

    public void guardarTelefono(View view){
        if(!inputTelefono.getText().toString().equals("")){
            despedirTexto(textoIntroduceTuTelefono);
            despedirEditText(inputTelefono);
            despedirButton(btnGuardarTelefono);
            telefono = inputTelefono.getText().toString();
            animacionCodigoSensor();
        }
    }

    private void animacionCodigoSensor(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoEnlazaSensor, 0);
            }
        }, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarButton(btnFinalizarRegistro, 300);
            }
        }, 1000);
    }

    public void crearUsuarioBaseDatos(View view){
        codigoSensor = "12345";
        JSONObject jsonObject = new JSONObject();
        String jsonString = "";
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("nombre", nombre);
            jsonObject.put("telefono", telefono);
            jsonObject.put("idSonda", codigoSensor);
            jsonObject.put("verificacion", false);
            jsonString = jsonObject.toString();
        } catch (JSONException e) { e.printStackTrace(); }
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/registrate", jsonString,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - FIN REGISTRO", codigo+" "+cuerpo);
                        // SI EL REGISTRO FINALIZO CORRECTAMENTE
                        if(cuerpo.equals("true")){
                            Log.d("TEST - FIN REGISTRO", "EXITOSO");
                            intent();
                        }
                        // SI FALLO ALGO AL INSERTAR EL NUEVO USUARIO
                        else {
                            Log.d("TEST - FIN REGISTRO","ALGO HA FALLADO");
                        }
                    }
                });
    }

    private void intent(){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/login/",
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
                            Log.d("TEST - INICIO POST REGISTRO", "ALGO SALIO MAL");
                        }
                    }
                });
    }

    public void redireccion(String email){
        Intent intent = new Intent(this, Home.class);
        datosUsuario.setEmail(email);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    // Creo una función para colocar el texto en la parte de abajo de la pantalla
    private void colocarTextViewsEnlaParteDeAbajo(List<TextView> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    // Función para colocar todos los botones en la parte de abajo
    private void colocarButtonsEnlaParteDeAbajo(List<Button> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    // Coloco los Edit Text en la parte de abajo
    private void colocarEditTextEnLaParteDeAbajo(List<EditText> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    // Función para subir un TextView
    private void mostrarTexto(TextView elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void despedirTexto(TextView elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }

    // Función para subir un EditText
    private void mostrarEditText(EditText elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void despedirEditText(EditText elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }

    // Función para subir un EditText
    private void mostrarButton(Button elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void despedirButton(Button elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }
}
