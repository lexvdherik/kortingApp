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

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.SettingsAdapter;
import hva.flashdiscount.model.Favorite;
import hva.flashdiscount.model.User;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.utils.LoginSingleton;

public class SettingsFragment extends Fragment {


    private static final String TAG = SettingsFragment.class.getSimpleName();
    public static Favorite[] companySettings;
    private SettingsAdapter settingsAdapter;
    private Switch swMain;
    private LoginSingleton loginSingleton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginSingleton = LoginSingleton.getInstance(getContext());
        if (getArguments() != null) {
            Log.i(TAG, getArguments().toString());
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

        swMain = (Switch) rootView.findViewById(R.id.switch_newsletter);
        swMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAllOptionsTo(swMain.isChecked());
                saveSettings();
                settingsAdapter.updateResults(companySettings);
            }
        });
        if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
            getFavorites();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
            getFavorites();
            return;
        }

        User user = null;
        if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
            user = loginSingleton.silentLogin();
        }
        if (user != null) {
            getFavorites();
            return;
        }
        loginSingleton.showLoginDialog();
    }

    /**
     * Set all toggles to the provided state.
     *
     * @param checkState Boolean
     */
    public void setAllOptionsTo(Boolean checkState) {
        int checked = (checkState) ? 1 : 0;

        for (Favorite item : companySettings) {
            item.setNotification(checked);
        }
    }

    private void getFavorites() {
        SettingsFragment.GetFavoritesResponseListener listener = new GetFavoritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavorites(listener, listener);
    }

    public void saveSettings() {
        SetSettingsResponseListener listener = new SetSettingsResponseListener();
        APIRequest.getInstance(getActivity()).setSettings(listener, listener, companySettings);
    }


    public class GetFavoritesResponseListener implements Response.Listener<Favorite[]>, Response.ErrorListener {

        @Override
        public void onResponse(Favorite[] favorites) {
            if (favorites.length > 0) {
                System.out.println(favorites[0].getCompany());

                final ListView lv = (ListView) getActivity().findViewById(R.id.notification_list);
                companySettings = favorites;
                swMain.setChecked(true);
                for (Favorite item : companySettings) {
                    if (item.getNotification() == 0) {
                        swMain.setChecked(false);
                        break;
                    }
                }
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
            Log.i(TAG, "Response of Settings!");
        }
    }
}
