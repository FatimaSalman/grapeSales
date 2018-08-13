package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ImageAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.callback.RegisterCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.User;

public class PointActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView pointNoTxt;
    private TextView codeTxt;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        connectionManager = new ConnectionManager(this);
        init();
        getUserDetails();
    }

    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        pointNoTxt = findViewById(R.id.pointNoTxt);
        codeTxt = findViewById(R.id.codeTxt);
        TextView copyTxt = findViewById(R.id.copyTxt);
        copyTxt.setOnClickListener(this);
        Button inviteBtn = findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.copyTxt) {
            copyCode();
        } else if (id == R.id.inviteBtn) {
            invite_friends();
        }
    }

    public void getUserDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.getDetails(AppPreferences.getString(PointActivity.this, "token"), new RegisterCallback() {
            @Override
            public void onUserRegisterDone(User user) {
                progressDialog.dismiss();
                codeTxt.setText(user.getInvite_code());
                pointNoTxt.setText(user.getPoint_no());
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(PointActivity.this, error);
            }
        });

    }

    public void invite_friends() {
        Toast.makeText(this, "Share Item", Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_TEXT, "كود دعوة صديق:" + "  " + codeTxt.getText().toString() + " \n" +
                        "https://play.google.com/store/apps/details?id=com.creatively.grapeSalesApp.grapeapplication");
        startActivity(intent);
    }

    public void copyCode() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("نسخ", codeTxt.getText().toString().trim());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "تم نسخ الكود",
                Toast.LENGTH_SHORT).show();
    }
}
