package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Clase que representa la pantalla de inicio de la aplicación.
 * Autor: Mario Merenciano
 */
public class Home extends AppCompatActivity {

    ProgressBar progressBar;
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
        cargarDatosEnGrafica();
        actualizarUltimaMedicion(5000);

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
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    CODIGO_PETICION_PERMISOS);
        }

        obtenerUltimaMedicion(datosUsuario.getEmail());

        actualizarUltimaMedicion(10000);

        // Luego, antes de iniciar el servicio, puedes verificar si está activo
        if (!isServicioActivo()) {
            activarServicio();
        } else {
            // El servicio ya está activo, realiza alguna acción o muestra un mensaje
            // por ejemplo, Log.d("Servicio", "El servicio ya está activo");
        }
    }

    /**
     * Verifica si un servicio específico está actualmente en ejecución en la aplicación.
     * Esta función recorre la lista de servicios en ejecución en el dispositivo y compara
     * el nombre de clase de cada servicio en ejecución con el de 'MiServicio'.
     * Si encuentra una coincidencia, indica que el servicio está activo y devuelve 'true'.
     * En caso contrario, devuelve 'false', indicando que el servicio no está activo.
     *
     * @return 'true' si el servicio 'MiServicio' está en ejecución, 'false' en caso contrario.
     */
    private boolean isServicioActivo() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MiServicio.class.getName().equals(service.service.getClassName())) {
                    return true; // El servicio está activo
                }
            }
        }
        return false; // El servicio no está activo
    }

    /**
     * Activa el servicio 'MiServicio'.
     * Esta función crea un Intent para el servicio 'MiServicio' y le añade información adicional,
     * en este caso, el correo electrónico del usuario, obtenido de 'datosUsuario'.
     * Luego, inicia el servicio utilizando este Intent. Este método es útil para iniciar el servicio
     * con los datos necesarios para su funcionamiento, permitiendo que el servicio realice sus tareas con
     * información contextual relevante del usuario.
     */
    public void activarServicio(){
        Intent serviceIntent = new Intent(this, MiServicio.class);
        serviceIntent.putExtra("email",datosUsuario.getEmail());
        startService(serviceIntent);
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

    /**
     * Redirige a la pantalla de perfil del usuario.
     */
    public void botonLandingPerfil(View view) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, PerfilUsuarioEditar.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Abre la actividad de información del usuario.
     * Esta función es llamada cuando se hace clic en un botón para ver la información del usuario.
     * Realiza las siguientes acciones:
     * 1. Crea una nueva intención para abrir la actividad de información.
     * 2. Agrega datos de usuario a la intención utilizando un objeto Bundle.
     * 3. Inicia la actividad de información pasando la intención.
     *
     * @param view La vista que activó la función, generalmente un botón.
     *             Se utiliza para cumplir con la firma de un método onClick en el archivo de diseño XML.
     *
     * @see Informacion Actividad que muestra la información del usuario.
     */
    public void botonAbrirInfomacion(View view) {
        Intent intent = new Intent(this, Informacion.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Obtiene los datos del usuario a través de una petición REST.
     * @param email El correo electrónico del usuario.
     */
    public void obtenerDatosUsuario(String email){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/usuario",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - RESPUESTA","codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                        if(cuerpo != null){
                            try {
                                JSONObject jsonObject = new JSONObject(cuerpo);
                                datosUsuario.setNombre(jsonObject.getString("nombre"));
                                datosUsuario.setTelefono(jsonObject.getString("telefono"));
                                datosUsuario.setIdSonda(jsonObject.getString("idSonda"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            saludo(datosUsuario.getNombre());
                        } else{
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
    private void actualizarUltimaMedicion(final long intervaloMillis){
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
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/medida",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST() {
                    @TargetApi(Build.VERSION_CODES.Q)
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        if (cuerpo != null) {
                            Log.d("TEST - MEDICION", ": " + cuerpo);
                            Gson gson = new Gson();
                            datosMedicion = gson.fromJson(cuerpo, DatosMedicion.class);
                            try {
                                actualizarTextoMedicion(String.valueOf(datosMedicion.getValor()));
                                ProgressBar progress = findViewById(R.id.progressBar);
                                progress.setProgress(datosMedicion.getValor());
                                if (datosMedicion.getValor() < 249) {
                                    progress.getCurrentDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                                } else if (datosMedicion.getValor() > 249 && datosMedicion.getValor() < 499) {
                                    progress.getCurrentDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                                } else if (datosMedicion.getValor() > 499) {
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

    /**
     * Solicita y procesa las últimas mediciones de un sensor asociado a un usuario.
     * Esta función envía una solicitud POST a un servidor para obtener un número específico de mediciones recientes
     * asociadas con el email del usuario. La cantidad de mediciones a recuperar se especifica en el parámetro 'cantidadMediciones'.
     * Tras recibir la respuesta del servidor, este método procesa el JSON recibido para extraer y almacenar las mediciones.
     * Si hay un error en la recepción o el procesamiento de los datos, se registra un mensaje de error.
     *
     * @param email             El correo electrónico del usuario asociado a las mediciones.
     * @param cantidadMediciones La cantidad de mediciones a obtener del servidor.
     */
    private void obtenerUltimasMediciones(String email, int cantidadMediciones) {
        Log.d("TAG4", "{\"email\": \"" + email + "\", \"cantidad\": " + cantidadMediciones + "}");
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/obtenerNMedidas",
                "{\"email\": \"" + email + "\", \"cantidad\": " + cantidadMediciones + "}",
                new PeticionarioREST.RespuestaREST() {
                    @TargetApi(Build.VERSION_CODES.Q)
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("TEST - MEDICION1", ": " + "cuerpo null");
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
    public void saludo(String texto){
        saludoUsuario.setText("¡Bienvenido "+texto+"!");
    }

    /**
     * Actualiza el valor del PPM (partes por millón) en la interfaz de usuario.
     * @param nuevoValor El nuevo valor de PPM a mostrar.
     */
    public void actualizarTextoMedicion(String nuevoValor){
        valorPpm.setText(nuevoValor);
    }


    //----------------------------------------------------------------------------------------------
    // API REST BLUETOOTH
    //----------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // BLUETOOTH
    // ---------------------------------------------------------------------------------------------
    /**
     * Maneja los resultados de la solicitud de permisos.
     * Este método es un callback que se invoca cuando el usuario responde a una solicitud de permiso.
     * Dependiendo de si los permisos fueron concedidos o no, realiza acciones específicas.
     * En este caso, si los permisos solicitados son concedidos (específicamente para el código de petición 'CODIGO_PETICION_PERMISOS'),
     * se registra un mensaje de confirmación. Si no se conceden, se registra un mensaje de error.
     * Este método es esencial para el manejo correcto de permisos en la aplicación, asegurando que las funcionalidades
     * que dependen de ciertos permisos solo se ejecuten si están disponibles.
     *
     * @param requestCode  El código de solicitud de permisos, para identificar de qué solicitud se recibió la respuesta.
     * @param permissions  Los permisos solicitados.
     * @param grantResults Los resultados de las solicitudes de permisos, indicando si se concedieron o no.
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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