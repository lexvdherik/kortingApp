package hva.flashdiscount.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.google.gson.Gson;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.BottomDiscountAdapter;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.service.GpsService;


public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();
    private MapView mapView;
    private GoogleMap googleMap;
    private Location location;
    private GpsService mGpsService;
    private BottomSheetBehavior mBottomSheetBehavior1;
//    private BottomSheetBehavior mBottomSheetBehavior2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGpsService = new GpsService(getActivity());

        location = new Location("");
        location.setLatitude(52.375368);
        location.setLongitude(4.894486);

        if (!mGpsService.checkPermission()) {
            mGpsService.askLocationPermission();
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        FragmentManager fm = getFragmentManager();
//        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//        if (!sharedPref.contains("idToken") && !((MainActivity) getActivity()).hasShownLogin) {
//            LoginDialogFragment dialogFragment = new LoginDialogFragment();
//            dialogFragment.show(fm, "Login Fragment");
//            ((MainActivity) getActivity()).hasShownLogin = true;
//        }
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        //View bottomSheet = getView().findViewById(R.id.bottom_sheet);
        final View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
//        final View bottomSheetMultiple = rootView.findViewById(R.id.bottom_sheet_multiple_discounts);

        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        // mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheetMultiple);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (mGpsService.checkPermission()) {
                    googleMap.setMyLocationEnabled(true);
                }

                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final Establishment establishment = (Establishment) marker.getTag();
                        final ListView listView = (ListView) rootView.findViewById(R.id.discount_list_view);
                        BottomDiscountAdapter adapter = new BottomDiscountAdapter(establishment.getDiscounts(), getActivity());

                        if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            TextView title = (TextView) rootView.findViewById(R.id.title_bottom_sheet);
                            title.setText(establishment.getCompany().getName());

                        } else if (mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

                            TextView title = (TextView) rootView.findViewById(R.id.title_bottom_sheet);
                            TextView description = (TextView) rootView.findViewById(R.id.description);


                            title.setText(establishment.getCompany().getName());
                            description.setText(String.valueOf(establishment.getDiscounts().size()));

                        } else {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }

                        if (establishment.getDiscounts().size() > 1) {

                            listView.setNestedScrollingEnabled(true);
                            listView.setAdapter(adapter);

//                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                                    //    int itemPosition = position;
//                                    Discount value = (Discount) listView.getItemAtPosition(position);
//                                    value.toString();
//                                }
//                            });

                        } else {
                            adapter.clear();
                            listView.setAdapter(adapter);
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

    private void updateLocation() {
        location = (mGpsService.getLocation() == null) ? location : mGpsService.getLocation();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        updateLocation();
        if (location != null && googleMap != null) {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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


    @Deprecated
    private void goToDetailView(Establishment establishment) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));

        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
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
                Log.w(TAG, "No connection!");
            }
        }

    }
}