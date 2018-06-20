package com.example.fatima.grapeapplication.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.OfferAdapter;
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
import java.util.Locale;

public class ShopDetailsUserActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Offer> offerList = new ArrayList<>();
    private Shop shop;
    private ProgressBar progressBar;
    private ConnectionManager connectionManager;
    private OfferAdapter shopAdapter;
    private ImageView progressbar;
    private TextView noTxt;
    private EditText offerEditText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_shop_details);
        connectionManager = new ConnectionManager(this);
        shop = (Shop) getIntent().getSerializableExtra("shop");
        init();
    }

    public void init() {
        progressbar = findViewById(R.id.waitProgress);
        offerEditText = findViewById(R.id.offerEditText);
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
        TextView mobileTxt = findViewById(R.id.mobileTxt);
        shopAddressTxt.setText(shop.getAddress());
        mobileTxt.setText(shop.getShop_phone());
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
        addLayout.setVisibility(View.GONE);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        ImageButton searchBtoon = findViewById(R.id.searchBtoon);
        searchBtoon.setOnClickListener(this);
        shopAdapter = new OfferAdapter(this, offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ShopDetailsUserActivity.this, OfferDetailsActivity.class);
                intent.putExtra("offer_id", offerList.get(position).getId());
                intent.putExtra("shop_id", offerList.get(position).getShop_id());
                startActivityForResult(intent, 12);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(shopAdapter);
        getOfferList();

        final SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getOfferList();
            }
        });
        offerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    FontManager.hideKeyboard(ShopDetailsUserActivity.this);
                    getofferSearchList();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.searchBtoon) {
            getofferSearchList();
        }
    }

    public void getOfferList() {
        offerList.clear();
        recyclerView.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);
        Offer offer = new Offer();
        offer.setShop_id(shop.getId());
        connectionManager.getOfferList(offer, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {

                if (status.equals("[]")) {
                    recyclerView.setVisibility(View.GONE);
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

                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);

                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id);
                            offerList.add(offer);
                            recyclerView.setVisibility(View.VISIBLE);
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
                AppErrorsManager.showErrorDialog(ShopDetailsUserActivity.this, error);
            }
        });

    }

    public void getofferSearchList() {
        offerList.clear();
        recyclerView.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);
        String offerName = offerEditText.getText().toString().trim();
        connectionManager.getOfferSearch(offerName, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {

                if (status.equals("[]")) {
                    recyclerView.setVisibility(View.GONE);
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

                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);

                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id);
                            offerList.add(offer);
                            recyclerView.setVisibility(View.VISIBLE);
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
                AppErrorsManager.showErrorDialog(ShopDetailsUserActivity.this, error);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && data != null) {
            getOfferList();
        }
    }
}
