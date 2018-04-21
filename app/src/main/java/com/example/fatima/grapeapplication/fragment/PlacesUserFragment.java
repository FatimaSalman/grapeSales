package com.example.fatima.grapeapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.activity.OfferListActivity;
import com.example.fatima.grapeapplication.activity.ShopListForUserActivity;

public class PlacesUserFragment extends Fragment implements View.OnClickListener {

    private String category;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_places, container, false);
        Bundle arguments = getArguments();
        category = arguments.getString("category");
        Log.e("category", category);
        init(view);
        return view;
    }


    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);
        switch (category) {
            case "Fashion":
                title.setText(getString(R.string.cloth));
                break;
            case "Shops":
                title.setText(getString(R.string.shops));
                break;
            case "Houseware":
                title.setText(getString(R.string.house));
                break;
            case "sports tool":
                title.setText(getString(R.string.sport));
                break;
            case "furniture":
                title.setText(getString(R.string.furth));
                break;
            case "Restaurants":
                title.setText(getString(R.string.resturance));
                break;
        }
        RelativeLayout gazaLayout = view.findViewById(R.id.gazaLayout);
        RelativeLayout jabaliLayout = view.findViewById(R.id.jabaliLayout);
        RelativeLayout bitLayout = view.findViewById(R.id.bitLayout);
        RelativeLayout middleLayout = view.findViewById(R.id.middleLayout);
        RelativeLayout khanLayout = view.findViewById(R.id.khanLayout);
        RelativeLayout rafahLayout = view.findViewById(R.id.rafahLayout);
        gazaLayout.setOnClickListener(this);
        jabaliLayout.setOnClickListener(this);
        bitLayout.setOnClickListener(this);
        middleLayout.setOnClickListener(this);
        khanLayout.setOnClickListener(this);
        rafahLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.gazaLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Gaza");
            startActivity(intent);
        } else if (id == R.id.jabaliLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Jabalia");
            startActivity(intent);
        } else if (id == R.id.bitLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Beit Hanoun");
            startActivity(intent);
        } else if (id == R.id.middleLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Central");
            startActivity(intent);
        } else if (id == R.id.khanLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Khan Younes");
            startActivity(intent);
        } else if (id == R.id.rafahLayout) {
            Intent intent = new Intent(getActivity(), ShopListForUserActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("city", "Rafah");
            startActivity(intent);
        }
    }


}
