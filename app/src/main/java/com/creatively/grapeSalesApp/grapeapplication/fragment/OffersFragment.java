package com.creatively.grapeSalesApp.grapeapplication.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainUserActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.OfferDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.SelectTypeActivity;
import com.creatively.grapeSalesApp.grapeapplication.adapter.OfferAdapterBest;
import com.creatively.grapeSalesApp.grapeapplication.adapter.OfferAdapterDisplay;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OffersFragment extends Fragment implements View.OnClickListener {
    private List<Offer> offerList = new ArrayList<>();
    private ConnectionManager connectionManager;
    private OfferAdapterBest shopAdapter;
    private ImageView progressbar;
    private TextView noTxt;
    private RecyclerView recyclerView;
    private EditText offerEditText;
    private Button moreBtn;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        TextView title = Objects.requireNonNull(getActivity()).findViewById(R.id.registerTitle);
        title.setText(getString(R.string.current_offer));
        ImageButton searchBtoon = view.findViewById(R.id.searchBtoon);
        searchBtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOfferSearchList();
            }
        });
        progressbar = view.findViewById(R.id.waitProgress);
        moreBtn = view.findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(this);
        progressbar.setVisibility(View.VISIBLE);
        noTxt = view.findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        recyclerView = view.findViewById(R.id.recyclerView);
        shopAdapter = new OfferAdapterBest(getActivity(), offerList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OfferDetailsActivity.class);
                intent.putExtra("offer_id", offerList.get(position).getId());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(shopAdapter);
        getBestOffer();
        offerEditText = view.findViewById(R.id.offerEditText);

//        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefresh.setRefreshing(false);
//                getBestOffer();
//            }
//        });

        offerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    FontManager.hideKeyboard(getActivity());
                    getOfferSearchList();
                    return true;
                }
                return false;
            }
        });
    }

    public void getBestOffer() {
        offerList.clear();
        moreBtn.setVisibility(View.GONE);
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
                            String shop = jsonObject.getString("shop");
                            JSONObject shopJson = new JSONObject(shop);
                            String shop_name = shopJson.getString("shop_name");
                            String seen_no = shopJson.getString("seen_no");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, seen_no, shop_name);
                            offerList.add(offer);
                            progressbar.setVisibility(View.GONE);
                            noTxt.setVisibility(View.GONE);
                            shopAdapter.notifyDataSetChanged();
                            moreBtn.setVisibility(View.VISIBLE);
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

    public void getOfferSearchList() {
        offerList.clear();
        moreBtn.setVisibility(View.GONE);
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
                            String shop = jsonObject.getString("shop");
                            JSONObject shopJson = new JSONObject(shop);
                            String shop_name = shopJson.getString("shop_name");
                            String seen_no = shopJson.getString("seen_no");
                            String shop_id = jsonObject.getString("shop_id");
                            Offer offer = new Offer(id, offer_name, offer_bio, befor_discount,
                                    after_discount, image, shop_id, rating, seen_no, shop_name);
                            offerList.add(offer);
                            progressbar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            noTxt.setVisibility(View.GONE);
                            shopAdapter.notifyDataSetChanged();
                            moreBtn.setVisibility(View.VISIBLE);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.moreBtn) {
            if (AppPreferences.getString(getActivity(), "token").equals("0")) {
                Intent intent = new Intent(getActivity(), SelectTypeActivity.class);
                startActivity(intent);
            } else {
                Log.e("type", AppPreferences.getString(getActivity(), "type"));
                if (AppPreferences.getString(getActivity(), "type").equals("0")) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("more", "more");
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                } else if (AppPreferences.getString(getActivity(), "type").equals("1")) {
                    Intent intent = new Intent(getActivity(), MainUserActivity.class);
                    intent.putExtra("more", "more");
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }

            }
        }
    }
}
