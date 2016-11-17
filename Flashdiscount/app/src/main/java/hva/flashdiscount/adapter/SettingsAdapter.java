package hva.flashdiscount.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;


import java.util.ArrayList;

import hva.flashdiscount.R;


/**
 * Created by Laptop_Ezra on 17-11-2016.
 */

public class SettingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<String> companySettings;

    public SettingsAdapter(Activity activity) {

        inflater = LayoutInflater.from(activity);

        companySettings = new ArrayList<>();
        companySettings.add("CoffeeCompany");
        companySettings.add("Cafe Noire");
        companySettings.add("Starbucks");
        companySettings.add("De Roeter");


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
        holder.tv.setText(companySettings.get(position));

        return rowView;
    }

}