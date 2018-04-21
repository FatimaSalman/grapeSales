package com.example.fatima.grapeapplication.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.activity.AddShopActivity;
import com.example.fatima.grapeapplication.activity.OfferDetailsActivity;
import com.example.fatima.grapeapplication.activity.ShopDetailsActivity;
import com.example.fatima.grapeapplication.adapter.OfferAdapter;
import com.example.fatima.grapeapplication.adapter.ShopAdapter;
import com.example.fatima.grapeapplication.callback.InstallCallback;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.AppPreferences;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OffersFragment extends Fragment {
    private List<Offer> offerList = new ArrayList<>();
    private ConnectionManager connectionManager;
    private OfferAdapter shopAdapter;
    private ImageView progressbar;
    private TextView noTxt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        return view;
    }


    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);
        title.setText(getString(R.string.current_offer));

        progressbar = view.findViewById(R.id.waitProgress);
        progressbar.setVisibility(View.VISIBLE);
        noTxt = view.findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        shopAdapter = new OfferAdapter(getActivity(), offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OfferDetailsActivity.class);
                intent.putExtra("offer_id", offerList.get(position).getId());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);
        getBestOffer();
    }

    public void getBestOffer() {
        offerList.clear();
        noTxt.setVisibility(View.GONE);

        connectionManager.offerList(new InstallCallback() {
            @Override
            public void onStatusDone(String status) {
                if (status.equals("[]")) {
                    noTxt.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
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
                AppErrorsManager.showErrorDialog(getActivity(), error);
            }
        });

    }

}
