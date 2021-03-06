package hva.flashdiscount.fragment;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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

import java.util.ArrayList;
import java.util.List;

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.BottomDiscountAdapter;
import hva.flashdiscount.adapter.CategoryAdapter;
import hva.flashdiscount.model.Category;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.service.GpsService;


public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();
    public CategoryAdapter categoryAdapter;
    public ArrayList<Category> categories = new ArrayList<>();
    public SparseArray<List<Marker>> markerHashMap = new SparseArray<>();
    MapView mMapView;
    AlertDialog dialog;
    private TabFragment tabFragment;
    private GoogleMap googleMap;
    private Location location;
    private GpsService mGpsService;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private ListView listView;
    private View bottomSheet;
    private Establishment establishment;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int nrOfDiscounts = listAdapter.getCount(), totalHeight = 0;
        for (int i = 0; i < ((nrOfDiscounts > 2) ? 2 : nrOfDiscounts); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if (nrOfDiscounts == 1) {
            totalHeight += 160;
        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) - 100;
        listView.setLayoutParams(params);
        listView.requestLayout();


    }

    public SparseArray getMarkerHashMap() {
        return markerHashMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mGpsService = new GpsService(getActivity());

        location = new Location("");
        location.setLatitude(52.375368);
        location.setLongitude(4.894486);

        if (!mGpsService.checkPermission()) {
            mGpsService.askLocationPermission();
        }

        getCategoriesFromAPI();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.toolbar_filter_button:
                filterMarkers();
                return false;
            default:
                break;
        }

        return false;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        initAttributes(rootView);
        mMapView.onCreate(savedInstanceState);
        //  categoryAdapter = new CategoryAdapter(getContext(), R.layout.category_list_child, categories, this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // getEstablishmentsFromAPI();
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (mGpsService.checkPermission()) {
                    googleMap.setMyLocationEnabled(true);
                }


                zoomToLocation(null);
                googleMap.setOnMarkerClickListener(getMarkerListener(rootView));


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
                getCategoriesFromAPI();
                getEstablishmentsFromAPI();
            }
        });

        return rootView;
    }


    private GoogleMap.OnMarkerClickListener getMarkerListener(final View rootView) {
        return new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                establishment = (Establishment) marker.getTag();
                BottomDiscountAdapter adapter = new BottomDiscountAdapter(establishment.getDiscounts(), getContext());
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                CardView detailLayout = (CardView) rootView.findViewById(R.id.card_view_discount);


                if (establishment.getDiscounts().size() == 1) {
                    detailLayout.setVisibility(View.VISIBLE);
                    TextView claimsText = (TextView) detailLayout.findViewById(R.id.claims_left);
                    claimsText.setText(establishment.getDiscounts().get(0).getAmountRemaining());
                    TextView timeText = (TextView) detailLayout.findViewById(R.id.time_left);
                    timeText.setText(establishment.getDiscounts().get(0).getTimeRemaining(getContext()));
                } else {
                    detailLayout.setVisibility(View.GONE);
                }

                if (establishment.getDiscounts().size() == 0) {
                    adapter.clear();
                } else {
                    listView.setAdapter(adapter);
                }

                listView = setListViewListeners(listView);

                setListViewHeightBasedOnChildren(listView);

                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return true;
                }
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                TextView bottomSheetTitle = (TextView) bottomSheet.findViewById(R.id.title_bottom_sheet);
                bottomSheetTitle.setText(establishment.getCompany().getName());
                TextView bottomSheetDescription = (TextView) bottomSheet.findViewById(R.id.description);
                bottomSheetDescription.setText(String.valueOf(establishment.getStreet() + " " + establishment.getStreetnumber() + ", " + establishment.getCity()));
                return true;
            }
        };
    }

    private ListView setListViewListeners(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToDetailView(establishment, i);
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return listView;
    }

    public void filterMarkers() {
        Log.d(TAG, "filterMarkers: " + "Filter start");
    }

    public void toggleSelectedMarkers(Category category, boolean checked) {
//        dialog.dismiss();
        //Get List from
        if (markerHashMap.get(category.getCategoryId()) != null) {
            for (Marker mark : markerHashMap.get(category.getCategoryId())) {
                Log.i("MARKERCHECK", mark.getTag().toString());
                mark.setVisible(checked);
            }
        }

        Log.i(TAG, "Hashmap = " + markerHashMap.toString());
    }

    private void initAttributes(View rootView) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        listView = (ListView) rootView.findViewById(R.id.discount_list_view);
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

    protected void createMarker(Establishment establishment) {

        MarkerOptions est = new MarkerOptions().position(establishment.getLocation()).anchor(0.5f, 0.5f);

        Marker marker = googleMap.addMarker(est);
        marker.setTag(establishment);
        Log.i("TESTSEAN", establishment.getCompany().getName() + establishment.getCompany().getCategoryId());

        if (markerHashMap.indexOfKey(establishment.getCompany().getCategoryId()) < 0) {
            markerHashMap.put(establishment.getCompany().getCategoryId(), new ArrayList<Marker>());
            markerHashMap.get(establishment.getCompany().getCategoryId()).add(marker);
        } else {
            markerHashMap.get(establishment.getCompany().getCategoryId()).add(marker);
        }

    }

    protected void createCategory(Category category) {

        categories.add(category);
    }

    private void getEstablishmentsFromAPI() {
        MapViewFragment.GetEstablishmentResponseListener listener = new MapViewFragment.GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);
    }

    public void getCategoriesFromAPI() {
        System.gc();
        GetCategoryResponseListener listener = new GetCategoryResponseListener();
        APIRequest.getInstance(getContext()).getCategories(listener, listener);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "OnMarkerClick");
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MapView mapView = (MapView) getView().findViewById(R.id.mapView);
        mapView.onResume();

        updateLocation();
        if (location != null && googleMap != null) {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void updateLocation() {
        location = (mGpsService.getLocation() == null) ? location : mGpsService.getLocation();
    }

    public void zoomToLocation(@Nullable Location location) {
        LatLng current = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        if (location != null) {
            current = new LatLng(location.getLatitude(), location.getLongitude());
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void goToDetailView(Establishment establishment, int discountPosition) {


        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPosition", discountPosition);
        arguments.putBoolean("dialog", false);
        arguments.putBoolean("succes", false);

        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_fromleft, R.anim.slide_out_toright)
                .replace(R.id.fragment_container, detailFragment, "detailfrag")
                .addToBackStack(null)
                .commit();
    }

    public void isGoogleMapsLocationTracking() {
        if (mGpsService.checkPermission()) {
            googleMap.setMyLocationEnabled(true);
        }
    }


    public class GetEstablishmentResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(Establishment[] establishments) {
            for (Establishment establishment : establishments) {
                Log.i(TAG, "Received Establishment category: " + (Integer.toString(establishment.getCompany().getCategoryId())));
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

    public class GetCategoryResponseListener implements Response.Listener<Category[]>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

        @Override
        public void onResponse(Category[] categories) {
            for (Category category : categories) {
                Log.i(TAG, "Response with: " + category.toString());
                createCategory(category);
            }
        }
    }


}

