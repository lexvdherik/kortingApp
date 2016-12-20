package hva.flashdiscount.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

public class DiscountListAdapter extends BaseExpandableListAdapter implements Filterable {

    private static final String TAG = DiscountListAdapter.class.getSimpleName();

    private final LayoutInflater inf;
    private ArrayList<Establishment> groups;
    private ArrayList<ArrayList<Discount>> children;

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
            convertView = inf.inflate(R.layout.list_discount_info, parent, false);
        }


        Discount discount = getChild(groupPosition, childPosition);
        ((TextView) convertView.findViewById(R.id.discount_description)).setText(discount.getDescription());
        ((TextView) convertView.findViewById(R.id.time_remaining)).setText(discount.getTimeRemaining(convertView.getContext()));
        ((TextView) convertView.findViewById(R.id.claims_remaining)).setText(discount.getAmountRemaining() + " " + convertView.getResources().getString(R.string.left));

        convertView.setEnabled(!discount.isValid(convertView.getContext()));

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_parent, parent, false);
        }
        int cCount = getChildrenCount(groupPosition);
        ImageView img = ((ImageView) convertView.findViewById(R.id.list_arrow_image));
        if (cCount > 0) {
            if (isExpanded) {
                img.setImageResource(R.drawable.ic_arrow_drop_down_black_24px);
            } else {
                img.setImageResource(R.drawable.ic_arrow_drop_up_black_24px);
            }
        }
        img.setVisibility(View.VISIBLE);

        ImageView lineColorCode = (ImageView) convertView.findViewById(R.id.list_icon);
        int color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = convertView.getContext().getColor(R.color.textColorSecondary);
        } else {
            color = convertView.getContext().getResources().getColor(R.color.textColorSecondary);
        }

        lineColorCode.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        Establishment establishment = getGroup(groupPosition);
        if (cCount == 0) {
            ((TextView) convertView.findViewById(R.id.flextitel)).setText(establishment.getDiscounts().get(0).getDescription());
            ((TextView) convertView.findViewById(R.id.company_name)).setText(establishment.getCompany().getName());
            ((TextView) convertView.findViewById(R.id.company_street)).setText(establishment.getStreet() + " " + establishment.getStreetnumber());
            ((TextView) convertView.findViewById(R.id.time_remaining)).setText(establishment.getDiscounts().get(0).getTimeRemaining(convertView.getContext()));
            ((TextView) convertView.findViewById(R.id.claims_remaining)).setText(establishment.getDiscounts().get(0).getAmountRemaining() + " " + convertView.getResources().getString(R.string.left));
            convertView.setEnabled(establishment.getDiscounts().get(0).isValid(convertView.getContext()));
        } else {
            ((TextView) convertView.findViewById(R.id.flextitel)).setText(String.valueOf(cCount + " " + convertView.getResources().getString(R.string.discount_plural)));
            ((TextView) convertView.findViewById(R.id.company_name)).setText(establishment.getCompany().getName());
            ((TextView) convertView.findViewById(R.id.company_street)).setText(establishment.getStreet() + " " + establishment.getStreetnumber());

        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (!(charSequence == null || charSequence.length() == 0)) {
                    ArrayList<Establishment> filteredResults = new ArrayList<>();
                    for (Establishment e : filteredResults) {
                        //   if(e.getCompany().getCategoryId() ) {}
                    }
                }


                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                //List<Discount> filteredResults = ;

                FilterResults results = new FilterResults();
                //  results.values =

            }
        };
    }
}
