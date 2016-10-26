package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private static final String TAG = DiscountListFragment.class.getSimpleName();

    View rootView;
    ExpandableListView expandableListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void getEstablishmentsFromAPI() {
        System.gc();
        GetEstablishmentResponseListener listener = new GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);


    }

    public class GetEstablishmentResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(final Establishment[] establishments) {

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
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

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
                Log.e(TAG, "No connection!");
            }
        }


    }

}