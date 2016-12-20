package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.DiscountListAdapter;
import hva.flashdiscount.model.Establishment;

public class DiscountListFragment extends Fragment {

    private static final String TAG = DiscountListFragment.class.getSimpleName();

    View rootView;
    ExpandableListView expandableListView;
    DiscountListFragment dlf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dlf = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discount_list, container, false);

        SwipeRefreshLayout s = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        s.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(dlf);
                ft.attach(dlf);
                ft.commit();
            }
        });

        s.setRefreshing(false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getEstablishmentsFromAPI();
    }

    private void getEstablishmentsFromAPI() {
        GetEstablishmentResponseListener listener = new GetEstablishmentResponseListener();
        APIRequest.getInstance(getActivity()).getEstablishment(listener, listener);
    }

    private void goToDetailView(Establishment establishment, int discountPostion) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPosition", discountPostion);
        arguments.putBoolean("dialog", false);
        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_frombottom, R.anim.slide_in_frombottom)
                .replace(R.id.fragment_container, detailFragment, "detailfrag")
                .addToBackStack(null)
                .commit();
    }

    public class GetEstablishmentResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(final Establishment[] establishments) {

            expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expListView);
            if (expandableListView == null) {
                return;
            }
            expandableListView.setAdapter(new DiscountListAdapter(establishments, getActivity()));

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                    Establishment establishment = (Establishment) parent.getExpandableListAdapter().getGroup(groupPosition);
                    goToDetailView(establishment, childPosition);

                    return false;
                }
            });

            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    int count = parent.getExpandableListAdapter().getChildrenCount(groupPosition);

                    if (count == 0) {
                        Establishment establishment = (Establishment) parent.getExpandableListAdapter().getGroup(groupPosition);
                        goToDetailView(establishment, 0);
                    }
                    return false;
                }
            });

        }


        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }
    }
}