package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

public class Home extends AppCompatActivity {

    ProgressBar progressBar;
    TextView saludoUsuario;
    TextView valorPpm;
    TextView estadoAire;

    private DatosUsuario datosUsuario;

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(50);

        saludoUsuario = findViewById(R.id.txtNombreLanding);
        valorPpm = findViewById(R.id.txtPpmLanding);
        estadoAire = findViewById(R.id.txtEstadoAireLanding);

        if(datosUsuario.getNombre() == null){
            obtenerDatosUsuario(datosUsuario.getEmail());
        } else {
            saludo(datosUsuario.getNombre());
        }

        obtenerUltimaMedicion(datosUsuario.getEmail());
        actualizarUltimaMedicion(5000);
    }

    public void botonLandingPerfil(View view) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    public void obtenerDatosUsuario(String email){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/usuario",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        if(cuerpo != null){
                            String respuesta = cuerpo.replace("\"", "");
                            datosUsuario.setNombre(respuesta);
                            saludo(respuesta);
                        } else{
                            Log.d("TEST - NOMBRE", "ERROR AL OBTENER");
                            datosUsuario.setNombre("Error al obtener");
                        }
                    }
                });
    }

    private void actualizarUltimaMedicion(final long intervaloMillis){
        runnable = new Runnable() {
            @Override
            public void run() {
                obtenerUltimaMedicion(datosUsuario.getEmail());

                // Vuelve a programar la ejecución después del intervalo
                handler.postDelayed(this, intervaloMillis);
            }
        };

        // Inicia la primera ejecución después del intervalo
        handler.postDelayed(runnable, intervaloMillis);
    }

    private void obtenerUltimaMedicion(String email){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/medida",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        if(cuerpo != null){
                            Log.d("TEST - MEDICION", ": "+cuerpo);
                            DatosMedicion datosMedicion;
                            Gson gson = new Gson();
                            datosMedicion  = gson.fromJson(cuerpo, DatosMedicion.class);
                            actualizarTextoMedicion(String.valueOf(datosMedicion.getValor()));
                        } else{
                            Log.d("TEST - MEDICION", "ERROR");
                        }
                    }
                });
    }

    public void saludo(String texto){
        saludoUsuario.setText("¡Bienvenido "+texto+"!");
    }

    public void actualizarTextoMedicion(String nuevoValor){
        valorPpm.setText(nuevoValor);
    }
}