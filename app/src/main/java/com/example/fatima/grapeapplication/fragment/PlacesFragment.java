package com.example.fatima.grapeapplication.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
public class PlacesFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout layout;
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
                title.setText(getString(R.string.furth_));
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
        layout = view.findViewById(R.id.layout);
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
            replaceFragment(new ShopsFragment(), "Gaza");
        } else if (id == R.id.jabaliLayout) {
            replaceFragment(new ShopsFragment(), "Jabalia");
        } else if (id == R.id.bitLayout) {
            replaceFragment(new ShopsFragment(), "Beit Hanoun");
        } else if (id == R.id.middleLayout) {
            replaceFragment(new ShopsFragment(), "Central");
        } else if (id == R.id.khanLayout) {
            replaceFragment(new ShopsFragment(), "Khan Younes");
        } else if (id == R.id.rafahLayout) {
            replaceFragment(new ShopsFragment(), "Rafah");
        }
    }

    public void replaceFragment(Fragment fragment, String data) {
        layout.setVisibility(View.GONE);
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        Bundle arguments = new Bundle();
        arguments.putString("city", data);
        arguments.putString("category", category);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}
