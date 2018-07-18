package com.creatively.grapeSalesApp.grapeapplication.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;

public class ContactUsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        TextView title = getActivity().findViewById(R.id.registerTitle);
        title.setText(getString(R.string.contact_us));
    }
}
