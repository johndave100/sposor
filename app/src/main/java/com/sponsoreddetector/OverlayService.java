package com.sponsoreddetector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class OverlayService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "SponsoredDetectorChannel";
    
    private AudioManager audioManager;
    private int originalVolume;
    private boolean isMuted = false;
    private static final String TAG = "OverlayService";
    
    private BroadcastReceiver sponsoredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean sponsoredFound = intent.getBooleanExtra("sponsored_found", false);
            
            if (sponsoredFound && !isMuted) {
                muteVolume();
            } else if (!sponsoredFound && isMuted) {
                restoreVolume();
            }
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel();
        
        IntentFilter filter = new IntentFilter("com.sponsoreddetector.SPONSORED_DETECTED");
        registerReceiver(sponsoredReceiver, filter);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }
    
    private void muteVolume() {
        try {
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            isMuted = true;
            updateNotification("Sponsored content detected - Volume muted");
            Log.d(TAG, "Muted audio");
        } catch (SecurityException e) {
            Log.w(TAG, "Could not change volume: " + e.getMessage());
        }
    }
    
    private void restoreVolume() {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            isMuted = false;
            updateNotification("Monitoring for sponsored content");
            Log.d(TAG, "Restored audio");
        } catch (SecurityException e) {
            Log.w(TAG, "Could not restore volume: " + e.getMessage());
        }
    }
    
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Sponsored Detector",
            NotificationManager.IMPORTANCE_LOW
        );
        
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
    
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sponsored Detector")
            .setContentText("Monitoring for sponsored content")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build();
    }
    
    private void updateNotification(String text) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sponsored Detector")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build();
            
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sponsoredReceiver != null) {
            unregisterReceiver(sponsoredReceiver);
        }
        if (isMuted) {
            restoreVolume();
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}