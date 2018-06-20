package com.example.fatima.grapeapplication.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.callback.InstallCallback;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.GMail;
import com.example.fatima.grapeapplication.manager.SendMail;
import com.example.fatima.grapeapplication.model.Order;

import java.util.Locale;

public class UserApplyFragment extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameEditText, mobileEditText, locationEditText, noteEditText;
    private String offer_id, shop_id, user_id;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_user);
        offer_id = getIntent().getStringExtra("offer_id");
        shop_id = getIntent().getStringExtra("shop_id");
        user_id = getIntent().getStringExtra("user_id");
        connectionManager = new ConnectionManager(this);
        init();
    }


    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        locationEditText = findViewById(R.id.locationEditText);
        noteEditText = findViewById(R.id.noteEditText);

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
    }

    public void orderNow() {
        final String username = fullNameEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String location = locationEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            fullNameEditText.setError(getString(R.string.required_field));
            fullNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(mobile)) {
            mobileEditText.setError(getString(R.string.required_field));
            mobileEditText.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            locationEditText.setError(getString(R.string.required_field));
            locationEditText.requestFocus();
        } else {
            Order order = new Order();
            order.setUserName(username);
            order.setShop_id(shop_id);
            order.setOffer_id(offer_id);
            order.setMobile(mobile);
            order.setAddress(location);
            order.setNote(note);
            order.setUser_id(user_id);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            connectionManager.applyOrder(order, new InstallCallback() {
                @Override
                public void onStatusDone(String status) {
                    progressDialog.hide();
                    AppErrorsManager.showSuccessDialog(UserApplyFragment.this, status, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    progressDialog.hide();
                    AppErrorsManager.showErrorDialog(UserApplyFragment.this, error);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerBtn) {
            AppErrorsManager.showSuccessDialog(this, getString(R.string.confirm),
                    getString(R.string.are_you_sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            orderNow();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

        }
    }

}
