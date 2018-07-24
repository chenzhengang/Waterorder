package com.example.qiupys.wateroder;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class User extends AppCompatActivity {
    private UserDataManager userDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        Fragment current=new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,current).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (userDataManager == null) {
            userDataManager = new UserDataManager(this);
            userDataManager.openDatabase();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment;
                    selectedFragment = new FavoritesFragment();

                    switch (item.getItemId()){
                        case R.id.nav_goods:
                            selectedFragment = new GoodsFragment();
                            break;
                        case R.id.nav_aks:
                            selectedFragment = new AksFragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment();
                            break;
//                        case R.id.nav_orders:
//                            selectedFragment = new OrdersFragment();
//                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };
}