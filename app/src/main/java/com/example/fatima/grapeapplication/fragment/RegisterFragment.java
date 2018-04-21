package com.example.fatima.grapeapplication.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.activity.MainActivity;
import com.example.fatima.grapeapplication.activity.MainUserActivity;
import com.example.fatima.grapeapplication.callback.RegisterCallback;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.AppPreferences;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.User;

import java.util.Locale;

public class RegisterFragment extends Fragment implements View.OnClickListener {


    private EditText fullNameEditText, mobileEditText, passwordEditText, confirmPasswordEditText;
    private ConnectionManager connectionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_register, container, false);
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        return view;
    }

    public void init(View view) {


        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        Button registerBtn = view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
    }

    public void register() {


        final String username = fullNameEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            fullNameEditText.setError(getString(R.string.required_field));
            fullNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(mobile)) {
            mobileEditText.setError(getString(R.string.required_field));
            mobileEditText.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.required_field));
            passwordEditText.requestFocus();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.required_field));
            confirmPasswordEditText.requestFocus();
        } else if (!TextUtils.equals(confirmPassword, password)) {
            confirmPasswordEditText.setError(getString(R.string.correct_pass));
            confirmPasswordEditText.requestFocus();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            User user = new User();
            user.setFull_name(username);
            user.setPhone(mobile);
            user.setPassword(password);
            user.setType("0");

            connectionManager.register(user, new RegisterCallback() {
                @Override
                public void onUserRegisterDone(User user) {
                    progressDialog.hide();
                    AppPreferences.saveString(getActivity(), "token", user.getToken());
                    AppPreferences.saveString(getActivity(), "user_id", user.getId());
                    AppPreferences.saveString(getActivity(), "active", user.getIs_active());
                    if (user.getType().equals("0")) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else if (user.getType().equals("1")) {
                        Intent intent = new Intent(getActivity(), MainUserActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onError(String error) {
                    progressDialog.hide();
                    AppErrorsManager.showErrorDialog(getActivity(), error);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerBtn) {
            register();
        }
    }


}
