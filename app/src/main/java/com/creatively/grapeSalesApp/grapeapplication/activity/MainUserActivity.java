package com.creatively.grapeSalesApp.grapeapplication.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ItemAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.fragment.CategoriesFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.CategoriesUserFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.ContactUsFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.OffersFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.OrdersFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.OrdersUserFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.ProfileFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.ShopsFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.WelcomeFragment;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.MenuItem;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MainUserActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionManager = new ConnectionManager(this);
        getFcmToken();
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
        RelativeLayout searchLayout = findViewById(R.id.searchLayout);
        searchLayout.setOnClickListener(this);
        replaceFragment(new CategoriesUserFragment());
        RecyclerView recyclerView = navigationView.findViewById(R.id.nav_drawer_recycler_view);
        itemAdapter = new ItemAdapter(this, menuItemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemAdapter.selectPosition = position;
                itemAdapter.notifyDataSetChanged();
                if (TextUtils.equals(menuItemList.get(position).getId(), "0")) {
                    replaceFragment(new CategoriesUserFragment());
                    toggleSlidingMenu();
                } else if (TextUtils.equals(menuItemList.get(position).getId(), "1")) {
                    replaceFragment(new OffersFragment());
                    toggleSlidingMenu();

                } else if (TextUtils.equals(menuItemList.get(position).getId(), "2")) {
                    replaceFragment(new OrdersUserFragment());
                    toggleSlidingMenu();

                } else if (AppPreferences.getString(MainUserActivity.this, "token").equals("0")) {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "3")) {
                        replaceFragment(new WelcomeFragment());
                        toggleSlidingMenu();
                    }
                } else {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "4")) {
                        replaceFragment(new ProfileFragment());
                        toggleSlidingMenu();
                    }
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "5")) {
                    replaceFragment(new ContactUsFragment());
                    toggleSlidingMenu();
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "6")) {
                    AppPreferences.clearAll(MainUserActivity.this);
                    Intent intent = new Intent(MainUserActivity.this, SelectTypeActivity.class);
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
        } else if (id == R.id.searchLayout) {
            startActivity(new Intent(this, SearchActivity.class));
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
        if (!AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("2", "الطلبات");
            menuItemList.add(menuItem);
        }
        if (AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("3", "صفحة التسجيل");
            menuItemList.add(menuItem);
        } else {
            menuItem = new MenuItem("4", "الملف الشخصي");
            menuItemList.add(menuItem);
        }
        menuItem = new MenuItem("5", "اتصل بنا");
        menuItemList.add(menuItem);
        if (!AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("6", "تسجيل الخروج");
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

//    @Override
//    public void onBackPressed() {
////        moveTaskToBack(true);
////        android.os.Process.killProcess(android.os.Process.myPid());
////        System.exit(1);
//    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(navigationView)) {
            mDrawerLayout.closeDrawer(navigationView);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public void getFcmToken() {
        connectionManager.addFcmToken(FirebaseInstanceId.getInstance().getToken(), new InstallCallback() {
            @Override
            public void onStatusDone(String status) {
                Log.e("data", status);
            }

            @Override
            public void onError(String error) {
                Log.e("error", error);
            }
        });
    }
}
