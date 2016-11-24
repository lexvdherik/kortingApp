package hva.flashdiscount.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import java.util.ArrayList;
import hva.flashdiscount.R;
import hva.flashdiscount.fragment.SettingsFragment;
import hva.flashdiscount.model.Favorite;


/**
 * Created by Laptop_Ezra on 17-11-2016.
 */

public class SettingsAdapter extends BaseAdapter {

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

        return rowView;
    }
}