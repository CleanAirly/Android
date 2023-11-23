package com.example.criborm.cleanairly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
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
}
