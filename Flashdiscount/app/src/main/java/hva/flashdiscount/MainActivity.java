package hva.flashdiscount;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.Crashlytics;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import hva.flashdiscount.fragment.DetailFragment;
import hva.flashdiscount.fragment.MapViewFragment;
import hva.flashdiscount.fragment.ScannerFragment;
import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.fragment.TabFragment;
import hva.flashdiscount.layout.RoundNetworkImageView;
import hva.flashdiscount.model.User;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.VolleySingleton;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 101;

    private static final String TAG = MainActivity.class.getSimpleName();
    public User user;
    ActionBarDrawerToggle toggle;
    private Context contextOfApplication;
    private LoginSingleton loginSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextOfApplication = getApplicationContext();
        loginSingleton = LoginSingleton.getInstance(this);

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        MaterialMenuInflater
                .with(this)
                .setDefaultColor(R.color.colorPrimary)
                .inflate(R.menu.activity_main_drawer, toolbar.getMenu());
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
                    Log.e(TAG, "login expiredddd");
                    User user = loginSingleton.silentLogin();
                    if (user != null) {
                        LinearLayout layout = (LinearLayout) findViewById(R.id.nav_header);

                        ImageLoader mImageLoader = VolleySingleton.getInstance(contextOfApplication).getImageLoader();
                        RoundNetworkImageView image = (RoundNetworkImageView) layout.findViewById(R.id.profile_picture);
                        if (image != null) {
                            image.setImageUrl(user.getPicture().toString(), mImageLoader);
                        }

                        ((TextView) layout.findViewById(R.id.naam)).setText(user.getName());
                        ((TextView) layout.findViewById(R.id.email)).setText(user.getEmail());
                    }
                } else if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
                    Log.e(TAG, "load everything from sharedpref");
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.nav_header);

                    ImageLoader mImageLoader = VolleySingleton.getInstance(contextOfApplication).getImageLoader();
                    ((RoundNetworkImageView) layout.findViewById(R.id.profile_picture)).setImageUrl(sharedPref.getString("picture", ""), mImageLoader);
                    ((TextView) layout.findViewById(R.id.naam)).setText(sharedPref.getString("name", ""));
                    ((TextView) layout.findViewById(R.id.email)).setText(sharedPref.getString("email", ""));

                } else {
                    Log.e(TAG, "show the dialoggg");
                    loginSingleton.showLoginDialog();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.e(TAG, " " + String.valueOf(newState));
                if (newState == drawer.STATE_SETTLING) {
                    try {
                        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detailfrag");
                        ScannerFragment sf = (ScannerFragment) getSupportFragmentManager().findFragmentByTag("scannerfrag");

                        if (df.isVisible() || sf.isVisible()) {
                            onBackPressed();
                            drawer.closeDrawer(GravityCompat.START);
                        }

                    } catch (NullPointerException e) {

                    }

                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabFragment tabFragment = new TabFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

            try {
                DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detailfrag");

                if (df.isVisible()) {
                    getSupportFragmentManager().popBackStack();

                    TabFragment tb = new TabFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tb).commit();
                    toggle.syncState();


                } else {
                    super.onBackPressed();
                }

            } catch (NullPointerException e) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.i(TAG, "BACK THROUGH NAV");
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
////            case R.id.action_settings:
////                return true;
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions.length != 1) {
            Log.e(TAG, permissions.toString());
            Log.i(TAG, "Permissions denied");
            return;
        }

        switch (permissions[0]) {
            case Manifest.permission.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DetailFragment fragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detailfrag");
                    FloatingActionButton button = (FloatingActionButton) fragment.getView().findViewById(R.id.claim_button);
                    button.callOnClick();
                    return;
                } else {
                    Toast.makeText(getContextOfApplication(), R.string.no_permission_camera, Toast.LENGTH_LONG).show();
                    return;
                }
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapViewFragment mapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_fragment);
                    if (mapViewFragment != null) {
                        mapViewFragment.isGoogleMapsLocationTracking();
                        mapViewFragment.zoomToLocation(null);
                    }
                    return;
                } else {
                    Toast.makeText(getContextOfApplication(), R.string.no_permission_location, Toast.LENGTH_LONG).show();
                    return;
                }
            default:
                Log.e(TAG, "onRequestPermissionsResult: " + grantResults[0]);
                Log.e(TAG, "onRequestPermissionsResult: " + permissions[0]);
                Toast.makeText(getContextOfApplication(), "Permission " + grantResults[0] + " for " + permissions[0], Toast.LENGTH_LONG).show();
        }
    }
}
