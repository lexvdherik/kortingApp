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
import hva.flashdiscount.model.Favorite;

public class SettingsFragment extends Fragment {


    private ArrayList<SettingsObject> companySettings;
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
        companySettings = fillList();

        settingsAdapter = new SettingsAdapter(getActivity(), companySettings);

        lv.setAdapter(settingsAdapter);

        final Switch swMain = (Switch) rootView.findViewById(R.id.switch_newsletter);
        swMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAllOptions(swMain.isChecked());
                settingsAdapter.updateResults(companySettings);
            }
        });

        getFavourites();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getFavourites();
    }

    private void getFavourites() {
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE0YmM2ZjRhZjFkZWFhMGExZGUzNDYwNzBiM2UwNjYyMTI0MWUzMmYifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJpYXQiOjE0Nzk4MjM1NDUsImV4cCI6MTQ3OTgyNzE0NSwiYXVkIjoiNDQ0OTUzNDA3ODA1LW41bTlxaXR2ZmNucm04azNtdWM3M3NxdjVnOTFkbW1pLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA1MzM4MDI4NDA5MTU5MDkxNzk1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjQ0NDk1MzQwNzgwNS1oMmxpdGdsdnNxdTY0djFoZjhsazllaTlrOGw3azRkMi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiYW50aG9ueS5zaXRhcmFtQGdtYWlsLmNvbSIsIm5hbWUiOiJBbnRob255IFNpdGFyYW0iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDUuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1QdU9mZmgxbHhuSS9BQUFBQUFBQUFBSS9BQUFBQUFBQUFDby9YUlFTQ1RUUDFDRS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiQW50aG9ueSIsImZhbWlseV9uYW1lIjoiU2l0YXJhbSIsImxvY2FsZSI6ImVuLUJFIn0.fcD8rYFybU7xXgJm3vo8l15H3Fo_5iSQmW4DFGT5Y0PqzTqUp5xpb6ypJHOkjsG-ff7E6YYFLS2z44SdJids6bgy0-FHdNNeGwypTeAf7Oa-fBYxGIQI7ppPwOzD2eXFy5WDW-YJaUuhfDoJlwhdY1cZMucQypIbo7xvv3nRFEppSyzXgTv_SQG9jQjXI6kgtPQ-bsoCOYgtiO0EDzT7IA5xf6abB-3Np9p_LW0yg7uCrgnkZBiyj3GPDp-Ci9-XA-LCvpIbq-0OdvXe34jkviOipvkMJ1ECvq7QepcuaEQg7vnYbrg9qFtJgkJ1BOLGsHT2hkqx7edmRoZv7YoJsg";
        System.gc();
        SettingsFragment.GetFavoritesResponseListener listener = new SettingsFragment.GetFavouritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavourites(listener, listener, idToken);
    }

    public class GetFavoritesResponseListener implements Response.Listener<Favorite[]>, Response.ErrorListener {

        @Override
        public void onResponse(final Favorite[] favorite) {
            Log.e(TAG, " getFavorites");
                Log.e(TAG, " " + favorite[0].toString());
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + error.getMessage());
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
