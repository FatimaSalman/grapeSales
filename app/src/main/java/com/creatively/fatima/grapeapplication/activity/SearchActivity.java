package com.creatively.fatima.grapeapplication.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.fatima.grapeapplication.R;
import com.creatively.fatima.grapeapplication.adapter.ShopAdapter;
import com.creatively.fatima.grapeapplication.adapter.ShopSearchAdapter;
import com.creatively.fatima.grapeapplication.adapter.SpinnerAdapter;
import com.creatively.fatima.grapeapplication.callback.InstallCallback;
import com.creatively.fatima.grapeapplication.callback.OnItemClickListener;
import com.creatively.fatima.grapeapplication.manager.AppErrorsManager;
import com.creatively.fatima.grapeapplication.manager.ConnectionManager;
import com.creatively.fatima.grapeapplication.manager.FontManager;
import com.creatively.fatima.grapeapplication.model.Offer;
import com.creatively.fatima.grapeapplication.model.Shop;
import com.creatively.fatima.grapeapplication.model.SpinnerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<SpinnerItem> cityList = new ArrayList<>();
    private ArrayList<SpinnerItem> categoryList = new ArrayList<>();
    private Dialog alertDialog;
    private String categoryId = "", cityId = "";
    private TextView byCateogry, byCity, noTxt;
    private EditText byExhibtion;
    private ConnectionManager connectionManager;
    private ImageView waitProgress;
    private List<Shop> shopList = new ArrayList<>();
    private ShopSearchAdapter shopAdapter;
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
        setContentView(R.layout.activity_search);
        connectionManager = new ConnectionManager(this);
        cityItemList();
        categoryItemList();
        init();
    }

    public void init() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        byCateogry = findViewById(R.id.byCateogry);
        byCity = findViewById(R.id.byCity);
        byExhibtion = findViewById(R.id.exhibtionEditText);
        byExhibtion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    FontManager.hideKeyboard(SearchActivity.this);
                    getShopList();
                    return true;
                }
                return false;
            }
        });
        noTxt = findViewById(R.id.noTxt);
        waitProgress = findViewById(R.id.waitProgress);
        ObjectAnimator animation = ObjectAnimator.ofFloat(waitProgress, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
        recyclerView = findViewById(R.id.recyclerView);
        ImageButton searchBtoon = findViewById(R.id.searchBtoon);

        byCateogry.setOnClickListener(this);
        searchBtoon.setOnClickListener(this);
        byCity.setOnClickListener(this);

        final SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getShopList();
            }
        });

        shopAdapter = new ShopSearchAdapter(this, shopList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SearchActivity.this, ShopDetailsUserActivity.class);
                intent.putExtra("shop", shopList.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(shopAdapter);
    }

    public void cityItemList() {
        SpinnerItem spinnerItem = new SpinnerItem("Gaza", getString(R.string.gaza));
        cityList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Jabalia", getString(R.string.jabalia));
        cityList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Beit Hanoun", getString(R.string.bitHanona));
        cityList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Central", getString(R.string.middle));
        cityList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Khan Younes", getString(R.string.khan_younis));
        cityList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Rafah", getString(R.string.rafah));
        cityList.add(spinnerItem);
    }

    public void categoryItemList() {
        SpinnerItem spinnerItem = new SpinnerItem("Fashion", getString(R.string.cloth));
        categoryList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Shops", getString(R.string.shops));
        categoryList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Houseware", getString(R.string.house));
        categoryList.add(spinnerItem);
        spinnerItem = new SpinnerItem("sports tool", getString(R.string.sport));
        categoryList.add(spinnerItem);
        spinnerItem = new SpinnerItem("furniture", getString(R.string.furth_));
        categoryList.add(spinnerItem);
        spinnerItem = new SpinnerItem("Restaurants", getString(R.string.resturance));
        categoryList.add(spinnerItem);
    }

    public void openWindowCategories() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(popupView);
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view1);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(SearchActivity.this,
                categoryList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;

                status = categoryList.get(position).getText();

                categoryId = categoryList.get(position).getId();
                byCateogry.setText(status);
                byCateogry.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    public void openWindowCity() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(popupView);
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view1);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(SearchActivity.this,
                cityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;

                status = cityList.get(position).getText();

                cityId = cityList.get(position).getId();
                byCity.setText(status);
                byCity.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    public void getShopList() {
        String exhibtionName = byExhibtion.getText().toString().trim();
        recyclerView.setVisibility(View.GONE);
        shopList.clear();
        waitProgress.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);

        connectionManager.getShopSearch(cityId, categoryId, exhibtionName, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {
                if (status.equals("[]")) {
                    noTxt.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    waitProgress.setVisibility(View.GONE);
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(status);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String shop_name = jsonObject.getString("shop_name");
                            String shop_address = jsonObject.getString("shop_address");
                            String shop_phone = jsonObject.getString("shop_phone");
                            String shop_bio = jsonObject.getString("shop_bio");
                            String user_id = jsonObject.getString("user_id");
                            String is_active = jsonObject.getString("is_active");
                            String image_url = jsonObject.getString("image_url");
                            String category_name = jsonObject.getString("category_name");
                            String city_name = jsonObject.getString("city_name");
                            String count = jsonObject.getString("count");
                            Shop shop1 = new Shop(id, shop_name, shop_address, count, image_url, shop_phone);
                            shopList.add(shop1);
                            recyclerView.setVisibility(View.VISIBLE);
                            waitProgress.setVisibility(View.GONE);
                            noTxt.setVisibility(View.GONE);
                            shopAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        waitProgress.setVisibility(View.GONE);
                        noTxt.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String error) {
                waitProgress.setVisibility(View.GONE);
                noTxt.setVisibility(View.GONE);
                AppErrorsManager.showErrorDialog(SearchActivity.this, error);
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.byCateogry) {
            openWindowCategories();
        } else if (id == R.id.byCity) {
            openWindowCity();
        } else if (id == R.id.searchBtoon) {
            getShopList();
        }
    }
}
