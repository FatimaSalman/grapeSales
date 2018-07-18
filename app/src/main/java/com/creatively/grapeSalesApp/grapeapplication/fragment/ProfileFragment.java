package com.creatively.grapeSalesApp.grapeapplication.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.RegisterCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.User;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText fullNameEditText, mobileEditText, passwordEditText;
    private ConnectionManager connectionManager;
    private String token;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);
        connectionManager = new ConnectionManager(getActivity());
        token = AppPreferences.getString(getActivity(), "token");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        init(view);
        getUserDetails();
        return view;
    }

    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);
        title.setText(getString(R.string.profile));
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        Button registerBtn = view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
    }

    public void getUserDetails() {
        connectionManager.getDetails(token, new RegisterCallback() {
            @Override
            public void onUserRegisterDone(User user) {
                progressDialog.dismiss();
                fullNameEditText.setText(user.getFull_name());
                mobileEditText.setText(user.getPhone());
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(getActivity(), error);
            }
        });

    }

    public void updateProfile() {
        progressDialog.show();
        final String name = fullNameEditText.getText().toString().trim();
        final String phone = mobileEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            fullNameEditText.setError(getString(R.string.required_field));
            fullNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            mobileEditText.setError(getString(R.string.required_field));
            mobileEditText.requestFocus();
        } else {
            User user = new User();
            user.setPhone(phone);
            user.setFull_name(name);
            user.setPassword(password);
            user.setToken(token);
            connectionManager.updateDetails(user, new RegisterCallback() {
                @Override
                public void onUserRegisterDone(User user) {
                   progressDialog.dismiss();
                    AppErrorsManager.showSuccessDialog(getActivity(),
                            getString(R.string.update_successfully), null);
                }

                @Override
                public void onError(String error) {
                   progressDialog.dismiss();
                    AppErrorsManager.showErrorDialog(getActivity(), error);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerBtn) {
            updateProfile();
        }
    }

}
