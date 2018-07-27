package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.fragment.LoginFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.RegisterFragment;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout loginLayout, registerLayout;
    private TextView registerTxt, loginTxt;
    private String type, offer_id, shop_id, user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        if (!AppPreferences.getString(this, "token").equals("0")) {
            startActivity(new Intent(this, MainActivity.class));
        }

        type = getIntent().getStringExtra("type");

        offer_id = getIntent().getStringExtra("offer_id");
        shop_id = getIntent().getStringExtra("shop_id");
        user_id = getIntent().getStringExtra("user_id");

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
            replaceFragment(new RegisterFragment(), type, offer_id, user_id, shop_id);
        } else if (id == R.id.loginLayout) {
            registerLayout.setBackground(null);
            registerTxt.setTypeface(FontManager.getTypefaceTextInputRegular(this));
            loginTxt.setTypeface(FontManager.getTypefaceTextInputBold(this));
            loginLayout.setBackground(getResources().getDrawable(R.drawable.bg_item_tab));
            replaceFragment(new LoginFragment(), type, offer_id, user_id, shop_id);
        }
    }

    public void replaceFragment(Fragment fragment, String data, String offer_id, String user_id, String shop_id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragmentManager != null;
        Bundle arguments = new Bundle();
        arguments.putString("type", data);
        arguments.putString("user_id", user_id);
        arguments.putString("offer_id", offer_id);
        arguments.putString("shop_id", shop_id);
        fragment.setArguments(arguments);
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
//        Log.e("offer_id",offer_id);
//        Log.e("shop_id",shop_id);
//        Log.e("user_id",user_id);
        replaceFragment(new LoginFragment(), type, offer_id, user_id, shop_id);
    }
}
