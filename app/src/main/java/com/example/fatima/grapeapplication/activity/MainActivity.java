package com.example.fatima.grapeapplication.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import android.support.design.widget.NavigationView;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.ItemAdapter;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.fragment.CategoriesFragment;
import com.example.fatima.grapeapplication.fragment.OffersFragment;
import com.example.fatima.grapeapplication.fragment.ProfileFragment;
import com.example.fatima.grapeapplication.fragment.WelcomeFragment;
import com.example.fatima.grapeapplication.manager.AppPreferences;
import com.example.fatima.grapeapplication.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        RelativeLayout menuLayout = findViewById(R.id.menuLayout);
        menuLayout.setOnClickListener(this);
        replaceFragment(new CategoriesFragment());
        RecyclerView recyclerView = navigationView.findViewById(R.id.nav_drawer_recycler_view);
        itemAdapter = new ItemAdapter(this, menuItemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemAdapter.selectPosition = position;
                itemAdapter.notifyDataSetChanged();
                if (TextUtils.equals(menuItemList.get(position).getId(), "0")) {
                    replaceFragment(new CategoriesFragment());
                    toggleSlidingMenu();
                } else if (TextUtils.equals(menuItemList.get(position).getId(), "1")) {
                    replaceFragment(new OffersFragment());
                    toggleSlidingMenu();

                } else if (AppPreferences.getString(MainActivity.this, "token").equals("0")) {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "2")) {
                        replaceFragment(new WelcomeFragment());
                        toggleSlidingMenu();
                    }
                } else {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "2")) {
                        replaceFragment(new ProfileFragment());
                        toggleSlidingMenu();
                    }
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "5")) {
                    AppPreferences.clearAll(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, SelectTypeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);
        menuItemTitle();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.menuLayout) {
            toggleSlidingMenu();
        }
    }

    private void toggleSlidingMenu() {
        if (mDrawerLayout.isDrawerOpen(navigationView)) {
            mDrawerLayout.closeDrawer(navigationView);
        } else {
            mDrawerLayout.openDrawer(navigationView);
        }
    }

    public void menuItemTitle() {
        MenuItem menuItem = new MenuItem("0", "الرئيسية");
        menuItemList.add(menuItem);
        menuItem = new MenuItem("1", "عروض حصرية");
        menuItemList.add(menuItem);
        if (AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("2", "صفحة التسجيل");
            menuItemList.add(menuItem);
        } else {
            menuItem = new MenuItem("2", "الملف الشخصي");
            menuItemList.add(menuItem);
        }
        menuItem = new MenuItem("3", "اتصل بنا");
        menuItemList.add(menuItem);
        menuItem = new MenuItem("4", "من نحن");
        menuItemList.add(menuItem);
        if (!AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("5", "تسجيل الخروج");
            menuItemList.add(menuItem);
        }

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}
