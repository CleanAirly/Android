package com.example.criborm.cleanairly;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa la pantalla de inicio de la aplicación.
 * Autor: Mario Merenciano
 */
public class Home extends AppCompatActivity {


    TextView saludoUsuario;
    TextView valorPpm;
    TextView estadoAire;

    private DatosUsuario datosUsuario;
    private DatosMedicion datosMedicion;
    private ArrayList<Integer> listaMediciones = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable runnable;

    private LineChart chart;

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Intent intent = getIntent();
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");



        chart = findViewById(R.id.chart);

        Log.d("clienterestandroid", "fin onCreate()");
        // -------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------
        saludoUsuario = findViewById(R.id.txtNombreLanding);
        valorPpm = findViewById(R.id.txtPpmLanding);
        estadoAire = findViewById(R.id.txtEstadoAireLanding);

        if (datosUsuario.getNombre() == null) {
            obtenerDatosUsuario(datosUsuario.getEmail());
        } else {
            saludo(datosUsuario.getNombre());
        }

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
        }

        obtenerUltimaMedicion(datosUsuario.getEmail());

        actualizarUltimaMedicion(10000);
        activarServicio();

    }



     /**
     * Carga datos en un gráfico de línea.
     */

    private void cargarDatosEnGrafica() {
        // Suponiendo que tienes el siguiente JSON:
        obtenerUltimasMediciones(datosUsuario.getEmail(), 6);
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            entries.clear();
            Collections.reverse(listaMediciones);
            int startIndex = Math.max(0, listaMediciones.size() - 6);
                for (int i = startIndex; i < listaMediciones.size(); i++) {
                    entries.add(new Entry(i, listaMediciones.get(i)));
                    Log.d("TAG2", entries.toString());
                }


            // Configuración del dataSet
            LineDataSet dataSet = new LineDataSet(entries, "Nivel de Ozono en el aire");
            dataSet.setColor(Color.parseColor("#A2BCF4"));
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(4f);

            chart.setData(new LineData(dataSet));
            chart.getXAxis().setDrawLabels(false);
            chart.getAxisRight().setDrawLabels(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.getDescription().setEnabled(false);
            chart.invalidate();  // Refrescar la gráfica



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activarServicio(){
        Intent serviceIntent = new Intent(this, MiServicio.class);
        serviceIntent.putExtra("email",datosUsuario.getEmail());
        startService(serviceIntent);


    }
    public void pararServicio(View view){
        Intent intent = new Intent(this, MiServicio.class);

        //Para detener el servicio
        stopService(intent);
    }
    /**
     * Redirige a la pantalla de perfil del usuario.
     */
    public void botonLandingPerfil(View view) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Obtiene los datos del usuario a través de una petición REST.
     * @param email El correo electrónico del usuario.
     */
    public void obtenerDatosUsuario(String email) {
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.102:3001/api/sensor/usuario",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - RESPUESTA", "codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        if (cuerpo != null) {
                            String respuesta = cuerpo.replace("\"", "");
                            datosUsuario.setNombre(respuesta);
                            saludo(respuesta);
                        } else {
                            Log.d("TEST - NOMBRE", "ERROR AL OBTENER");
                            datosUsuario.setNombre("Error al obtener");
                        }
                    }
                });
    }

    /**
     * Actualiza la última medición con un intervalo específico.
     * @param intervaloMillis El intervalo en milisegundos.
     */
    private void actualizarUltimaMedicion(final long intervaloMillis) {
        runnable = new Runnable() {
            @Override
            public void run() {
                cargarDatosEnGrafica();
                obtenerUltimaMedicion(datosUsuario.getEmail());




                // Vuelve a programar la ejecución después del intervalo
                handler.postDelayed(this, intervaloMillis);
            }
        };

        // Inicia la primera ejecución después del intervalo
        handler.postDelayed(runnable, intervaloMillis);
    }

    /**
     * Obtiene la última medición de los sensores a través de una petición REST.
     * @param email El correo electrónico del usuario.
     */

    private void obtenerUltimaMedicion(String email) {
        com.example.criborm.cleanairly.PeticionarioREST elPeticionario = new com.example.criborm.cleanairly.PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.102:3001/api/sensor/medida",
                "{\"email\": \"" + email + "\"}",
                new com.example.criborm.cleanairly.PeticionarioREST.RespuestaREST() {
                    @TargetApi(Build.VERSION_CODES.Q)
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        if (cuerpo != null) {
                            Log.d("TEST - MEDICION", ": " + cuerpo);
                            Gson gson = new Gson();
                            datosMedicion = gson.fromJson(cuerpo, com.example.criborm.cleanairly.DatosMedicion.class);
                            try {
                                actualizarTextoMedicion(String.valueOf(datosMedicion.getValor()));
                                ProgressBar progress = findViewById(R.id.progressBar);
                                progress.setProgress(datosMedicion.getValor());
                                if(datosMedicion.getValor() < 249){
                                    progress.getCurrentDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                                }
                                else if(datosMedicion.getValor() > 249 && datosMedicion.getValor() < 499){
                                    progress.getCurrentDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                                }
                                else if(datosMedicion.getValor() > 499){
                                    progress.getCurrentDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                }
                            } catch (Exception e) {
                                Log.d("TEST - MEDICION", "ERROR: " + e);
                            }
                        } else {
                            Log.d("TEST - MEDICION", "ERROR");
                        }
                    }
                });
    }
    private void obtenerUltimasMediciones(String email, int cantidadMediciones) {
        com.example.criborm.cleanairly.PeticionarioREST elPeticionario = new com.example.criborm.cleanairly.PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.102:3001/api/sensor",
                "{\"email\": \"" + email + "\"}",
                new com.example.criborm.cleanairly.PeticionarioREST.RespuestaREST() {
                    @TargetApi(Build.VERSION_CODES.Q)
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        if (cuerpo != null) {
                            Log.d("TEST - MEDICION1", ": " + cuerpo);
                            try {
                                String j = "{ data:" + cuerpo + "}";
                                JSONObject json = new JSONObject(j);
                                JSONArray mediciones = json.getJSONArray("data");
                                listaMediciones.clear();
                                for (int i =0; i<cantidadMediciones; i++){
                                    JSONObject medicion = mediciones.getJSONObject(i);
                                    int valor = medicion.getInt("valor");
                                    Log.d("TAG1", Integer.toString(valor));
                                    listaMediciones.add(valor);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Log.d("TEST - MEDICION", "ERROR");
                        }
                    }
                });
    }

    /**
     * Muestra un saludo en la interfaz de usuario.
     * @param texto El texto del saludo.
     */
    public void saludo(String texto) {
        saludoUsuario.setText("¡Bienvenido " + texto + "!");
    }

    /**
     * Actualiza el valor del PPM (partes por millón) en la interfaz de usuario.
     * @param nuevoValor El nuevo valor de PPM a mostrar.
     */
    public void actualizarTextoMedicion(String nuevoValor) {
        valorPpm.setText(nuevoValor);
    }


    //----------------------------------------------------------------------------------------------
    // API REST BLUETOOTH
    //----------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // BLUETOOTH
    // ---------------------------------------------------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()


}