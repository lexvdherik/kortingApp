package hva.flashdiscount.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

public class DiscountListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inf;
    private ArrayList<Establishment> groups;
    private ArrayList<ArrayList<Discount>> children;

    private static final String TAG = DiscountListAdapter.class.getSimpleName();

    public DiscountListAdapter(Establishment[] establishmentsArray, Activity activity) {

        ArrayList<Establishment> establishments = new ArrayList<>(Arrays.asList(establishmentsArray));

        groups = new ArrayList<>();
        children = new ArrayList<>();
        for (int i = 0; i < establishments.size(); i++) {
            groups.add(establishments.get(i));
            children.add(new ArrayList<Discount>());

            ArrayList<Discount> discounts = establishments.get(i).getDiscounts();

            int discountCount = discounts.size();
            if (discountCount != 1) {
                for (int j = 0; j < discounts.size(); j++) {
                    children.get(i).add(discounts.get(j));
                }
            }

        }

        inf = LayoutInflater.from(activity);

    }



    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Establishment getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Discount getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
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
        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_child, parent, false);
        }

        Discount discount = getChild(groupPosition, childPosition);
        ((TextView) convertView.findViewById(R.id.description)).setText(discount.getDescription());
//        ((TextView) convertView.findViewById(R.id.company_name)).setText(getGroup(groupPosition).getCompany().getName());
        ((TextView) convertView.findViewById(R.id.time_remaining)).setText(discount.getTimeRemaining());
        ((ImageView) convertView.findViewById(R.id.list_icon)).setImageResource(discount.getCategoryImage(getGroup(groupPosition).getCompany().getCategoryId()));

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_parent, parent, false);
        }

        Establishment establishment = getGroup(groupPosition);
        int cCount = getChildrenCount(groupPosition);
        if (cCount == 0) {
            ((TextView) convertView.findViewById(R.id.flextitel)).setText(establishment.getDiscounts().get(0).getDescription());
            ((TextView) convertView.findViewById(R.id.company_name)).setText(establishment.getCompany().getName());
            ((TextView) convertView.findViewById(R.id.time_remaining)).setText(establishment.getDiscounts().get(0).getTimeRemaining());
        } else {
            ((TextView) convertView.findViewById(R.id.flextitel)).setText(String.valueOf(cCount + " Aanbiedingen"));
            ((TextView) convertView.findViewById(R.id.company_name)).setText(establishment.getCompany().getName());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
