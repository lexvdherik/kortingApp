package hva.flashdiscount.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.service.GpsService;

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener{

    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private Location location;
    private GpsService gpsService;

    private static final String TAG = MapViewFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gpsService = new GpsService(getActivity());
        context = getActivity();

        if (gpsService.canGetLocation()) {
            location = new Location(gpsService.getLocation());

            location.setLatitude(gpsService.getLatitude());
            location.setLongitude(gpsService.getLongitude());
        } else {
            location = new Location("");
            location.setLatitude(52.375368);
            location.setLongitude(4.894486);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

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

                if (gpsService.checkWriteExternalPermission()) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    Log.e("nono", "no");
                }


                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Establishment establishment = (Establishment) marker.getTag();
                        goToDetailView(establishment);

                    }
                });
                getEstablishmentsFromAPI();
            }
        });

        return rootView;
    }

    private void goToDetailView(Establishment establishment){

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));

        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
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

    protected Marker createMarker(Establishment establishment) {
        if (establishment.getDiscounts().size() > 1) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(establishment.getLocation()).anchor(0.5f, 0.5f)
                    .title(establishment.getCompany().getName()).snippet(establishment.getDiscounts().size() + " kortingen"));
            marker.setTag(establishment);
            return marker;
        } else {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(establishment.getLocation()).anchor(0.5f, 0.5f)
                    .title(establishment.getCompany().getName()).snippet(establishment.getDiscounts().get(0).getDescription()));
            marker.setTag(establishment);
            return marker;
        }
    }

    private void getEstablishmentsFromAPI() {
        System.gc();
        MapViewFragment.GetEstablishmentResponseListener listener = new MapViewFragment.GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    public class GetEstablishmentResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(Establishment[] establishments) {
            for (Establishment establishment : establishments) {
                createMarker(
                        establishment
                );
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }
}