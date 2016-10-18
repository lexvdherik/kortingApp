package hva.flashdiscount.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.DiscountListAdapter;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.service.EstablishmentService;

public class DiscountListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    View rootView;
    ExpandableListView expandableListView;

    private ArrayList<Establishment> establishments;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private EstablishmentService establishmentService;


    public DiscountListFragment() {


    }

    public void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            default:
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("permission", "GRANTED");
                    mGoogleApiClient.connect();

                } else {

                    Log.e("permission", "DENIED");
                }
                return;

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e("latitude", String.valueOf(mLastLocation.getLatitude()));
        Log.e("longitude", String.valueOf(mLastLocation.getLongitude()));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        context = getActivity();

        establishmentService = new EstablishmentService(context, this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discount_list, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }

        establishments = new ArrayList<>();

        establishments = establishmentService.getEstablishments();

    }

    public void fillList() {
        Log.e("asdfsafafsd", "testerere");
//        swipeRefreshLayout.setRefreshing(false);
        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expListView);
        expandableListView.setAdapter(new DiscountListAdapter(establishments, getActivity()));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                DetailFragment detailFragment = new DetailFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();

                return false;
            }
        });

    }


}

