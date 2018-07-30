package com.creatively.grapeSalesApp.grapeapplication.fragment;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.AddShopActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.ShopDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ItemAdapter;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ShopAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopsFragment extends Fragment implements View.OnClickListener {
    private List<Shop> shopList = new ArrayList<>();
    private String category, city, user_id;
    private ConnectionManager connectionManager;
    private ShopAdapter shopAdapter;
    private ImageView progressbar;
    private TextView noTxt;
    private String is_active;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shops_list, container, false);
        Bundle arguments = getArguments();
        category = arguments.getString("category");
        city = arguments.getString("city");
        user_id = AppPreferences.getString(getActivity(), "user_id");
        is_active = AppPreferences.getString(getActivity(), "active");
        Log.e("category", category + " // " + is_active + user_id);
        connectionManager = new ConnectionManager(getActivity());
        init(view);
        return view;
    }


    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);
        switch (city) {
            case "Gaza":
                title.setText(getString(R.string.gaza));
                break;
            case "Jabalia":
                title.setText(getString(R.string.jabalia));
                break;
            case "Beit Hanoun":
                title.setText(getString(R.string.bitHanona));
                break;
            case "Central":
                title.setText(getString(R.string.middle));
                break;
            case "Khan Younes":
                title.setText(getString(R.string.khan_younis));
                break;
            case "Rafah":
                title.setText(getString(R.string.rafah));
                break;
        }

        progressbar = view.findViewById(R.id.waitProgress);
        noTxt = view.findViewById(R.id.noTxt);
        ObjectAnimator animation = ObjectAnimator.ofFloat(progressbar, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        final RelativeLayout addLayout = view.findViewById(R.id.addLayout);
        addLayout.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerView);
        shopAdapter = new ShopAdapter(getActivity(), shopList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                int id = view.getId();
                if (id == R.id.layout) {
                    Intent intent = new Intent(getActivity(), ShopDetailsActivity.class);
                    intent.putExtra("shop", shopList.get(position));
                    startActivity(intent);
                } else if (id == R.id.ic_remove) {
                    AppErrorsManager.showSuccessDialog(getActivity(),
                            "عملية الحذف", " هل تريد حذف هذا المعرض؟", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteShop(shopList.get(position).getId());
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                } else if (id == R.id.ic_edit) {
                    Intent intent = new Intent(getActivity(), AddShopActivity.class);
                    intent.putExtra("shop", shopList.get(position));
                    startActivityForResult(intent, 12);

                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(shopAdapter);
        if (!user_id.equals("0")) {
            getShopList();
            addLayout.setVisibility(View.VISIBLE);
        } else {
            progressbar.setVisibility(View.GONE);
            noTxt.setVisibility(View.VISIBLE);
            noTxt.setText("يجب تسجيل الدخول او الاشتراك لاضافة معرض");
            addLayout.setVisibility(View.GONE);
        }

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                if (!user_id.equals("0")) {
                    getShopList();
                    addLayout.setVisibility(View.VISIBLE);
                } else {
                    progressbar.setVisibility(View.GONE);
                    noTxt.setVisibility(View.VISIBLE);
                    noTxt.setText("يجب تسجيل الدخول او الاشتراك لاضافة معرض");
                    addLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.addLayout) {
            if (!is_active.equals("0")) {
                Intent intent = new Intent(getActivity(), AddShopActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("city", city);
                startActivityForResult(intent, 12);
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.account_not_active));
            }
        }
    }

    public void getShopList() {
        shopList.clear();
        recyclerView.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        noTxt.setVisibility(View.GONE);
        Shop shop = new Shop();
        shop.setUser_id(user_id);
        shop.setCategory_name(category);
        shop.setCity_name(city);
        connectionManager.getShopList(shop, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {
                if (status.equals("[]")) {
                    noTxt.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
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
                            String record_no = jsonObject.getString("record_no");
                            String user_id = jsonObject.getString("user_id");
                            String is_active = jsonObject.getString("is_active");
                            String image_url = jsonObject.getString("image_url");
                            String category_name = jsonObject.getString("category_name");
                            String city_name = jsonObject.getString("city_name");
                            String count = jsonObject.getString("count");
                            String rating = jsonObject.getString("rating");
                            String seenTxt = jsonObject.getString("seen_no");
                            Shop shop1 = new Shop(id, shop_name, shop_address, count, image_url,
                                    shop_phone, shop_bio, record_no, rating, seenTxt);
                            shopList.add(shop1);
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
                AppErrorsManager.showErrorDialog(getActivity(), error);
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

    public void deleteShop(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.deleteShop(id, new InstallCallback() {
            @Override
            public void onStatusDone(String status) {
                progressDialog.dismiss();
                AppErrorsManager.showSuccessDialog(getActivity(), status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getShopList();
                    }
                });
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(getActivity(), error);
            }
        });

    }
}
