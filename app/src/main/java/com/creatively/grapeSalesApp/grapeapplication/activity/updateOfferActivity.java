package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ImageAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OfferCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.callback.RegisterCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FilePath;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Images;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.User;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class updateOfferActivity extends AppCompatActivity implements View.OnClickListener {

    //    private CircularImageView shopImage;
//    private ImageView ic_camera;
    private ConnectionManager connectionManager;
    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private File fileSchema;
    private String offer_id;
    private EditText offerNameEditText, previousPriceEditText, nextPriceEditText, bioEditText;
    private ProgressDialog progressDialog;
    private List<Images> imageList = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private ImageView addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
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
        addImage = findViewById(R.id.addImage);
        previousPriceEditText = findViewById(R.id.previousPriceEditText);
        nextPriceEditText = findViewById(R.id.nextPriceEditText);
        bioEditText = findViewById(R.id.bioEditText);
//        ic_camera = findViewById(R.id.ic_camera);
//        shopImage = findViewById(R.id.shopImage);
        addImage.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        RecyclerView recycleViewImage = findViewById(R.id.recycleView);
        imageAdapter = new ImageAdapter(this, imageList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                imageList.remove(position);
                imageAdapter.notifyDataSetChanged();
            }
        });
        recycleViewImage.setLayoutManager(new GridLayoutManager(this, 3));
        recycleViewImage.setItemAnimator(new DefaultItemAnimator());
        recycleViewImage.setNestedScrollingEnabled(false);
        recycleViewImage.setAdapter(imageAdapter);
        getOfferDetails();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerBtn) {
            updateOffer();
        } else if (id == R.id.addImage) {
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
            offer.setImagesList(imageList);
            offer.setImageFile(fileSchema);
            offer.setId(offer_id);
            connectionManager.updateOffer(offer, new InstallCallback() {
                @Override
                public void onStatusDone(String result) {
                   progressDialog.dismiss();
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
                   progressDialog.dismiss();
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
//        Picasso.get().load(uri).into(shopImage);
//        ic_camera.setVisibility(View.GONE);
        Images image = new Images(uri, fileSchema);
        imageList.add(image);
        imageAdapter.notifyDataSetChanged();

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
                try {
                    JSONArray jsonArray = new JSONArray(offer1.getOfferImage());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String image = jsonArray.getString(i);
                        Log.e("images", image);
                        Images images = new Images(image);
                        imageList.add(images);
                        imageAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Picasso.get()
//                        .load(FontManager.IMAGE_URL + offer1.getOfferImage()).into(shopImage);
//                ic_camera.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(updateOfferActivity.this, error);
            }
        });

    }
}
