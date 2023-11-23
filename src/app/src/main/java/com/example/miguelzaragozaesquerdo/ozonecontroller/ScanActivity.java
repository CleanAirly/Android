package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private DatosUsuario datosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso de la cámara ya está concedido, inicia la cámara.
            startCamera();
        } else {
            // Si no, solicita el permiso.
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Aquí obtienes el resultado del escaneo (rawResult.getText()).
        Toast.makeText(this, "Código QR escaneado: " + rawResult.getText(), Toast.LENGTH_SHORT).show();

        crearUsuarioBaseDatos(datosUsuario.getEmail(), datosUsuario.getPassword(), datosUsuario.getNombre(), datosUsuario.getTelefono(), rawResult.getText());

        // Puedes enviar el resultado a la base de datos aquí.
        // Implementa la lógica para enviar la información a la base de datos.

        // Una vez que hayas procesado el resultado, reanuda la cámara para más escaneos.
    }

    private void startCamera() {
        // Inicia la cámara para escanear códigos QR.
        scannerView.setAutoFocus(true);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de la cámara concedido, inicia la cámara.
                startCamera();
            } else {
                // Permiso de la cámara denegado. Puedes mostrar un mensaje o tomar medidas adicionales.
            }
        }
    }

    public void crearUsuarioBaseDatos(String email, String password, String nombre, String telefono, String codigoSensor){
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
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.102:3001/api/sensor/registrate", jsonString,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - FIN REGISTRO", codigo+" "+cuerpo);
                        // SI EL REGISTRO FINALIZO CORRECTAMENTE
                        if(cuerpo.equals("true")){
                            Log.d("TEST - FIN REGISTRO", "EXITOSO");
                            intent(email, password);
                        }
                        // SI FALLO ALGO AL INSERTAR EL NUEVO USUARIO
                        else {
                            Log.d("TEST - FIN REGISTRO","ALGO HA FALLADO");
                        }
                    }
                });
    }

    private void intent(String email, String password){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.102:3001/api/sensor/login/",
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
}
