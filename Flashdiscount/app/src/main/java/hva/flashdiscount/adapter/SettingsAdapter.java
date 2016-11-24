package hva.flashdiscount.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Set;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.model.Favorite;


/**
 * Created by Laptop_Ezra on 17-11-2016.
 */

public class SettingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Favorite[] companySettings;
    private static final String TAG = SettingsAdapter.class.getSimpleName();

    public SettingsAdapter(Activity activity, Favorite[] companySettings) {

        inflater = LayoutInflater.from(activity);
        this.companySettings = companySettings;
    }

    public void updateResults(Favorite[] results) {
        companySettings = results;
        //Triggers the list update
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return companySettings.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class Holder {
        Switch tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.settings_row, null);
        holder.tv = (Switch) rowView.findViewById(R.id.setting_company_name);
        holder.tv.setText(companySettings[position].getCompany().getName());
        Boolean checked = false;
        if(companySettings[position].getNotification() == 1){
            checked = true;
        }

        holder.tv.setChecked(checked);

        holder.tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                int check = 0;
                if(isChecked){
                    check = 1;
                }
                SettingsFragment.companySettings[position].setNotification(check);
                companySettings[position].setNotification(check);
                saveSettings();
                Log.e(TAG, companySettings[position].getCompany() + " : " + companySettings[position].getNotification());

            }
        });

        return rowView;
    }

    public void saveSettings() {
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjM4Y2FjZjM3ZjUyOWQ4YzM2ZDBlNmJkYzU5OTNlNWQ3Njk1ZDg5NzgifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJpYXQiOjE0Nzk4MjcyMzMsImV4cCI6MTQ3OTgzMDgzMywiYXVkIjoiNDQ0OTUzNDA3ODA1LW41bTlxaXR2ZmNucm04azNtdWM3M3NxdjVnOTFkbW1pLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA1MzM4MDI4NDA5MTU5MDkxNzk1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjQ0NDk1MzQwNzgwNS1oMmxpdGdsdnNxdTY0djFoZjhsazllaTlrOGw3azRkMi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiYW50aG9ueS5zaXRhcmFtQGdtYWlsLmNvbSIsIm5hbWUiOiJBbnRob255IFNpdGFyYW0iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDUuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1QdU9mZmgxbHhuSS9BQUFBQUFBQUFBSS9BQUFBQUFBQUFDby9YUlFTQ1RUUDFDRS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoiQW50aG9ueSIsImZhbWlseV9uYW1lIjoiU2l0YXJhbSIsImxvY2FsZSI6ImVuLUJFIn0.L734Sa4090IBuKM0CwOoeMOI-H82Z7wcscA0R8jdhgI9NtC2G6ARlOtNQ_WhdpYQF7dNH6uXrJVDkpsKRrebjU-bwkTD-HDC6VsuS3BoVlO_daVnY15Ci395nFOV18zNaC_vgYXHgn7dh_OeND5e4ZmBhvOrttTc7pszZ5TRiHndEnmt8RvNRRcGP2QEU-VNhca1obzIC5IILY7yQ_XrShhX_tIjL83ohGSoLALKnevqAX6l2H-hYg3841JvK1HTIVfGtyM-CBN1jWD5xBssz58vIgyRuNE3WWqUDn7PcuBFR6evNxph9Utz8pQZs4pavsxEOLsfNJwQ9leXeT3oUA";
        System.gc();
        SetSettingsResponseListener listener = new SetSettingsResponseListener();
        APIRequest.getInstance(inflater.getContext()).setSettings(listener, listener, idToken, companySettings);
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