package com.creatively.grapeSalesApp.grapeapplication.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainUserActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.WelcomeActivity;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;

import java.util.Objects;

public class WelcomeFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout loginLayout, registerLayout;
    private TextView registerTxt, loginTxt;
    private String type;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_welcome, container, false);
        init(view);
        type = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("type");
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init(View view) {
        Toolbar toolBar = view.findViewById(R.id.toolBar);
        toolBar.setVisibility(View.GONE);
        TextView title = Objects.requireNonNull(getActivity()).findViewById(R.id.registerTitle);
        title.setText(getString(R.string.register_page));

        loginLayout = view.findViewById(R.id.loginLayout);
        registerTxt = view.findViewById(R.id.registerTxt);
        loginTxt = view.findViewById(R.id.loginTxt);
        registerLayout = view.findViewById(R.id.registerLayout);
        loginLayout.setOnClickListener(this);
        registerLayout.setOnClickListener(this);
        showOtherFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerLayout) {
            loginLayout.setBackground(null);
            loginTxt.setTypeface(FontManager.getTypefaceTextInputRegular(Objects.requireNonNull(getActivity())));
            registerTxt.setTypeface(FontManager.getTypefaceTextInputBold(getActivity()));
            registerLayout.setBackground(getActivity().getDrawable(R.drawable.bg_item_tab));
            Log.e("type///", type);
            replaceFragment(new RegisterFragment(), type);
        } else if (id == R.id.loginLayout) {
            registerLayout.setBackground(null);
            registerTxt.setTypeface(FontManager.getTypefaceTextInputRegular(Objects.requireNonNull(getActivity())));
            loginTxt.setTypeface(FontManager.getTypefaceTextInputBold(getActivity()));
            loginLayout.setBackground(getActivity().getDrawable(R.drawable.bg_item_tab));
            replaceFragment(new LoginFragment(), type);
        }
    }

    public void replaceFragment(Fragment fragment, String data) {
        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        Bundle arguments = new Bundle();
        arguments.putString("type", data);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentReplace, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showOtherFragment() {
        registerLayout.setBackground(null);
        registerTxt.setTypeface(FontManager.getTypefaceTextInputRegular(Objects.requireNonNull(getActivity())));
        loginTxt.setTypeface(FontManager.getTypefaceTextInputBold(getActivity()));
        loginLayout.setBackground(getActivity().getDrawable(R.drawable.bg_item_tab));
        replaceFragment(new LoginFragment(), type);
    }
}
