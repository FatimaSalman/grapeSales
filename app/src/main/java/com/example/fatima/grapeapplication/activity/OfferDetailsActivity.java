package com.example.fatima.grapeapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.OfferAdapter;
import com.example.fatima.grapeapplication.callback.OfferCallback;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OfferDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ConnectionManager connectionManager;
    private String offer_id;
    private RoundedImageView shopImage;
    private ProgressBar progressBar;
    private TextView shopNameTxt, shopAddressTxt, offerPreviousPrice, offerNextPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        connectionManager = new ConnectionManager(this);
        offer_id = getIntent().getStringExtra("offer_id");
        init();
    }

    public void init() {

        RelativeLayout backLayout = findViewById(R.id.backLayout);

        Button backBtn = findViewById(R.id.backBtn);
        backLayout.setOnClickListener(this);
        Button orderBtn = findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        shopImage = findViewById(R.id.shopImage);
        progressBar = findViewById(R.id.progressBar);
        shopNameTxt = findViewById(R.id.shopNameTxt);
        shopAddressTxt = findViewById(R.id.shopAddressTxt);
        offerPreviousPrice = findViewById(R.id.offerPreviousPrice);
        offerNextPrice = findViewById(R.id.offerNextPrice);
        getOfferDetails();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout || id == R.id.backBtn) {
            finish();
        } else if (id == R.id.orderBtn) {
            Intent intent = new Intent(this, UserApplyFragment.class);
            startActivity(intent);
        }
    }

    public void getOfferDetails() {
        Offer offer = new Offer();
        offer.setId(offer_id);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.getOfferDetails(offer, new OfferCallback() {
            @Override
            public void onOfferDone(Offer offer1) {
                progressDialog.dismiss();
                shopNameTxt.setText(offer1.getOfferTitle());
                offerPreviousPrice.setText(offer1.getPreviousPrice());
                offerPreviousPrice.setPaintFlags(offerPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                offerNextPrice.setText(offer1.getNextPrice());
                shopAddressTxt.setText(offer1.getOfferBio());
                progressBar.setVisibility(View.VISIBLE);
                Picasso.with(OfferDetailsActivity.this)
                        .load(FontManager.IMAGE_URL + offer1.getOfferImage())
                        .into(shopImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(OfferDetailsActivity.this, error);
            }
        });

    }
}
