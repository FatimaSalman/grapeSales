package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.adapter.OfferAdapterDisplay;
import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;
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
    private OfferAdapterDisplay shopAdapter;
    private ImageView progressbar;
    private TextView noTxt, shopNameTxt, shopAddressTxt, mobileTxt;
    private EditText offerEditText;
    private RecyclerView recyclerView;
    private String shop_id;
    private RoundedImageView shopImage;
    private RatingBar ratingStart;
    private ScrollView scrollView;

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
        shop_id = getIntent().getStringExtra("shop_id");
        init();
    }

    public void init() {
        ratingStart = findViewById(R.id.rating);
        scrollView = findViewById(R.id.scrollView);
        progressbar = findViewById(R.id.waitProgress);
        offerEditText = findViewById(R.id.offerEditText);
        noTxt = findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        shopImage = findViewById(R.id.shopImage);
        progressBar = findViewById(R.id.progressBar);
        shopNameTxt = findViewById(R.id.shopNameTxt);
        shopAddressTxt = findViewById(R.id.shopAddressTxt);
        mobileTxt = findViewById(R.id.mobileTxt);
        if (shop != null) {
            shopNameTxt.setText(shop.getName());
            shopAddressTxt.setText(shop.getAddress());
            mobileTxt.setText(shop.getShop_phone());
            progressBar.setVisibility(View.VISIBLE);
            ratingStart.setRating(Float.parseFloat(shop.getRating()));
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
        } else {
            scrollView.setVisibility(View.GONE);
            getShopDetails();
        }
        RelativeLayout addLayout = findViewById(R.id.addLayout);
        addLayout.setVisibility(View.GONE);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        ImageButton searchBtoon = findViewById(R.id.searchBtoon);
        searchBtoon.setOnClickListener(this);
        shopAdapter = new OfferAdapterDisplay(this, offerList, new OnItemClickListener() {
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
        if (shop != null)
            offer.setShop_id(shop.getId());
        else
            offer.setShop_id(shop_id);
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
                            String rating = jsonObject.getString("rating");

                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);

                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, "");
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
        String id;
        if (shop != null)
            id = shop.getId();
        else
            id = shop_id;
        connectionManager.getOfferStoreSearch(id, offerName, new InstallCallback() {
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
                            String rating = jsonObject.getString("rating");

                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);

                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, "");
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


    public void getShopDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.getShopDetails(shop_id, new InstallCallback() {
            @Override
            public void onStatusDone(String result) {
                try {
                    scrollView.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(result);
//                    String id = jsonObject.getString("id");
                    String shop_name = jsonObject.getString("shop_name");
                    String shop_address = jsonObject.getString("shop_address");
                    String shop_phone = jsonObject.getString("shop_phone");
//                    String shop_bio = jsonObject.getString("shop_bio");
//                    String user_id = jsonObject.getString("user_id");
//                    String is_active = jsonObject.getString("is_active");
                    String image_url = jsonObject.getString("image_url");
//                    String category_name = jsonObject.getString("category_name");
//                    String city_name = jsonObject.getString("city_name");
//                    String count = jsonObject.getString("count");
                    String rating = jsonObject.getString("rating");
                    ratingStart.setRating(Float.parseFloat(rating));
                    shopNameTxt.setText(shop_name);
                    shopAddressTxt.setText(shop_address);
                    mobileTxt.setText(shop_phone);
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.with(ShopDetailsUserActivity.this).load(FontManager.IMAGE_URL + image_url)
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

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(ShopDetailsUserActivity.this, error);
            }
        });

    }
}
