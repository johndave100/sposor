package com.sponsoreddetector;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Switch serviceSwitch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        serviceSwitch = findViewById(R.id.serviceSwitch);
        
        serviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkPermissions()) {
                    startDetectionService();
                } else {
                    requestPermissions();
                    serviceSwitch.setChecked(false);
                }
            } else {
                stopDetectionService();
            }
        });
    }
    
    private boolean checkPermissions() {
        return Settings.canDrawOverlays(this) && isAccessibilityServiceEnabled();
    }
    
    private boolean isAccessibilityServiceEnabled() {
        String enabledServices = Settings.Secure.getString(
            getContentResolver(),
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return enabledServices != null && 
               enabledServices.contains(getPackageName() + "/" + TextDetectionService.class.getName());
    }
    
    private void requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            Toast.makeText(this, "Please enable overlay permission", Toast.LENGTH_LONG).show();
        }
        
        if (!isAccessibilityServiceEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable accessibility service for Sponsored Detector", Toast.LENGTH_LONG).show();
        }
    }
    
    private void startDetectionService() {
        Intent intent = new Intent(this, OverlayService.class);
        startForegroundService(intent);
        Toast.makeText(this, "Sponsored detection enabled", Toast.LENGTH_SHORT).show();
    }
    
    private void stopDetectionService() {
        Intent intent = new Intent(this, OverlayService.class);
        stopService(intent);
        Toast.makeText(this, "Sponsored detection disabled", Toast.LENGTH_SHORT).show();
    }
}