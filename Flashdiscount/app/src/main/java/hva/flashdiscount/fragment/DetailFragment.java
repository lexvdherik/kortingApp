package hva.flashdiscount.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;


public class DetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
    }

    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.e(TAG, "getArguments() != null");


            String gson = getArguments().getString("establishment");
            Establishment establishment = new Gson().fromJson(gson, Establishment.class);
            Discount discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));

            Log.e(TAG, establishment.getCompany().getName());
            Log.e(TAG, discount.getDescription());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discount_detail, container, false);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
