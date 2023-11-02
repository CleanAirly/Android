package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.graphics.Color;
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
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    ProgressBar progressBar;
    TextView saludoUsuario;
    TextView valorPpm;
    TextView estadoAire;

    private DatosUsuario datosUsuario;

    private Handler handler = new Handler();
    private Runnable runnable;

    private LineChart chart;

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
    }

    private void cargarDatosEnGrafica() {
        // Suponiendo que tienes el siguiente JSON:
        String jsonData =
                "{"
                        + "\"data\": ["
                        + "    {\"hour\": \"07h\", \"value\": 20},"
                        + "    {\"hour\": \"11h\", \"value\": 60},"
                        + "    {\"hour\": \"15h\", \"value\": 55},"
                        + "    {\"hour\": \"19h\", \"value\": 30},"
                        + "    {\"hour\": \"23h\", \"value\": 50}"
                        + "]"
                        + "}";

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                float xValue = (float) i;
                float yValue = (float) obj.getInt("value");
                entries.add(new Entry(xValue, yValue));
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

    public void botonLandingPerfil(View view) {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    public void obtenerDatosUsuario(String email){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.36:3001/api/sensor/usuario",
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
        elPeticionario.hacerPeticionREST("POST", "http://192.168.1.103:3001/api/sensor/medida",
                "{\"email\": \"" + email + "\"}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        if(cuerpo != null){
                            Log.d("TEST - MEDICION", ": "+cuerpo);
                            DatosMedicion datosMedicion;
                            Gson gson = new Gson();
                            datosMedicion  = gson.fromJson(cuerpo, DatosMedicion.class);
                            try{
                                actualizarTextoMedicion(String.valueOf(datosMedicion.getValor()));
                            }catch(Exception e){
                                Log.d("TEST - MEDICION", "ERROR: "+e);
                            }
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