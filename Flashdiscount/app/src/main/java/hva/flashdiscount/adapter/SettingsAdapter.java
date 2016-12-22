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
import com.google.firebase.messaging.FirebaseMessaging;

import hva.flashdiscount.R;
import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Favorite;
import hva.flashdiscount.network.APIRequest;


/**
 * TODO: Write description.
 *
 * @author Laptop_Ezra
 * @since 17-11-2016
 */

public class SettingsAdapter extends BaseAdapter {

    private static final String TAG = SettingsAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private Favorite[] companySettings;

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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.settings_row, null);
        holder.tv = (Switch) rowView.findViewById(R.id.setting_company_name);
        Boolean checked = false;
        if (companySettings[position].getNotification() == 1) {
            checked = true;
        }

        holder.tv.setChecked(checked);

        holder.tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                int check = 0;
                if (isChecked) {
                    check = 1;
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(companySettings[position].getEstablishmentId()));
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(companySettings[position].getEstablishmentId()));
                }
                SettingsFragment.companySettings[position].setNotification(check);
                companySettings[position].setNotification(check);
                saveSettings();
            }
        });

        Company company = companySettings[position].getCompany();
        if (company == null) {
            holder.tv.setText(R.string.no_company_data);
            return rowView;
        }
        holder.tv.setText(company.getName());

        return rowView;
    }

    private void saveSettings() {
        SetSettingsResponseListener listener = new SetSettingsResponseListener();
        APIRequest.getInstance(inflater.getContext()).setSettings(listener, listener, companySettings);
    }

    private class Holder {
        Switch tv;
    }

    private class SetSettingsResponseListener implements Response.Listener, Response.ErrorListener {
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