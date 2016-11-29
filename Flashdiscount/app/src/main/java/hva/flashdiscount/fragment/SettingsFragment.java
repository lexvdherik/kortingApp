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

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.SettingsAdapter;
import hva.flashdiscount.model.Favorite;

public class SettingsFragment extends Fragment {


    public static Favorite[] companySettings;
    private SettingsAdapter settingsAdapter;

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

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final ListView lv = (ListView) rootView.findViewById(R.id.notification_list);
        companySettings = new Favorite[0];
        settingsAdapter = new SettingsAdapter(getActivity(), companySettings);

        lv.setAdapter(settingsAdapter);

        final Switch swMain = (Switch) rootView.findViewById(R.id.switch_newsletter);
        swMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAllOptions(swMain.isChecked());
                saveSettings();
                settingsAdapter.updateResults(companySettings);
            }
        });

        getFavorites();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getFavorites();
    }


    public void setAllOptions(Boolean checkState) {

        int checked = 0;

        if (checkState) {
            checked = 1;
        }

        for (Favorite item : companySettings) {
            item.setNotification(checked);
        }
    }

    private void getFavorites() {
        String idToken = "TEST";
        System.gc();
        SettingsFragment.GetFavoritesResponseListener listener = new GetFavoritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavorites(listener, listener, idToken);
    }

    public void saveSettings() {
        String idToken = "TEST";
        System.gc();
        SetSettingsResponseListener listener = new SetSettingsResponseListener();
        APIRequest.getInstance(getActivity()).setSettings(listener, listener, idToken, companySettings);
    }


    public class GetFavoritesResponseListener implements Response.Listener<Favorite[]>, Response.ErrorListener {

        @Override
        public void onResponse(Favorite[] favorites) {
            if (favorites.length > 0) {
                System.out.println(favorites[0].getCompany());

                final ListView lv = (ListView) getActivity().findViewById(R.id.notification_list);
                companySettings = favorites;
                settingsAdapter = new SettingsAdapter(getActivity(), favorites);

                lv.setAdapter(settingsAdapter);
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + error.getMessage());
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

    public class SetSettingsResponseListener implements Response.Listener, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

        @Override
        public void onResponse(Object response) {
            Log.e(TAG, "Response of Settings!");
        }
    }
}