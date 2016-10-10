package hva.flashdiscount.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

/**
 * Created by Anthony on 09-Oct-16.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inf;
    private String[] groups;
    private String[][] children;
    private ArrayList<Establishment> establishments;

    public ExpandableListAdapter(ArrayList<Establishment> establishments, Activity activity) {
        this.establishments = establishments;

        groups = new String[establishments.size()];
        children = new String[establishments.size()][];
        for(int i = 0; i < establishments.size(); i++){
            groups[i] = establishments.get(i).getCompany().getName();

            ArrayList<Discount> discounts = establishments.get(i).getDiscounts();
            for(int j = 0; j < discounts.size(); j++){
                children[i] = new String[]{discounts.get(j).getTimeRemaining()};
            }

        }

        inf = LayoutInflater.from(activity);
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            holder.text = (TextView) convertView.findViewById(R.id.company_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_group, parent, false);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.company_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        TextView text;
    }
}
