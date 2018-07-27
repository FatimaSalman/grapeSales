package com.creatively.grapeSalesApp.grapeapplication.fragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.OrderDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.OrderUserDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.adapter.OrderAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrdersUserFragment extends Fragment {

    private String token;
    private ConnectionManager connectionManager;
    private List<Order> orderList = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private ImageView progressbar;
    private TextView noTxt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        token = AppPreferences.getString(getActivity(), "token");
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        return view;
    }


    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);

        title.setText(getString(R.string.order));


        progressbar = view.findViewById(R.id.waitProgress);
        noTxt = view.findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        orderAdapter = new OrderAdapter(getActivity(), orderList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OrderUserDetailsActivity.class);
                intent.putExtra("order_id", orderList.get(position).getId());
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderAdapter);
        getOrderList(token);

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getOrderList(token);
            }
        });
    }

    public void getOrderList(String token) {
        orderList.clear();
        progressbar.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);
        connectionManager.allUserOrderList(token, new InstallCallback() {
            @Override
            public void onStatusDone(String result) {
                if (result.equals("[]")) {
                    noTxt.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String shop = jsonObject.getString("shop");
                            String status = jsonObject.getString("status");
                            String created_at = jsonObject.getString("created_at");
                            JSONObject shopObject = new JSONObject(shop);
                            String shop_name = shopObject.getString("shop_name");

                            String offer = jsonObject.getString("offer");
                            JSONObject offerObject = new JSONObject(offer);
                            String offer_name = offerObject.getString("offer_name");


                            String offer_image = offerObject.getString("offer_image");
                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);
                            Order order = new Order(id, shop_name, offer_name, image, created_at, status);
                            orderList.add(order);
                            progressbar.setVisibility(View.GONE);
                            noTxt.setVisibility(View.GONE);
                            orderAdapter.notifyDataSetChanged();
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
                AppErrorsManager.showErrorDialog(getActivity(), error);
            }
        });

    }
}
