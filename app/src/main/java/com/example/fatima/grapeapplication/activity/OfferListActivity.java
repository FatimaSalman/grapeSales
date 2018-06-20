package com.example.fatima.grapeapplication.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.OfferListAdapter;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.model.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OfferListActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Offer> offerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_offer_list);
        init();
    }

    public void init() {

        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        final SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getShopList();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        OfferListAdapter shopAdapter = new OfferListAdapter(this, offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(OfferListActivity.this, OfferDetailsActivity.class);
                startActivity(intent);
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
        if (id == R.id.backLayout) {
            finish();
        }
    }

    public void getShopList() {
//        Offer offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);
//        offer = new Offer("0", "معرض قصر الضيافة", "غزة - شارع ديغول - مقابل قصر الحاكم", "168", "200", "");
//        offerList.add(offer);

    }
}
