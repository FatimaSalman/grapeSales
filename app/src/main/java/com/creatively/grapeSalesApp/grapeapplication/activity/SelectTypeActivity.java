package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectTypeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_select_type);
        init();
        checkAndRequestPermissions();
        if (!AppPreferences.getString(this, "token").equals("0")) {
            if (AppPreferences.getString(this, "type").equals("0")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (AppPreferences.getString(this, "type").equals("1")) {
                startActivity(new Intent(this, MainUserActivity.class));
                finish();
            }
        }
    }

    public void init() {
        RelativeLayout markerLayout = findViewById(R.id.markerLayout);
        RelativeLayout userLayout = findViewById(R.id.userLayout);
        markerLayout.setOnClickListener(this);
        userLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.markerLayout) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("type", "0");
            startActivity(intent);
        } else if (id == R.id.userLayout) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("type", "1");
            startActivity(intent);
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }
}
