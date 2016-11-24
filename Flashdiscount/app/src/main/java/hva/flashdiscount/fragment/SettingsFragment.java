package hva.flashdiscount.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
                settingsAdapter.updateResults(companySettings);
                saveSettings();
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

        if(checkState == true){
            checked = 1;
        }

        for (Favorite item : companySettings) {
            item.setNotification(checked);
        }
    }



    private void getFavorites() {
        Log.e(TAG, " getFavorites");
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjM4Y2FjZjM3ZjUyOWQ4YzM2ZDBlNmJkYzU5OTNlNWQ3Njk1ZDg5NzgifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJpYXQiOjE0Nzk4MjcyMzMsImV4cCI6MTQ3OTgzMDgzMywiYXVkIjoiNDQ0OTUzNDA3ODA1LW41bTlxaXR2ZmNucm04azNtdWM3M3NxdjVnOTFkbW1pLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA1MzM4MDI4NDA5MTU5MDkxNzk1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjQ0NDk1MzQwNzgwNS1oMmxpdGdsdnNxdTY0djFoZjhsazllaTlrOGw3azRkMi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiYW50aG9ueS5zaXRhcmFtQGdtYWlsLmNvbSIsIm5hbWUiOiJBbnRob255IFNpdGFyYW0iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDUuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1QdU9mZmgxbHhuSS9BQUFBQUFBQUFBSS9BQUFBQUFBQUFDby9YUlFTQ1RUUDFDRS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiQW50aG9ueSIsImZhbWlseV9uYW1lIjoiU2l0YXJhbSIsImxvY2FsZSI6ImVuLUJFIn0.L734Sa4090IBuKM0CwOoeMOI-H82Z7wcscA0R8jdhgI9NtC2G6ARlOtNQ_WhdpYQF7dNH6uXrJVDkpsKRrebjU-bwkTD-HDC6VsuS3BoVlO_daVnY15Ci395nFOV18zNaC_vgYXHgn7dh_OeND5e4ZmBhvOrttTc7pszZ5TRiHndEnmt8RvNRRcGP2QEU-VNhca1obzIC5IILY7yQ_XrShhX_tIjL83ohGSoLALKnevqAX6l2H-hYg3841JvK1HTIVfGtyM-CBN1jWD5xBssz58vIgyRuNE3WWqUDn7PcuBFR6evNxph9Utz8pQZs4pavsxEOLsfNJwQ9leXeT3oUA";
        System.gc();
        SettingsFragment.GetFavoritesResponseListener listener = new SettingsFragment.GetFavoritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavorites(listener, listener, idToken);
    }

    private void saveSettings() {
        Log.e(TAG, " saveFavorites");
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjM4Y2FjZjM3ZjUyOWQ4YzM2ZDBlNmJkYzU5OTNlNWQ3Njk1ZDg5NzgifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJpYXQiOjE0Nzk4MjcyMzMsImV4cCI6MTQ3OTgzMDgzMywiYXVkIjoiNDQ0OTUzNDA3ODA1LW41bTlxaXR2ZmNucm04azNtdWM3M3NxdjVnOTFkbW1pLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA1MzM4MDI4NDA5MTU5MDkxNzk1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjQ0NDk1MzQwNzgwNS1oMmxpdGdsdnNxdTY0djFoZjhsazllaTlrOGw3azRkMi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiYW50aG9ueS5zaXRhcmFtQGdtYWlsLmNvbSIsIm5hbWUiOiJBbnRob255IFNpdGFyYW0iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDUuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1QdU9mZmgxbHhuSS9BQUFBQUFBQUFBSS9BQUFBQUFBQUFDby9YUlFTQ1RUUDFDRS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiQW50aG9ueSIsImZhbWlseV9uYW1lIjoiU2l0YXJhbSIsImxvY2FsZSI6ImVuLUJFIn0.L734Sa4090IBuKM0CwOoeMOI-H82Z7wcscA0R8jdhgI9NtC2G6ARlOtNQ_WhdpYQF7dNH6uXrJVDkpsKRrebjU-bwkTD-HDC6VsuS3BoVlO_daVnY15Ci395nFOV18zNaC_vgYXHgn7dh_OeND5e4ZmBhvOrttTc7pszZ5TRiHndEnmt8RvNRRcGP2QEU-VNhca1obzIC5IILY7yQ_XrShhX_tIjL83ohGSoLALKnevqAX6l2H-hYg3841JvK1HTIVfGtyM-CBN1jWD5xBssz58vIgyRuNE3WWqUDn7PcuBFR6evNxph9Utz8pQZs4pavsxEOLsfNJwQ9leXeT3oUA";
        System.gc();
        SetSettingsResponseListener listener = new SetSettingsResponseListener();
        Log.e(TAG, companySettings[0].getCompany().getName() + " : " + companySettings[0].getNotification());
        Log.e(TAG, companySettings[1].getCompany().getName() + " : " + companySettings[0].getNotification());
        APIRequest.getInstance(getActivity()).setSettings(listener, listener, idToken, companySettings);
    }


    public class GetFavoritesResponseListener implements Response.Listener<Favorite[]>, Response.ErrorListener {

        @Override
        public void onResponse(Favorite[] favorites) {
            if(favorites.length > 0){
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
