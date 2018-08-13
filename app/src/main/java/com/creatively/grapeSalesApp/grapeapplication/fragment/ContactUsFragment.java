package com.creatively.grapeSalesApp.grapeapplication.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;

import java.util.Objects;

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        TextView title = Objects.requireNonNull(getActivity()).findViewById(R.id.registerTitle);
        title.setText(getString(R.string.contact_us));
        ImageView ic_facebook = view.findViewById(R.id.ic_facebook);
        ic_facebook.setOnClickListener(this);
        ImageView ic_instagram = view.findViewById(R.id.ic_instagram);
        ic_instagram.setOnClickListener(this);
        ImageView ic_twitter = view.findViewById(R.id.ic_twitter);
        ic_twitter.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ic_facebook) {
            try {
                Objects.requireNonNull(getActivity()).getPackageManager().getPackageInfo("com.facebook.katana", 0);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/grapesalesapp"));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/grapesalesapp"));
                startActivity(intent);
            }
        } else if (id == R.id.ic_twitter) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Grapes_App"));
            startActivity(intent);
        } else if (id == R.id.ic_instagram) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/grapesalesapp/"));
            startActivity(intent);
        }
    }
}
