package hva.flashdiscount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;

/**
 * Created by Maiko on 6-10-2016.
 */

public class DiscountAdapter extends ArrayAdapter<Discount> {

    public DiscountAdapter(Context context, ArrayList<Discount> discounts) {
        super(context, 0, discounts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Discount discount = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_discount_row, parent, false);

        }

        TextView discountTitle = (TextView) convertView.findViewById(R.id.description);
        TextView companyName = (TextView) convertView.findViewById(R.id.company_name);
        TextView timeRemaining = (TextView) convertView.findViewById(R.id.time_remaining);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_icon);

//        discountTitle.setText(discount.getDescription());
//        companyName.setText(discount.getCompanyName());
//        imageView.setImageResource(discount.getCategoryImage());

        timeRemaining.setText(discount.getTimeRemaining());

        return convertView;

    }
}
