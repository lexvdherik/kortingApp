package hva.flashdiscount.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private Location location;
    private GpsService mGpsService;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private ListView listView;
    private ListView categoryListView;
    private TextView bottomSheettitle;
    private TextView bottomSheetdescription;
   // private ArrayList<Category> categories;
    private BottomDiscountAdapter adapter;
    private Establishment establishment;
    private  CategoryAdapter categoryAdapter;
    private List<Marker> cafeList = new ArrayList<>();
    private List<Marker> barList = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    private List<List<Marker>> dataList = new ArrayList<List<Marker>>();
    private HashMap<Integer, List<Marker>> markerHashMap = new HashMap<>();

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int nrOfDiscounts = listAdapter.getCount();
        if (nrOfDiscounts > 2) {
            nrOfDiscounts = 2;
        }
        int totalHeight = 0;
        for (int i = 0; i < nrOfDiscounts; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGpsService = new GpsService(getActivity());
        context = getActivity();

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        initAttributes(rootView);
        mMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button filterButton = (Button) rootView.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //view = new View(context);


                filterTheMarkers();

                //ListView categoryListView = (ListView) rootView.findViewById(R.id.cate);
            }
        });

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
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        establishment = (Establishment) marker.getTag();
                        adapter = new BottomDiscountAdapter(establishment.getDiscounts(), context);
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        CardView detailLayout = (CardView) rootView.findViewById(R.id.card_view_discount);


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

                        if (establishment.getDiscounts().size() > 1) {
                            detailLayout.setVisibility(View.GONE);
                            listView.setNestedScrollingEnabled(true);
                            listView.setAdapter(adapter);
                        } else if (establishment.getDiscounts().size() == 1) {
                            detailLayout.setVisibility(View.VISIBLE);
                            listView.setAdapter(adapter);
                        } else {
                            detailLayout.setVisibility(View.GONE);
                            adapter.clear();
                            listView.setAdapter(adapter);
                        }

                        setListViewHeightBasedOnChildren(listView);

                        if (mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bottomSheettitle.setText(establishment.getCompany().getName());
                            bottomSheetdescription.setText(String.valueOf(establishment.getStreet() + " " + establishment.getStreetNumber() + ", " + establishment.getCity()));
                        } else {
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                getCategoriesFromAPI();
                getEstablishmentsFromAPI();
                filterTheMarkers();


            }
        });

        return rootView;
    }

    AlertDialog dialog;
    CheckBox buses, trains;

    public void filterTheMarkers() {
        if (dialog == null) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View checkBoxView = inflater.inflate(R.layout.marker_selection, null);
           // View list = inflater.inflate(R.layout.category_list, null);

            categoryAdapter = new CategoryAdapter(context, R.layout.category_list_child, categories, this);

            categoryListView = (ListView) checkBoxView.findViewById(R.id.listview_categories);
            categoryListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            categoryListView.setAdapter(categoryAdapter);


            builder.setView(checkBoxView);


            buses = (CheckBox) checkBoxView.findViewById(R.id.checkBox1);
            //trains = (CheckBox) list.findViewById(R.id.checkBox2);
            Button okButton = (Button) checkBoxView.findViewById(R.id.okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displaySelectedMarkers();
                }
            });
//            Button cancelButton = (Button) checkBoxView.findViewById(R.id.cancelButton);
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
            dialog = builder.create();
        }
        dialog.show();
    }

    public void displaySelectedMarkers() {
        dialog.dismiss();


        //Get List from
        for(int i=0; i < categoryAdapter.getCount(); i++){

            if(categoryAdapter.getItemChecked(i)){

                for(Marker mark: markerHashMap.get(i + 1)){
                    mark.setVisible(true);
                }
            } else {
                for(Marker mark: markerHashMap.get(i + 1)){
                    mark.setVisible(false);
                }
            }


        }



        //int[] intArray = Arrays.stream(checkedItems).as
      //  Log.i("CHECKED", String.valueOf(checkedItems.length));
//        Log.i("CHECKED", String.valueOf(checkedCategoryIDs.length));
//
//        for(int i=0; i < checkedCategoryIDs.length; i++){
//
//            // int id = (int) array.get(i); //each selected ID
//       //     List<Marker> markers = markerHashMap.get(id);
////            if(markerHashMap.get(checkedCategoryIDs[i]) != null){
//                for(Marker mark: markerHashMap.get(checkedCategoryIDs[i])){
//                    mark.setVisible(false);
//              //  }
//            }
//        }
//
//        for(){}
//
//        for(int i =0; i < markerHashMap.size() + 1; i++){
//
//            for(int j =0; j <markerHashMap.get(i).size(); j++){
//                Marker mark = markerHashMap.get(i).get(j);
//
//                mark.setVisible(false);
//
//            }
//
//        }

//        for(Marker train : markerHashMap.get(1)){
//            train.setVisible(trains.isChecked());
//        }
//
//        for(Marker buss : markerHashMap.get(2)){
//            buss.setVisible(buses.isChecked());
//        }

        Log.i("HASHMAP", markerHashMap.toString());
    }

    public void doNothing(View view) { dialog.dismiss(); }

    private void initAttributes(View rootView) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        bottomSheettitle = (TextView) rootView.findViewById(R.id.title_bottom_sheet);
        bottomSheetdescription = (TextView) rootView.findViewById(R.id.description);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        listView = (ListView) rootView.findViewById(R.id.discount_list_view);
        categoryListView = (ListView) rootView.findViewById(R.id.listview_categories);
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

        if(!markerHashMap.containsKey(establishment.getCompany().getCategoryId())) {

            markerHashMap.put(establishment.getCompany().getCategoryId(), new ArrayList<Marker>());
            markerHashMap.get(establishment.getCompany().getCategoryId()).add(marker);

        } else {

            markerHashMap.get(establishment.getCompany().getCategoryId()).add(marker);

        }

    }

    protected void createCategory(Category category){

        categories.add(category);
    }

    private void getEstablishmentsFromAPI() {
        System.gc();
        MapViewFragment.GetEstablishmentResponseListener listener = new MapViewFragment.GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);
    }

    private void getCategoriesFromAPI(){
        System.gc();
        MapViewFragment.getCategoryResponseListener listener = new MapViewFragment.getCategoryResponseListener();
        APIRequest.getInstance(getActivity()).getCategories(listener, listener);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(TAG, "OnMarkerClick");
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
                Log.i("MARKER", (Integer.toString(establishment.getCompany().getCategoryId()) ));
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

    public class getCategoryResponseListener implements Response.Listener<Category[]>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

        @Override
        public void onResponse(Category[] categories) {
            for(Category category : categories){
                Log.i(TAG, category.toString());
                createCategory(category);
            }
        }
    }


}

