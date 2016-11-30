package hva.flashdiscount.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.R;

import static android.os.Binder.getCallingPid;
import static android.os.Binder.getCallingUid;

/**
 * Class to simplify GPS interactions.
 */
public class GpsService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = GpsService.class.getSimpleName();

    private final FragmentActivity activity;
    private GoogleApiClient googleApiClient;
    private Location location;


    private static final long TIME_BW_UPDATES = 1000 * 30; // 0,5 minute
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest mLocationRequest;


    /**
     * Constructor.
     *
     * @param fragmentActivity {@link Context} application context
     */
    public GpsService(FragmentActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        googleApiClient.disconnect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setInterval(TIME_BW_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public boolean checkPermission() {
        int fine;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fine = activity.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            fine = activity.getApplicationContext().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getCallingPid(), getCallingUid());
        }
        return (fine == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request permission for location.
     */
    public void askLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity.getApplicationContext().getApplicationContext(), R.string.location_explanation, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.REQUEST_LOCATION_PERMISSION);
        }

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.REQUEST_LOCATION_PERMISSION);
        }
    }


    /**
     * Get the users location.
     *
     * @return null|{@link Location} Location to use or null in case of failure
     */
    @Nullable
    public Location getLocation() {
        if (!checkPermission()) {
            Log.e(TAG, "No location permission when requesting location");
            return null;
        }
        createLocationRequest();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            googleApiClient.connect();
        }

        location = fusedLocationProviderApi.getLastLocation(googleApiClient);

        return location;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkPermission()) {
            //Permissions still valid
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, this);
        LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }
}
