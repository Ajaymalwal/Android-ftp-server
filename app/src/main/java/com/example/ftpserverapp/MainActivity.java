package com.example.ftpserverapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.stop_button);

        startButton.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                startFtpService();
            } else {
                requestStoragePermission();
            }
        });

        stopButton.setOnClickListener(v -> stopFtpService());
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFtpService();
            } else {
                Toast.makeText(this, "Permission required to start FTP server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startFtpService() {
        Intent serviceIntent = new Intent(this, FtpServerService.class);
        startForegroundService(serviceIntent);
        Toast.makeText(this, "FTP server started", Toast.LENGTH_SHORT).show();
    }

    private void stopFtpService() {
        Intent serviceIntent = new Intent(this, FtpServerService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "FTP server stopped", Toast.LENGTH_SHORT).show();
    }
}
