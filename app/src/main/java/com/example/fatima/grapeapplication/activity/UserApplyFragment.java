package com.example.fatima.grapeapplication.activity;


import android.content.DialogInterface;
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
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.GMail;
import com.example.fatima.grapeapplication.manager.SendMail;
import com.example.fatima.grapeapplication.model.Order;

public class UserApplyFragment extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameEditText, mobileEditText, locationEditText, noteEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
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
            Order user = new Order();
//            user.setUsername(username);
//            user.setUsername(email);
//            user.setUsername(codeTxt + mobile);
//            user.setUsername(password);
//
//            Log.e("usss", user.getEmail());
//            connectionManager.register(user, new RegisterCallback() {
//                @Override
//                public void onUserRegisterDone(User user) {
//                    Gson json = new Gson();
//                    String userJson = json.toJson(user);
//                    AppPreferences.saveString(RegisterFragment.this, "userJson", userJson);
//                    if (user.getRole_id().equals("5ac25884a0a25a3172c88632")) {
//                        Intent intent = new Intent(RegisterFragment.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                }
//
//                @Override
//                public void onError(String error) {
//                    AppErrorsManager.showErrorDialog(RegisterFragment.this, error);
//                }
//            });
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
                            SendMail sendMail = new SendMail(UserApplyFragment.this,
                                    "fatoom.21.19911@gmail.com", "helllo",
                                    "helllo");
                            sendMail.execute();
//                            GMail m = new GMail(getResources().getString(
//                                    R.string.emailId), getResources().getString(
//                                    R.string.pas));
//                            m.setToAddresses("fatoom.21.19911@gmail.com");
//                            m.setFromAddress("grapesalesapp@gmail.com");
//                            m.setMailSubject("helllo");
//                            m.setMailBody("hello");
//                            try {
//                                m.send();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Log.e("maaaail", m. + "");
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

        }
    }

}
