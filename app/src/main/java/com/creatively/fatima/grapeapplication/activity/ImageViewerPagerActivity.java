package com.creatively.fatima.grapeapplication.activity;

import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.creatively.fatima.grapeapplication.R;
import com.creatively.fatima.grapeapplication.adapter.ImagesPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class ImageViewerPagerActivity extends AppCompatActivity {

    ViewPager mPager;
    ImagesPagerAdapter ipa;
    ArrayList<String> images;
    int currentPos;
    int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_viewer_pager);

        images = getIntent().getStringArrayListExtra("IMAGES");
        selected = getIntent().getExtras().getInt("SELECTED");
        mPager = findViewById(R.id.pager);
        ipa = new ImagesPagerAdapter(getSupportFragmentManager(), images);
        mPager.setAdapter(ipa);
        mPager.setCurrentItem(selected);
    }


}
