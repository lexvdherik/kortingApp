package hva.flashdiscount.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;


import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.SettingsAdapter;
import hva.flashdiscount.model.Establishment;

public class SettingsFragment extends Fragment {


    private ArrayList<SettingsObject> companySettings;
    private SettingsAdapter test;
    ListView listView;

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    public class Holder {
        Switch tv;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final ListView lv = (ListView) rootView.findViewById(R.id.notification_list);
        companySettings = fillList();

        test = new SettingsAdapter(getActivity(), companySettings);

        lv.setAdapter(test);

        final Switch swMain = (Switch) rootView.findViewById(R.id.switch_newsletter);
        swMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAllOptions(swMain.isChecked());
                test.updateResults(companySettings);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getFavouritesFromAPI();
    }

    private void getFavouritesFromAPI() {
        System.gc();
        GetFavouritesResponseListener listener = new GetFavouritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavourites(listener, listener);

    }


    public class GetFavouritesResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(final Establishment[] establishments) {

            listView = (ListView) getActivity().findViewById(R.id.notification_list);
            listView.setAdapter(new SettingsAdapter(getActivity(), companySettings));
            Switch sw = (Switch) getActivity().findViewById(R.id.setting_company_name);
            sw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: enabling/disabling specific notifications
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

    public ArrayList<SettingsObject> fillList() {
        ArrayList<SettingsObject> temp = new ArrayList<>();

        SettingsObject test = new SettingsObject("CoffeeCompany", true);
        SettingsObject testTwee = new SettingsObject("Cafe Noire", false);
        temp.add(test);
        temp.add(testTwee);
        return temp;
    }

    public void setAllOptions(Boolean checkState) {

        for (SettingsObject item : companySettings) {
            item.setChecked(checkState);
        }
    }

    public class SettingsObject {

        private String title;
        private Boolean checked;

        public SettingsObject(String title, Boolean checked) {
            this.title = title;
            this.checked = checked;
        }

        public String getTitle() {
            return title;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }

    }
}
