package com.creatively.fatima.grapeapplication.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.creatively.fatima.grapeapplication.R;
import com.creatively.fatima.grapeapplication.callback.InstallCallback;
import com.creatively.fatima.grapeapplication.callback.LoginCallback;
import com.creatively.fatima.grapeapplication.manager.AppErrorsManager;
import com.creatively.fatima.grapeapplication.manager.AppPreferences;
import com.creatively.fatima.grapeapplication.manager.ConnectionManager;
import com.creatively.fatima.grapeapplication.manager.FilePath;
import com.creatively.fatima.grapeapplication.model.Shop;
import com.creatively.fatima.grapeapplication.model.User;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

public class AddShopActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText fullNameEditText, mobileEditText, identityEditText, bioEditText, recordEditText;
    private CircularImageView shopImage;
    private ImageView ic_camera;
    private ConnectionManager connectionManager;
    private String category, city, user_id;
    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private File fileSchema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_add_shop);
        connectionManager = new ConnectionManager(this);
        category = getIntent().getStringExtra("category");
        city = getIntent().getStringExtra("city");
        user_id = AppPreferences.getString(this, "user_id");

        Log.e("dataa", category + " // " + city + " // " + user_id);
        init();
    }

    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);


        fullNameEditText = findViewById(R.id.fullNameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        identityEditText = findViewById(R.id.identityEditText);
        bioEditText = findViewById(R.id.bioEditText);
        ic_camera = findViewById(R.id.ic_camera);
        shopImage = findViewById(R.id.shopImage);
        recordEditText = findViewById(R.id.recordEditText);

        shopImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerBtn) {
            addShop();
        } else if (id == R.id.shopImage) {
            openGalleryFile();
        }
    }

    public void addShop() {

        String shop_name = fullNameEditText.getText().toString().trim();
        String shop_address = mobileEditText.getText().toString().trim();
        String shop_phone = identityEditText.getText().toString().trim();
        String shop_bio = bioEditText.getText().toString().trim();
        String record_no = recordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(shop_name)) {
            fullNameEditText.setError(getString(R.string.required_field));
            fullNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(shop_address)) {
            mobileEditText.setError(getString(R.string.required_field));
            mobileEditText.requestFocus();
        } else if (TextUtils.isEmpty(shop_phone)) {
            identityEditText.setError(getString(R.string.required_field));
            identityEditText.requestFocus();
        } else if (TextUtils.isEmpty(shop_bio)) {
            bioEditText.setError(getString(R.string.required_field));
            bioEditText.requestFocus();
        } else if (fileSchema == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image_shop));
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            Shop shop = new Shop();
            shop.setName(shop_name);
            shop.setAddress(shop_address);
            shop.setShop_phone(shop_phone);
            shop.setShop_bio(shop_bio);
            shop.setCategory_name(category);
            shop.setCity_name(city);
            shop.setUser_id(user_id);
            shop.setRecord_no(record_no);
            shop.setImageFile(fileSchema);
            connectionManager.addShop(shop, new InstallCallback() {
                @Override
                public void onStatusDone(String result) {
                    progressDialog.hide();
                    AppErrorsManager.showSuccessDialog(AddShopActivity.this, result, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            setResult(12, intent);
                            finish();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    progressDialog.hide();
                    AppErrorsManager.showErrorDialog(AddShopActivity.this, error);
                }
            });

        }
    }

    private void openGalleryFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        fileSchema = new File(pdfPathHolder);
        Picasso.with(this).load(uri).into(shopImage);
        ic_camera.setVisibility(View.GONE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE_SCHEMA) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile(data);
            }
        }
    }
}
