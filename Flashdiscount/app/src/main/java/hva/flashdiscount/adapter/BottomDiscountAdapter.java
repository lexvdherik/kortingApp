package hva.flashdiscount.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;

/**
 * TODO: Write description.
 *
 * @author chrisvanderheijden
 * @since 24/11/2016
 */

public class BottomDiscountAdapter extends ArrayAdapter<Discount> {

    private static final String TAG = BottomDiscountAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private ArrayList<Discount> discountArrayList;

    public BottomDiscountAdapter(ArrayList<Discount> discountArrayList, Context context) {
        super(context, R.layout.list_discount_info, discountArrayList);
        this.discountArrayList = discountArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return discountArrayList.size();
    }

    @Override
    public Discount getItem(int i) {
        return discountArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return discountArrayList.get(i).getDiscountId();
    }

    @Override
    @NonNull
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        Discount discount = getItem(i);
        view = inflater.inflate(R.layout.list_discount_info, viewGroup, false);
        if (discount == null) {
            return view;
        }
        TextView description = (TextView) view.findViewById(R.id.discount_description);
        TextView time = (TextView) view.findViewById(R.id.time_remaining);
        TextView items = (TextView) view.findViewById(R.id.claims_remaining);

        description.setText(discount.getDescription());
        if (getCount() == 1) {
            time.setVisibility(View.GONE);
            items.setVisibility(View.GONE);

            return view;
        }

        time.setText(discount.getTimeRemaining(getContext()));
        items.setText(discount.getAmountRemaining() + " " + getContext().getString(R.string.left));

        return view;
    }

}
