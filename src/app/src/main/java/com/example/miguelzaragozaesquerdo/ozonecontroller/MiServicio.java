package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class MiServicio extends Service {
    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    private DatosUsuario datosUsuario;
    // --------------------------------------------------------------
    // BLUETOOTH
    // --------------------------------------------------------------
    private FusedLocationProviderClient fusedLocationClient;
    private Location localizacion;
    private int major;
    private int minor;
    private double distanciaSensor;
    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private Handler handler = new Handler();
    private Runnable runnableCode;
    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    private String resultadoGuardar = "ini";
    private String resultadoGuardarAnterior = "";

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel( CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CANAL_ID) .setContentTitle("Inicializando...")
                .setContentText("\"Tu sensor se esta inicializando\"") .setSmallIcon(R.drawable.ic_baseline_cloud_download_24);
        startForeground(NOTIFICACION_ID, notificacion.build());
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        Log.d("SERVICIO", "onStartCommand: ");
        datosUsuario = (DatosUsuario) intent.getSerializableExtra("datosUsuario");
        obtenerUbicacion();




        // Crea un Runnable que contenga la lógica que deseas ejecutar repetidamente.
        runnableCode = new Runnable() {
            @Override
            public void run() {

                // Por ejemplo, aquí puedes llamar a una función para obtener la ubicación.
                obtenerUbicacion();
                inicializarBlueTooth();
                buscarEsteDispositivoBTLE("GTI-3A-CRISTIAN");
                guardarUltimaMedicion(intent.getExtras().getString("email"));
                Log.d("bool", resultadoGuardar);



                if(resultadoGuardar.equals("true") && !resultadoGuardarAnterior.equals("true") && major > 500){
                    notificacion.setContentTitle("Medida demasiado alta");
                    notificacion.setContentText("Usted está expuesto a una cantidad muy elevada de ozono");
                    notificacion.setSmallIcon(R.drawable.ic_baseline_cloud_done_24);
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                    resultadoGuardarAnterior = "true";
                }
                else if(resultadoGuardar.equals("true") && !resultadoGuardarAnterior.equals("true")){
                        notificacion.setContentTitle("Funcionando correctamente");
                        notificacion.setContentText("Tu sensor funciona correctamente");
                        notificacion.setSmallIcon(R.drawable.ic_baseline_cloud_done_24);
                        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                        resultadoGuardarAnterior = "true";
                }

                else if(resultadoGuardar.equals("ini")&&!resultadoGuardarAnterior.equals("ini")){
                    notificacion.setContentTitle("Inicializando...");
                    notificacion.setContentText("Tu sensor se esta inicializando");
                    notificacion.setSmallIcon(R.drawable.ic_baseline_cloud_download_24);
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                    resultadoGuardarAnterior = "ini";
                }
                else if(resultadoGuardar.equals("false")&&!resultadoGuardarAnterior.equals("false")){
                    notificacion.setContentTitle("Sensor apagado");
                    notificacion.setContentText("Comprueba el nivel de carga de tu sensor");
                    notificacion.setSmallIcon(R.drawable.ic_baseline_cloud_off_24);
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                    resultadoGuardarAnterior = "false";
                }
                else if(distanciaSensor > 100){
                    notificacion.setContentTitle("Sensor muy lejos");
                    notificacion.setContentText("El sensor está muy lejos de tu móvil");
                    notificacion.setSmallIcon(R.drawable.ic_baseline_cloud_off_24);
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                    resultadoGuardarAnterior = "false";
                }





                // Ejecuta este Runnable nuevamente después de 10 segundos.
                handler.postDelayed(this, 10000); // 10000 milisegundos = 10 segundos
            }
        };

        // Inicia el primer ciclo para la ejecución después de 10 segundos.
        handler.post(runnableCode);

        return START_STICKY;
    }





    private void obtenerUbicacion() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Manejar la falta de permisos aquí o notificar a la actividad asociada.
            // Puedes enviar un broadcast o notificar a la actividad como se discutió antes.
            // Puedes incluso solicitar permisos a la actividad desde aquí.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Manejar la ubicación obtenida aquí.
                        localizacion = location;
                        // Puedes realizar acciones basadas en la ubicación, por ejemplo, guardarla o procesarla.

                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error que ocurra al obtener la ubicación.
                });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // Detén el Handler cuando el servicio se destruya para evitar fugas de memoria.
        handler.removeCallbacks(runnableCode);
        Log.d("SERVICIO", "destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int majorAnterior;
    private void guardarUltimaMedicion(String email) {
        if(localizacion != null){
            Log.d("TEST-ENVIAR", "guardarUltimaMedicion: ");
            if(major != 0 && major !=majorAnterior){
                PeticionarioREST elPeticionario = new PeticionarioREST();
                elPeticionario.hacerPeticionREST("POST", "http://192.168.1.47:3001/api/sensor/value",
                        "{\"email\": \"" + email + "\"" + "," +
                                "\"valor\": \"" + major + "\"" + "," +
                                "\"lugar\": \"" + localizacion.getLatitude() + ", " + localizacion.getAltitude() + "\"" + "," +
                                "\"idContaminante\": \"" + "1" + "\"}",
                        new PeticionarioREST.RespuestaREST() {
                            @Override
                            public void callback(int codigo, String cuerpo) {
                                if (cuerpo != null) {
                                    Log.d("TEST - ENVIAR", ": " + cuerpo);
                                    if(major == majorAnterior){
                                        resultadoGuardar = "false";
                                    }else{
                                        resultadoGuardar = "true";
                                    }
                                    majorAnterior = major;
                                } else {
                                    Log.d("TEST - MEDICION", "ERROR");
                                    resultadoGuardar = "false";
                                }
                            }
                        });
            }
            else{
                resultadoGuardar = "false";
            }

        }
        else{
            obtenerUbicacion();
            resultadoGuardar = "ini";
        }
    }


    //----------------------------------------------------------------------------------------------
    // API REST BLUETOOTH
    //----------------------------------------------------------------------------------------------

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                //mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------


    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        //Log.d(ETIQUETA_LOG, " ****************************************************");
        //Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        // Log.d(ETIQUETA_LOG, " ****************************************************");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        // Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        // Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
         //Log.d(ETIQUETA_LOG, " rssi = " + rssi);
         distanciaSensor = (-1*rssi);

        // Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        // Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);
/*
        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));


        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

 */


        major = Utilidades.bytesToInt(tib.getMajor());

        minor = Utilidades.bytesToInt(tib.getMinor());
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                //BluetoothDevice bluetoothDevice = resultado.getDevice();
                //byte[] bytes = resultado.getScanRecord().getBytes();
                //int rssi = resultado.getRssi();
                //TramaIBeacon tib = new TramaIBeacon(bytes);
                // major = Utilidades.bytesToInt(tib.getMajor());
                // minor = Utilidades.bytesToInt(tib.getMinor());
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(sf);

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + com.example.criborm.cleanairly.Utilidades.stringToUUID( dispositivoBuscado ) );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Iniciar el escaneo
        this.elEscanner.startScan(filters, scanSettings, this.callbackDelEscaneo);

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado");
        //this.buscarEsteDispositivoBTLE( com.example.criborm.cleanairly.Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) );

        //this.buscarEsteDispositivoBTLE( "EPSG-GTI-PROY-3A" );
        this.buscarEsteDispositivoBTLE("GTI-3A-CRISTIAN");

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }



    } // ()
}