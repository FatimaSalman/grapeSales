package com.creatively.grapeSalesApp.grapeapplication.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainUserActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.UserApplyFragment;
import com.creatively.grapeSalesApp.grapeapplication.callback.LoginCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private ConnectionManager connectionManager;
    private EditText userNameEditText, passwordEditText;
    private String offer_id, shop_id, user_id;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_login, container, false);
        connectionManager = new ConnectionManager(getActivity());
//        Handler handler = new Handler(Looper.getMainLooper());
        init(view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            offer_id = arguments.getString("offer_id");
            shop_id = arguments.getString("shop_id");
            user_id = arguments.getString("user_id");
        }

        offer_id = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("offer_id");
        shop_id = getActivity().getIntent().getStringExtra("shop_id");
        user_id = getActivity().getIntent().getStringExtra("user_id");
        return view;
    }


    public void init(View view) {

        userNameEditText = view.findViewById(R.id.userNameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        Button loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginBtn) {
            loginForm();
        }
    }

    public void loginForm() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        String email = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            userNameEditText.setError(getString(R.string.required_field));
            userNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.required_field));
            passwordEditText.requestFocus();
        } else {
            User user = new User();
            user.setFull_name(email);
            user.setPassword(password);
            user.setFcm_token(FirebaseInstanceId.getInstance().getToken());
            connectionManager.login(user, new LoginCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onLoginDone(User user) {
                    progressDialog.dismiss();
                    AppPreferences.saveString(getActivity(), "token", user.getToken());
                    AppPreferences.saveString(getActivity(), "user_id", user.getId());
                    AppPreferences.saveString(getActivity(), "active", user.getIs_active());
                    AppPreferences.saveString(getActivity(), "type", user.getType());
                    if (user.getType().equals("0")) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).finish();
                    } else if (user.getType().equals("1")) {
                        if (offer_id != null && shop_id != null && user_id != null) {
                            Intent intent = new Intent(getActivity(), UserApplyFragment.class);
                            intent.putExtra("offer_id", offer_id);
                            intent.putExtra("shop_id", shop_id);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).finish();
                        } else {
                            Intent intent = new Intent(getActivity(), MainUserActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).finish();
                        }

                    }
                }

                @Override
                public void onError(String error) {
                    progressDialog.dismiss();
                    AppErrorsManager.showErrorDialog(getActivity(), error);
                }
            });

        }
    }
}
