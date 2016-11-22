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


/**
 * Created by Laptop_Ezra on 17-11-2016.
 */

public class SettingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<SettingsFragment.SettingsObject> companySettings;

    public SettingsAdapter(Activity activity, ArrayList<SettingsFragment.SettingsObject> companySettings) {

        inflater = LayoutInflater.from(activity);
        this.companySettings = companySettings;
    }

    public void updateResults(ArrayList<SettingsFragment.SettingsObject> results) {
        companySettings = results;
        //Triggers the list update
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return companySettings.size();
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
        holder.tv.setText(companySettings.get(position).getTitle());
        holder.tv.setChecked(companySettings.get(position).getChecked());

        return rowView;
    }
}