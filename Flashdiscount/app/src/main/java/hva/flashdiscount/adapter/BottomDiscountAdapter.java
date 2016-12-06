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
        super(context, R.layout.bottom_sheet_list_detail, discountArrayList);
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
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {


        Discount discount = getItem(i);

        view = inflater.inflate(R.layout.bottom_sheet_list_detail, viewGroup, false);
        TextView description = (TextView) view.findViewById(R.id.description_bottom_sheet);
        if (discount == null) {
            return view;
        }
        description.setText(discount.getDescription());


        return view;
    }
//
//    @Override
//    public void onClick(View view) {
//
//        int position = (int) view.getTag();
//        Discount discount = getItem(position);
//
//
//
//    }

//    private void goToDetailView(Discount discount) {
//
//        Bundle arguments = new Bundle();
//        arguments.putString("discount", new Gson().toJson(discount));
//
//        DetailFragment detailFragment = new DetailFragment();
//
//        detailFragment.setArguments(arguments);
//
//        FragmentManager manager = ((Activity) context).getFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, detailFragment)
//                .addToBackStack(null)
//                .commit();
//    }

}
