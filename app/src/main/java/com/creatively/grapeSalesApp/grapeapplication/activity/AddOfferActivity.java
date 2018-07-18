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
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FilePath;
import com.creatively.grapeSalesApp.grapeapplication.model.Images;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddOfferActivity extends AppCompatActivity implements View.OnClickListener {

    //شعار لعرض خاص لمنطقة محددة للمسختدم
    //تحديد منطقة المستخدم عند الدخول لتطبيق
    //البحث المخصص

//    private CircularImageView shopImage;
    private ImageView ic_camera;
    private ConnectionManager connectionManager;
    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_MULTI = 51;
    private File fileSchema;
    private String shop_id;
    private EditText offerNameEditText, previousPriceEditText, nextPriceEditText, bioEditText;
    private List<Images> imageList = new ArrayList<>();
    private ImageAdapter imageAdapter;

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
        shop_id = getIntent().getStringExtra("shop_id");
        init();
    }

    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);

        offerNameEditText = findViewById(R.id.offerNameEditText);
        ImageView addImage = findViewById(R.id.addImage);
        previousPriceEditText = findViewById(R.id.previousPriceEditText);
        nextPriceEditText = findViewById(R.id.nextPriceEditText);
        bioEditText = findViewById(R.id.bioEditText);
        ic_camera = findViewById(R.id.ic_camera);
//        shopImage = findViewById(R.id.shopImage);
//        shopImage.setOnClickListener(this);
        addImage.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.registerBtn) {
            addOffer();
        }
//        else if (id == R.id.shopImage) {
//            openGalleryFile();
//        }
        else if (id == R.id.addImage) {
            openGalleryFile1();
        }
    }

    public void addOffer() {

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
        }else if (imageList.size() == 0) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_least_1_images));
        } else if (imageList.size() > 3) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_least_3_images));
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            Offer offer = new Offer();
            offer.setOfferTitle(offer_name);
            offer.setPreviousPrice(before_price);
            offer.setNextPrice(after_price);
            offer.setOfferBio(offer_bio);
            offer.setShop_id(shop_id);
            offer.setImagesList(imageList);
            offer.setImageFile(fileSchema);
            connectionManager.addOffer(offer, new InstallCallback() {
                @Override
                public void onStatusDone(String result) {
                   progressDialog.dismiss();
                    AppErrorsManager.showSuccessDialog(AddOfferActivity.this, result, new DialogInterface.OnClickListener() {
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
                    AppErrorsManager.showErrorDialog(AddOfferActivity.this, error);
                }
            });

        }
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
//        Picasso.with(this).load(uri).into(shopImage);
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
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_MULTI) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile1(data);
            }
        }
    }

    private void openGalleryFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA);
    }

    private void openGalleryFile1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_MULTI);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile1(Intent data) {
        if (data == null) return;
        File fileSchema;
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            if (count > 3) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_least_3_images));
            } else {
                int currentItem = 0;
                while (currentItem < count) {
                    Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                    String pdfPathHolder = FilePath.getPath(this, imageUri);
                    Log.e("pdfPathHolder", pdfPathHolder + "");
                    assert pdfPathHolder != null;
                    fileSchema = new File(pdfPathHolder);
                    Images image = new Images(imageUri, fileSchema);
                    imageList.add(image);
                    imageAdapter.notifyDataSetChanged();
                    currentItem = currentItem + 1;
                }
            }
        } else if (data.getData() != null) {
            Uri uri = data.getData();
            Log.e("image", uri + "");
            String pdfPathHolder = FilePath.getPath(this, uri);
            Log.e("pdfPathHolder", pdfPathHolder + "");
            assert pdfPathHolder != null;
            fileSchema = new File(pdfPathHolder);
            Images image = new Images(uri, fileSchema);
            imageList.add(image);
            imageAdapter.notifyDataSetChanged();
        }
    }

}
