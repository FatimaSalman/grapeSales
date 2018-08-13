package com.creatively.grapeSalesApp.grapeapplication.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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


import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainUserActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.UserApplyFragment;
import com.creatively.grapeSalesApp.grapeapplication.callback.RegisterCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;
import java.util.Objects;

public class RegisterFragment extends Fragment implements View.OnClickListener {


    private EditText fullNameEditText, mobileEditText, passwordEditText, confirmPasswordEditText,
            codeEditText;
    private ConnectionManager connectionManager;
    private String type, offer_id, shop_id, user_id;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_register, container, false);
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getString("type");
            Log.e("type", type);
            offer_id = arguments.getString("offer_id");
            shop_id = arguments.getString("shop_id");
            user_id = arguments.getString("user_id");
        }
        type = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("type");
        Log.e("type", type);
        return view;
    }

    public void init(View view) {
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        codeEditText = view.findViewById(R.id.codeEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        Button registerBtn = view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
    }

    public void register() {
        final String username = fullNameEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String code = codeEditText.getText().toString().trim();

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
            user.setType(type);
            user.setFcm_token(FirebaseInstanceId.getInstance().getToken());

            connectionManager.register(user, code, new RegisterCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onUserRegisterDone(User user) {
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerBtn) {
            register();
        }
    }


}
