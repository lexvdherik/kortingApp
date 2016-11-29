package hva.flashdiscount;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.fragment.TabFragment;
import hva.flashdiscount.model.User;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context contextOfApplication;
    public User user;
    public boolean hasShownLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextOfApplication = getApplicationContext();

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        MaterialMenuInflater
                .with(this)
                .setDefaultColor(R.color.colorPrimary)
                .inflate(R.menu.activity_main_drawer, toolbar.getMenu());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabFragment tabFragment = new TabFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, tabFragment);
        ft.commit();
    }

    public Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabFragment()).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "BACK THROUGH NAV");
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
//            case R.id.action_settings:
//                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_discounts) {
            Log.i(TAG, "nav_discounts");
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabFragment()).commit();
        } else if (id == R.id.nav_myDiscounts) {
            Log.i(TAG, "nav_myDiscounts");
        } else if (id == R.id.nav_favorites) {
            Log.i(TAG, "nav_favorites");
        } else if (id == R.id.nav_account_settings) {
            Log.i(TAG, "nav_account_settings");


            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();

            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
