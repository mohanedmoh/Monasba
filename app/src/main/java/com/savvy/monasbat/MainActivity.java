package com.savvy.monasbat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Usage_requirements.OnFragmentInteractionListener,
        Home.OnFragmentInteractionListener, Aboutus.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener, FAQ.OnFragmentInteractionListener {
    DrawerLayout drawer;
    boolean home = true;
    SharedPreferences shared;
    boolean doubleBackToExitPressedOnce = false;
    Fragment fragment = null;
    Class fragmentClass = null;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        drawer = findViewById(R.id.drawer_layout);
        // actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.side_nav_bar);

        shared = this.getSharedPreferences("com.savvy.monasbat", Context.MODE_PRIVATE);
        setSupportActionBar(toolbar);
        //change the name and the phone
        //changeNavLabels();
        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle b = new Bundle();
        Home h = new Home();


        try {
            if (BuildConfig.DEBUG) {
               // throw new AssertionError("Assertion failed");
            }
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedInstanceState == null) {

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, h).commit();
        setTitleT(getResources().getString(R.string.home));
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        changeNavLabels();
    }

    private void setTitleT(String string) {
        ((TextView) findViewById(R.id.title)).setText(string);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (home) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                    return;
                }
            } else {
                MenuItem item = ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(0);
                setTitleT(getResources().getString(R.string.home));
                onNavigationItemSelected(item);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.press_back), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
    }

    private void changeNavLabels() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.side_name)).setText(shared.getString("name", "No name"));
        ((TextView) headerView.findViewById(R.id.side_phone)).setText(shared.getString("phone", "No phone"));
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {   // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        fragment = null;
        fragmentClass = null;

        if (id == R.id.nav_profile) {
            fragmentClass = Profile.class;
            setTitleT(getResources().getString(R.string.profile));
            home = false;

        } else if (id == R.id.nav_home) {
            setTitleT(getResources().getString(R.string.home));
            fragmentClass = Home.class;
            home = true;

        } else if (id == R.id.nav_aboutus) {
            setTitleT(getResources().getString(R.string.aboutus));
            fragmentClass = Aboutus.class;
            home = false;

        } else if (id == R.id.nav_faq) {
            setTitleT(getResources().getString(R.string.faq));
            fragmentClass = FAQ.class;
            home = false;

        } else if (id == R.id.nav_agreement) {
            setTitleT(getResources().getString(R.string.agreement));
            fragmentClass = Usage_requirements.class;
            home = false;

        } else if (id == R.id.nav_sign_out) {
            shared.edit().putBoolean("login", false).apply();
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            finish();
        }



        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
