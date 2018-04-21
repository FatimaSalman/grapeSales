package com.example.fatima.grapeapplication.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.OfferAdapter;
import com.example.fatima.grapeapplication.adapter.ShopAdapter;
import com.example.fatima.grapeapplication.callback.InstallCallback;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.Shop;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Offer> offerList = new ArrayList<>();
    private Shop shop;
    private ProgressBar progressBar;
    private ConnectionManager connectionManager;
    private OfferAdapter shopAdapter;
    private ImageView progressbar;
    private TextView noTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        connectionManager = new ConnectionManager(this);
        shop = (Shop) getIntent().getSerializableExtra("shop");
        init();
    }

    public void init() {

        progressbar = findViewById(R.id.waitProgress);
        noTxt = findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        RoundedImageView shopImage = findViewById(R.id.shopImage);
        progressBar = findViewById(R.id.progressBar);
        TextView shopNameTxt = findViewById(R.id.shopNameTxt);
        shopNameTxt.setText(shop.getName());
        TextView shopAddressTxt = findViewById(R.id.shopAddressTxt);
        shopAddressTxt.setText(shop.getAddress());
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(this).load(FontManager.IMAGE_URL + shop.getShopImage())
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
        RelativeLayout addLayout = findViewById(R.id.addLayout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        addLayout.setOnClickListener(this);
        backLayout.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        shopAdapter = new OfferAdapter(this, offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ShopDetailsActivity.this, updateOfferActivity.class);
                intent.putExtra("offer_id", offerList.get(position).getId());
                startActivityForResult(intent, 12);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(shopAdapter);
        getShopList();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.addLayout) {
            Intent intent = new Intent(ShopDetailsActivity.this, AddOfferActivity.class);
            intent.putExtra("shop_id", shop.getId());
            startActivityForResult(intent, 12);
        } else if (id == R.id.backLayout) {
            finish();
        }
    }

    public void getShopList() {
        offerList.clear();
        progressbar.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);
        Offer offer = new Offer();
        offer.setShop_id(shop.getId());
        connectionManager.getOfferList(offer, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {

                if (status.equals("[]")) {
                    progressbar.setVisibility(View.GONE);
                    noTxt.setVisibility(View.VISIBLE);
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(status);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String offer_name = jsonObject.getString("offer_name");
                            String offer_bio = jsonObject.getString("offer_bio");
                            String befor_discount = jsonObject.getString("befor_discount");
                            String after_discount = jsonObject.getString("after_discount");
                            String offer_image = jsonObject.getString("offer_image");
                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, offer_image, shop_id);
                            offerList.add(offer);
                            progressbar.setVisibility(View.GONE);
                            noTxt.setVisibility(View.GONE);
                            shopAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                        noTxt.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String error) {
                progressbar.setVisibility(View.GONE);
                noTxt.setVisibility(View.GONE);
                AppErrorsManager.showErrorDialog(ShopDetailsActivity.this, error);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && data != null) {
            getShopList();
        }
    }
}
