package hva.flashdiscount.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.gms.vision.text.Line;
import com.google.gson.Gson;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
//import hva.flashdiscount.adapter.CustomInfoWindowAdapter;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.service.GpsService;

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();
    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private Location location;
    private GpsService gpsService;
    private BottomSheetBehavior mBottomSheetBehavior1;

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
        final View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        FragmentManager fm = getFragmentManager();
        LoginDialogFragment dialogFragment = new LoginDialogFragment();
        dialogFragment.show(fm, "Login Fragment");

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        //View bottomSheet = getView().findViewById(R.id.bottom_sheet);
        final View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);

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

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final Establishment establishment = (Establishment) marker.getTag();


                        if(mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            TextView title = (TextView) rootView.findViewById(R.id.title_bottom_sheet);
                            title.setText(establishment.getCompany().getName());
                        }
                        else if(mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                            //mButton1.setText(R.string.collapse_button1);
                            TextView title = (TextView) rootView.findViewById(R.id.title_bottom_sheet);
                            title.setText(establishment.getCompany().getName());

                        }
                        else {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                          //  mButton1.setText(R.string.button1);
                        }



                        return true;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });

                getEstablishmentsFromAPI();
            }
        });

        return rootView;
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
            Marker marker = googleMap.addMarker(new MarkerOptions().position(establishment.getLocation()).anchor(0.5f, 0.5f));
            marker.setTag(establishment);
            return marker;
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
                createMarker(establishment);
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