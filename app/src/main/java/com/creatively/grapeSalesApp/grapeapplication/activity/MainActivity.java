package com.creatively.grapeSalesApp.grapeapplication.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.adapter.ItemAdapter;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.callback.RegisterCallback;
import com.creatively.grapeSalesApp.grapeapplication.fragment.CategoriesFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.ContactUsFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.OffersFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.OrdersFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.ProfileFragment;
import com.creatively.grapeSalesApp.grapeapplication.fragment.WelcomeFragment;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.model.MenuItem;
import com.creatively.grapeSalesApp.grapeapplication.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private List<MenuItem> menuItemList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);
        connectionManager = new ConnectionManager(this);
        init();
        if (!AppPreferences.getString(this, "token").equals("0")) {
            if (AppPreferences.getString(this, "type").equals("1")) {
                Intent intent = new Intent(this, MainUserActivity.class);
                startActivity(intent);
                finish();
            } else if (getIntent().getStringExtra("is_active") != null) {
                if (getIntent().getStringExtra("is_active").equals("active"))
                    getUserDetails();
            } else getUserDetails();
        }

        if (AppPreferences.getString(this, "type").equals("1")) {
            Intent intent = new Intent(this, MainUserActivity.class);
            startActivity(intent);
            finish();
        }
        String more = getIntent().getStringExtra("more");
        if (TextUtils.equals(more, "more")) {
            itemAdapter.selectPosition = 0;
            replaceFragment(new CategoriesFragment());
        }
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

        RelativeLayout searchLayout = findViewById(R.id.searchLayout);
        searchLayout.setOnClickListener(this);
        RelativeLayout menuLayout = findViewById(R.id.menuLayout);
        menuLayout.setOnClickListener(this);
        replaceFragment(new OffersFragment());
        RecyclerView recyclerView = navigationView.findViewById(R.id.nav_drawer_recycler_view);
        itemAdapter = new ItemAdapter(this, menuItemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemAdapter.selectPosition = position;
                itemAdapter.notifyDataSetChanged();
                if (TextUtils.equals(menuItemList.get(position).getId(), "0")) {
                    if (!AppPreferences.getString(MainActivity.this, "token").equals("0")) {
                        replaceFragment(new CategoriesFragment());
                        toggleSlidingMenu();
                    } else {
                        Intent intent = new Intent(MainActivity.this, SelectTypeActivity.class);
                        startActivity(intent);
                    }
                } else if (TextUtils.equals(menuItemList.get(position).getId(), "1")) {
                    replaceFragment(new OffersFragment());
                    toggleSlidingMenu();

                } else if (TextUtils.equals(menuItemList.get(position).getId(), "2")) {
                    replaceFragment(new OrdersFragment());
                    toggleSlidingMenu();

                } else if (AppPreferences.getString(MainActivity.this, "token").equals("0")) {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "3")) {
                        Intent intent = new Intent(MainActivity.this, SelectTypeActivity.class);
                        startActivity(intent);
//                        replaceFragment(new WelcomeFragment());
//                        toggleSlidingMenu();
                    }
                } else {
                    if (TextUtils.equals(menuItemList.get(position).getId(), "4")) {
                        replaceFragment(new ProfileFragment());
                        toggleSlidingMenu();
                    }
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "5")) {
                    if (AppPreferences.getString(MainActivity.this, "token").equals("0")) {
                        Intent intent = new Intent(MainActivity.this, SelectTypeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, PointActivity.class);
                        startActivity(intent);
                    }
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "6")) {
                    replaceFragment(new ContactUsFragment());
                    toggleSlidingMenu();
                }
                if (TextUtils.equals(menuItemList.get(position).getId(), "7")) {
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
        menuItem = new MenuItem("5", "جمع نقاط");
        menuItemList.add(menuItem);
        menuItem = new MenuItem("6", "اتصل بنا");
        menuItemList.add(menuItem);
        if (!AppPreferences.getString(this, "token").equals("0")) {
            menuItem = new MenuItem("7", "تسجيل الخروج");
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(navigationView)) {
            mDrawerLayout.closeDrawer(navigationView);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public void getUserDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.getDetails(AppPreferences.getString(MainActivity.this, "token"), new RegisterCallback() {
            @Override
            public void onUserRegisterDone(User user) {
                progressDialog.dismiss();
                AppPreferences.saveString(MainActivity.this, "active", user.getIs_active());

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(MainActivity.this, error);
            }
        });

    }


}
