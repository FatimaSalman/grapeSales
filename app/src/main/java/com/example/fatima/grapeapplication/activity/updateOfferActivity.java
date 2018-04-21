package com.example.fatima.grapeapplication.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.callback.InstallCallback;
import com.example.fatima.grapeapplication.callback.OfferCallback;
import com.example.fatima.grapeapplication.callback.RegisterCallback;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.FilePath;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.User;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class updateOfferActivity extends AppCompatActivity implements View.OnClickListener {

    private CircularImageView shopImage;
    private ImageView ic_camera;
    private ConnectionManager connectionManager;
    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private File fileSchema;
    private String offer_id;
    private EditText offerNameEditText, previousPriceEditText, nextPriceEditText, bioEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        connectionManager = new ConnectionManager(this);
        offer_id = getIntent().getStringExtra("offer_id");
        init();
    }

    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setText(getString(R.string.update));
        registerBtn.setOnClickListener(this);

        offerNameEditText = findViewById(R.id.offerNameEditText);
        previousPriceEditText = findViewById(R.id.previousPriceEditText);
        nextPriceEditText = findViewById(R.id.nextPriceEditText);
        bioEditText = findViewById(R.id.bioEditText);
        ic_camera = findViewById(R.id.ic_camera);
        shopImage = findViewById(R.id.shopImage);
        shopImage.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        getOfferDetails();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerBtn) {
            updateOffer();
        } else if (id == R.id.shopImage) {
            openGalleryFile();
        }
    }

    public void updateOffer() {

        String offer_name = offerNameEditText.getText().toString().trim();
        String before_price = previousPriceEditText.getText().toString().trim();
        String after_price = nextPriceEditText.getText().toString().trim();
        String offer_bio = bioEditText.getText().toString().trim();

        if (TextUtils.isEmpty(offer_name)) {
            offerNameEditText.setError(getString(R.string.required_field));
            offerNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(before_price)) {
            previousPriceEditText.setError(getString(R.string.required_field));
            previousPriceEditText.requestFocus();
        } else if (TextUtils.isEmpty(after_price)) {
            nextPriceEditText.setError(getString(R.string.required_field));
            nextPriceEditText.requestFocus();
        } else if (TextUtils.isEmpty(offer_bio)) {
            bioEditText.setError(getString(R.string.required_field));
            bioEditText.requestFocus();
        } else {
            progressDialog.show();
            Offer offer = new Offer();
            offer.setOfferTitle(offer_name);
            offer.setPreviousPrice(before_price);
            offer.setNextPrice(after_price);
            offer.setOfferBio(offer_bio);
            offer.setImageFile(fileSchema);
            offer.setId(offer_id);
            connectionManager.updateOffer(offer, new InstallCallback() {
                @Override
                public void onStatusDone(String result) {
                    progressDialog.hide();
                    AppErrorsManager.showSuccessDialog(updateOfferActivity.this, result, new DialogInterface.OnClickListener() {
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
                    AppErrorsManager.showErrorDialog(updateOfferActivity.this, error);
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

    public void getOfferDetails() {
        Offer offer = new Offer();
        offer.setId(offer_id);
        connectionManager.getOfferDetails(offer, new OfferCallback() {
            @Override
            public void onOfferDone(Offer offer1) {
                progressDialog.dismiss();
                offerNameEditText.setText(offer1.getOfferTitle());
                previousPriceEditText.setText(offer1.getPreviousPrice());
                nextPriceEditText.setText(offer1.getNextPrice());
                bioEditText.setText(offer1.getOfferBio());

                Picasso.with(updateOfferActivity.this)
                        .load(FontManager.IMAGE_URL + offer1.getOfferImage()).into(shopImage);
                ic_camera.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(updateOfferActivity.this, error);
            }
        });

    }
}
