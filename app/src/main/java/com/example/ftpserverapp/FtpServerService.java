package com.example.ftpserverapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;

public class FtpServerService extends Service {

    private static final String TAG = "FtpServerService";
    private FtpServer ftpServer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundNotification();
        startFtpServer();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "ftp_channel",
                "FTP Server",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startForegroundNotification() {
        Notification notification = new NotificationCompat.Builder(this, "ftp_channel")
                .setContentTitle("FTP Server Running")
                .setContentText("Your FTP server is active on port 2121")
                .setSmallIcon(R.drawable.ic_ftp)
                .setOngoing(true)
                .build();
        startForeground(1, notification);
    }

    private void startFtpServer() {
        try {
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory factory = new ListenerFactory();
            factory.setPort(2121);
            serverFactory.addListener("default", factory.createListener());

            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            File userFile = new File(getFilesDir(), "users.properties");
            userManagerFactory.setFile(userFile);

            serverFactory.setUserManager(userManagerFactory.createUserManager());
            ftpServer = serverFactory.createServer();
            ftpServer.start();

            Log.i(TAG, "FTP server started on port 2121");

        } catch (Exception e) {
            Log.e(TAG, "Error starting FTP server", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (ftpServer != null && !ftpServer.isStopped()) {
            ftpServer.stop();
            Log.i(TAG, "FTP server stopped");
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
