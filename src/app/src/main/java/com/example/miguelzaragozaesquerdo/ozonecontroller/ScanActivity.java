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

    /**
     * Se llama cuando la actividad entra en el estado de "Reanudar" (Resume).
     * Este método se utiliza para reanudar operaciones que se pausaron o detuvieron en 'onPause',
     * como la cámara en este caso. Se configura el manejador de resultados del escáner (scannerView)
     * y se inicia la cámara para continuar con el escaneo de códigos QR.
     */
    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    /**
     * Se llama cuando la actividad entra en el estado de "Pausa" (Pause).
     * Este método es donde se liberan los recursos o se pausan operaciones que no deben continuar
     * (o consumir recursos) mientras la Actividad no está en primer plano, como la cámara en este caso.
     * Aquí, se detiene la cámara del escáner para ahorrar recursos y evitar el uso de la cámara en segundo plano.
     */
    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    /**
     * Maneja el resultado del escaneo del código QR.
     * Este método se activa cuando se escanea un código QR. Muestra un mensaje con el resultado del escaneo
     * y llama a 'crearUsuarioBaseDatos' para almacenar los datos del usuario junto con el resultado del escaneo en la base de datos.
     * Luego, se puede implementar lógica adicional para procesar o utilizar el resultado del escaneo.
     * Finalmente, reanuda la cámara para más escaneos.
     *
     * @param rawResult El resultado del escaneo del código QR.
     */
    @Override
    public void handleResult(Result rawResult) {
        // Aquí obtienes el resultado del escaneo (rawResult.getText()).
        Toast.makeText(this, "Código QR escaneado: " + rawResult.getText(), Toast.LENGTH_SHORT).show();

        crearUsuarioBaseDatos(datosUsuario.getEmail(), datosUsuario.getPassword(), datosUsuario.getNombre(), datosUsuario.getTelefono(), rawResult.getText());

        // Puedes enviar el resultado a la base de datos aquí.
        // Implementa la lógica para enviar la información a la base de datos.

        // Una vez que hayas procesado el resultado, reanuda la cámara para más escaneos.
    }

    /**
     * Inicia la cámara para escanear códigos QR.
     * Este método configura y pone en marcha la cámara para el escaneo de códigos QR,
     * estableciendo el enfoque automático y asignando el manejador de resultados.
     */
    private void startCamera() {
        // Inicia la cámara para escanear códigos QR.
        scannerView.setAutoFocus(true);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    /**
     * Maneja los resultados de la solicitud de permisos.
     * Este método se llama cuando el usuario responde a la solicitud de permisos.
     * Si el permiso de la cámara es concedido, inicia la cámara. En caso contrario,
     * se puede mostrar un mensaje al usuario o tomar otras medidas.
     *
     * @param requestCode  El código de solicitud de permisos.
     * @param permissions  Los permisos solicitados.
     * @param grantResults Los resultados de las solicitudes de permisos.
     */
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

    /**
     * Crea un nuevo usuario en la base de datos.
     * Este método prepara y envía una solicitud POST al servidor para registrar un nuevo usuario.
     * Toma los datos del usuario, incluyendo email, contraseña, nombre, teléfono y código del sensor,
     * y los convierte en un objeto JSON. Luego, envía este objeto JSON al servidor a través de una petición REST.
     * La respuesta del servidor se maneja mediante un callback, donde se verifica si el registro fue exitoso
     * y se toman las acciones correspondientes.
     *
     * @param email        El correo electrónico del usuario.
     * @param password     La contraseña del usuario.
     * @param nombre       El nombre del usuario.
     * @param telefono     El número de teléfono del usuario.
     * @param codigoSensor El código del sensor asociado al usuario.
     */
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
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/registrate", jsonString,
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

    /**
     * Realiza una petición de inicio de sesión al servidor.
     * Este método envía una solicitud POST al servidor para autenticar al usuario utilizando su correo electrónico y contraseña.
     * La información de autenticación se envía en formato JSON. Tras recibir la respuesta del servidor, se verifica si la autenticación fue exitosa.
     * En caso afirmativo, se almacena el token de autenticación y el correo electrónico en SharedPreferences y se redirige al usuario a otra actividad.
     * Si la autenticación falla, se registra un mensaje de error.
     *
     * @param email    El correo electrónico del usuario para el inicio de sesión.
     * @param password La contraseña del usuario para el inicio de sesión.
     */
    private void intent(String email, String password){
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

    /**
     * Redirige al usuario a la pantalla principal (Home) de la aplicación.
     * Este método crea un Intent para cambiar de la actividad actual a la actividad Home.
     * Antes de iniciar la actividad, establece el correo electrónico del usuario en el objeto 'datosUsuario'
     * y pasa este objeto a la actividad Home a través del Intent.
     * Esto asegura que la siguiente actividad tenga acceso a los datos relevantes del usuario, como su email.
     *
     * @param email El correo electrónico del usuario, que será pasado a la actividad Home.
     */
    public void redireccion(String email){
        Intent intent = new Intent(this, Home.class);
        datosUsuario.setEmail(email);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }
}