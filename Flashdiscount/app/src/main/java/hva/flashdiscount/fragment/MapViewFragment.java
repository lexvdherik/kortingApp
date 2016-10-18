package hva.flashdiscount.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.service.EstablishmentService;
import hva.flashdiscount.service.GpsTracker;

/**
 * Created by chrisvanderheijden on 10/10/2016.
 */

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private Location location;
    private GpsTracker gpsTracker;
    private ArrayList<Establishment> establishments;
    private EstablishmentService establishmentService;
    private boolean loaded = false;

    public void setLoaded(boolean b){
        this.loaded = b;
    }

    public boolean isLoaded(){
        return this.loaded;
    }

    public static MapViewFragment newInstance(int page, String title) {
        MapViewFragment mapViewFragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putInt("0", page);
        args.putString("Map", title);
        mapViewFragment.setArguments(args);
        return mapViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gpsTracker = new GpsTracker(getActivity());
        context = getActivity();
        establishmentService = new EstablishmentService(context, 1, this.getParentFragment());

     //   gpsTracker.startReceivingLocationUpdates();
        //location = gpsTracker.getLocation();


        // check if GPS enabled
        if (gpsTracker.canGetLocation()) {
            Log.e("turst", gpsTracker.getLocation().toString());
            location = new Location(gpsTracker.getLocation());

            location.setLatitude(gpsTracker.getLatitude());
            location.setLongitude(gpsTracker.getLongitude());

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }


    //    location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        page = getArguments().getInt("0", 0);
//        title = getArguments().getString("Map");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        //mMapView = (MapView) getChildFragmentManager().findFragmentById(R.id.mapView).getView();
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //For showing a move to my location button


                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng current = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if(isLoaded() == true){
                    addEstablishmentMarkers();
                } else{
                    setLoaded(true);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        establishments = new ArrayList<>();

        establishments = establishmentService.getEstablishments();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void addEstablishmentMarkers() {
        for (int i = 0; i < establishments.size(); i++) {
            createMarker(
                    establishments.get(i).getCompany().getName(),
                    establishments.get(i).getLatitude(),
                    establishments.get(i).getLongitude()
            );
        }
    }

    protected Marker createMarker(String title, double latitude, double longitude) {

        return googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).anchor(0.5f, 0.5f).title(title));
    }

}
