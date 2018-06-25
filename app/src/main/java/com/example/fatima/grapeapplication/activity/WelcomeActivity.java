package com.example.fatima.grapeapplication.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.fragment.LoginFragment;
import com.example.fatima.grapeapplication.fragment.RegisterFragment;
import com.example.fatima.grapeapplication.manager.FontManager;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout loginLayout, registerLayout;
    private TextView registerTxt, loginTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }


    public void init() {

        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        loginLayout = findViewById(R.id.loginLayout);
        registerTxt = findViewById(R.id.registerTxt);
        loginTxt = findViewById(R.id.loginTxt);
        registerLayout = findViewById(R.id.registerLayout);
        loginLayout.setOnClickListener(this);
        registerLayout.setOnClickListener(this);
        showOtherFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerLayout) {
            loginLayout.setBackground(null);
            loginTxt.setTypeface(FontManager.getTypefaceTextInputRegular(this));
            registerTxt.setTypeface(FontManager.getTypefaceTextInputBold(this));
            registerLayout.setBackground(getResources().getDrawable(R.drawable.bg_item_tab));
            replaceFragment(new RegisterFragment());
        } else if (id == R.id.loginLayout) {
            registerLayout.setBackground(null);
            registerTxt.setTypeface(FontManager.getTypefaceTextInputRegular(this));
            loginTxt.setTypeface(FontManager.getTypefaceTextInputBold(this));
            loginLayout.setBackground(getResources().getDrawable(R.drawable.bg_item_tab));
            replaceFragment(new LoginFragment());
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragmentManager != null;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentReplace, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    public void showOtherFragment() {
        registerLayout.setBackground(null);
        registerTxt.setTypeface(FontManager.getTypefaceTextInputRegular(this));
        loginTxt.setTypeface(FontManager.getTypefaceTextInputBold(this));
        loginLayout.setBackground(getResources().getDrawable(R.drawable.bg_item_tab));
        replaceFragment(new LoginFragment());
    }
}
