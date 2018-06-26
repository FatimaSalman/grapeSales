package com.creatively.fatima.grapeapplication.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.fatima.grapeapplication.R;
import com.creatively.fatima.grapeapplication.activity.AddShopActivity;
import com.creatively.fatima.grapeapplication.activity.OfferDetailsActivity;
import com.creatively.fatima.grapeapplication.activity.ShopDetailsActivity;
import com.creatively.fatima.grapeapplication.activity.ShopDetailsUserActivity;
import com.creatively.fatima.grapeapplication.adapter.OfferAdapter;
import com.creatively.fatima.grapeapplication.adapter.ShopAdapter;
import com.creatively.fatima.grapeapplication.callback.InstallCallback;
import com.creatively.fatima.grapeapplication.callback.OnItemClickListener;
import com.creatively.fatima.grapeapplication.manager.AppErrorsManager;
import com.creatively.fatima.grapeapplication.manager.AppPreferences;
import com.creatively.fatima.grapeapplication.manager.ConnectionManager;
import com.creatively.fatima.grapeapplication.manager.FontManager;
import com.creatively.fatima.grapeapplication.model.Offer;
import com.creatively.fatima.grapeapplication.model.Shop;

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
    private RecyclerView recyclerView;
    private EditText offerEditText;

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
        ImageButton searchBtoon = view.findViewById(R.id.searchBtoon);
        searchBtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getofferSearchList();
            }
        });
        progressbar = view.findViewById(R.id.waitProgress);
        progressbar.setVisibility(View.VISIBLE);
        noTxt = view.findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        recyclerView = view.findViewById(R.id.recyclerView);
        shopAdapter = new OfferAdapter(getActivity(), offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OfferDetailsActivity.class);
                intent.putExtra("offer_id", offerList.get(position).getId());
                intent.putExtra("shop_id", offerList.get(position).getShop_id());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);
        getBestOffer();
        offerEditText = view.findViewById(R.id.offerEditText);

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                getBestOffer();
            }
        });

        offerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    FontManager.hideKeyboard(getActivity());
                    getofferSearchList();
                    return true;
                }
                return false;
            }
        });
    }

    public void getBestOffer() {
        offerList.clear();
        progressbar.setVisibility(View.VISIBLE);
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
                            String rating = jsonObject.getString("rating");
                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);
                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, "");
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
                            String rating = jsonObject.getString("rating");
                            String offer_image = jsonObject.getString("offer_image");

                            JSONArray jsonArray2 = new JSONArray(offer_image);
                            String image = jsonArray2.getString(0);

                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, "");
                            offerList.add(offer);
                            progressbar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
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
