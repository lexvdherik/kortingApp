package hva.flashdiscount;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import hva.flashdiscount.fragment.DetailFragment;
import hva.flashdiscount.fragment.MapViewFragment;
import hva.flashdiscount.fragment.NavigationDrawerFragment;
import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.fragment.TabFragment;
import hva.flashdiscount.model.User;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.TransactionHandler;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TransactionHandler.FragmentTransactionHandler {

    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 101;

    private static final String TAG = MainActivity.class.getSimpleName();
    public User user;
    public Toolbar mToolbar;
    private LoginSingleton loginSingleton;
    private boolean hasShownLogin = false;
    private int mDrawerPosition = -1;
    private boolean mIsFragmentHandlingMenus = false;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginSingleton = LoginSingleton.getInstance(this);
        if (!loginSingleton.loggedIn()) {
            if (!loginSingleton.loginExpired()) {
                loginSingleton.silentLogin();
            }
        }
//        loginSingleton.silentLogin();

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TabFragment tabFragment = new TabFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, tabFragment, "tab_fragment");
        ft.commit();

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
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_frombottom, 0)
                            .replace(R.id.fragment_container, tb)
                            .commit();
                } else {
                    back();
                }

            } catch (NullPointerException e) {

            }
            try {
                SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("settingsfrag");
                if (sf.isVisible()) {
                    getSupportFragmentManager().popBackStack();
                    TabFragment tb = new TabFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_frombottom, 0)
                            .replace(R.id.fragment_container, tb)
                            .commit();
                } else {
                    back();
                }
            } catch (NullPointerException e) {
            }
        }

    }
    public void back() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else if (mDrawerPosition > 0) { // Else, if not on the home page, go back to the home page
            forceChangeItemSelected(0);
        } else { // Otherwise, let the system handle this back press
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions.length != 1) {
            Log.i(TAG, permissions.toString());
            Log.e(TAG, "Permissions denied");
            return;
        }

        switch (permissions[0]) {
            case Manifest.permission.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DetailFragment fragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag("detailfrag");
                    FloatingActionButton button = (FloatingActionButton) fragment.getView().findViewById(R.id.claim_button);
                    return;
                }

                Toast.makeText(getApplicationContext(), R.string.no_permission_camera, Toast.LENGTH_LONG).show();
                return;

            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapViewFragment mapViewFragment = (MapViewFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_fragment);
                    if (mapViewFragment != null) {
                        mapViewFragment.isGoogleMapsLocationTracking();
                        mapViewFragment.zoomToLocation(null);
                    }
                    return;
                }

                Toast.makeText(getApplicationContext(), R.string.no_permission_location, Toast.LENGTH_LONG).show();
                return;

            default:
                Log.i(TAG, "onRequestPermissionsResult: " + grantResults[0]);
                Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
                Toast.makeText(getApplicationContext(), "Permission " + grantResults[0] + " for " + permissions[0], Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        changeItemSelected(position);
    }

    private void changeItemSelected(int newPos) {
        // If old position = new position, do nothing
        if (mDrawerPosition == newPos) {
            return;
        }

        // If position was -1, state that there was an error then fix the issue
        if (newPos == -1) {
            Log.d(TAG, "changeItemSelected(pos) was given -1. Fixing issue for now");
            newPos = 0;
        }

        // First, update the main content by replacing fragments
        Fragment newFrag = null;
        String name = null;

        //-> Choosing which fragment to show logic

        Log.e(TAG, "blah - " + String.valueOf(newPos));
        switch (newPos) {
            case 0:
                if (!loginSingleton.loggedIn()) {
                    loginSingleton.login();
                }
                break;

            case 1:
                // Tab frag
                newFrag = new TabFragment();
                break;
            case 2:
                // Settings frag
                newFrag = new SettingsFragment();
                name = "settingsfrag";
                break;

            default:
                // OTHERWISE, there was a big mistake
                Log.e(TAG, "changeItemSelected(pos: " + newPos + "): Invalid position");
                newFrag = new TabFragment();
                break;
//                return;
        }

        if (newPos != 0) {
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_fromleft, R.anim.slide_out_toright)
                    .replace(R.id.fragment_container, newFrag, name)
                    .addToBackStack(null)
                    .commit();
        }

        // Finally, save that this was the latest position set
        mDrawerPosition = newPos;
    }

    @Override
    public void changeFragment(TransactionHandler.RequestType requestType, boolean addToBackstack) {
        // Simply call on changeFragment with option 0
        changeFragment(requestType, addToBackstack, 0);
    }

    // TODO: Delete above and below methods, or make them actually useful
    @Override
    public void changeFragment(TransactionHandler.RequestType requestType, boolean addToBackstack, int option) {
        if (requestType == TransactionHandler.RequestType.MAIN_DRAWER) {
            // Simply do a force main content change [don't really care yet for backstack here yet]
            forceChangeItemSelected(option);
        }
//                else if(requestType == TransactionHandler.RequestType.GOAL_ADDER) {
//                    Toast.makeText(this, "Want the Goal Adder? Too bad", Toast.LENGTH_SHORT).show();
//
//                    android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                    Fragment fragment = new GoalAdderFragment();
//
//                    // Lower level fragment should transition horizontally
//                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_fromright,
//                            R.animator.slide_out_toleft);
//                    fragmentTransaction.replace(R.id.container, fragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                }
    }

    @Override
    public void fragmentHandlingMenus(boolean isFragmentHandlingMenus,
                                      View.OnClickListener newHomeButtonListener) {
        // Simply store the setting
        mIsFragmentHandlingMenus = isFragmentHandlingMenus;

        // Toggle the drawer as necessary
        mNavigationDrawerFragment.toggleDrawerUse(!isFragmentHandlingMenus, newHomeButtonListener);
    }

    // Changes both the drawer position as well as the content frag position
    private void forceChangeItemSelected(int position) {
        mNavigationDrawerFragment.setSelectedItem(position);
        changeItemSelected(position);
    }

}

