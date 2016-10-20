package hva.flashdiscount;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

import hva.flashdiscount.fragment.DiscountListFragment;
import hva.flashdiscount.fragment.MapViewFragment;
import hva.flashdiscount.fragment.TabFragment;
import hva.flashdiscount.service.EstablishmentService;
import hva.flashdiscount.service.GpsTracker;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GpsTracker gpsTracker;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private String mPermissionFine = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private String mPermissionCourse = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);

        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, mPermissionFine)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{mPermissionFine}, REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        gpsTracker = new GpsTracker(MainActivity.this);
        // gpsTracker.showSettingsAlert();
        // gpsTracker.startReceivingLocationUpdates();
        // gpsTracker.getLocation();
        //Log.i("LOCATION", gpsTracker.getLocation().toString());

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discounts) {
            // Handle the camera action
        } else if (id == R.id.nav_myDiscounts) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_account_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onListDataChange(int place) {
//        List fragments = getSupportFragmentManager().getFragments();
//        switch (place){
//            case 0:
//                break;
//            case 1:
//                MapViewFragment mvf = (MapViewFragment) fragments.get(place);
//                if(mvf.isLoaded() == true){
//                    mvf.addEstablishmentMarkers();
//                } else{
//                    mvf.setLoaded(true);
//                }
//                break;
//            case 2:
//                final DiscountListFragment dlf = (DiscountListFragment) fragments.get(place);
//                dlf.fillList();
//
//                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.detach(dlf);
//                        ft.attach(dlf);
//                        ft.commit();
//                    }
//                });
//                swipeRefreshLayout.setRefreshing(false);
//                break;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                gpsTracker.getLocation();

            } else {
                // Failure Stuff
            }
        }
    }
}
