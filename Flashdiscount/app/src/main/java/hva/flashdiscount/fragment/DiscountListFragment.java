package hva.flashdiscount.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.DiscountListAdapter;
import hva.flashdiscount.model.Establishment;

public class DiscountListFragment extends Fragment {

    View rootView;
    ExpandableListView expandableListView;
    private Context context;


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
//                    mGoogleApiClient.connect();

                } else {

                    Log.e("permission", "DENIED");
                }
                return;

        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discount_list, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getEstablishmentsFromAPI();

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }


    }

    private void getEstablishmentsFromAPI() {
        System.gc();
        GetEstablishmentResponseListener listener = new GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);


    }

    public class GetEstablishmentResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {


        @Override
        public void onResponse(Establishment[] establishments) {

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

        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
            }
        }


    }

}