package hva.flashdiscount.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;


import hva.flashdiscount.MainActivity;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.utils.VolleySingleton;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;


public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private ImageLoader mImageLoader;
    private View mRootView;
    private Establishment establishment;
    private Discount discount;
    private NetworkImageView companyImage;
    private TextView companyName;
    private TextView companyDescription;
    private TextView claimsLeft;
    private TextView timeLeft;
    private TextView discountDescription;
    private int dicountPosition;
    private boolean dialog;
    private boolean success;


    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MessageDialogFragment newInstance(String message) {
        MessageDialogFragment f = new MessageDialogFragment();

        Bundle args = new Bundle();
        args.putString("message", message);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        if (getArguments() != null) {

            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                Log.e(TAG, "SupportActionbar found");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            Log.e(TAG, "onCreate: " + String.valueOf(getFragmentManager().getBackStackEntryCount()));

            if (getArguments() != null) {
                String gson = getArguments().getString("establishment");
                dicountPosition = getArguments().getInt("discountPosition");
                establishment = new Gson().fromJson(gson, Establishment.class);
                discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));
                success = getArguments().getBoolean("success");
                dialog = getArguments().getBoolean("dialog");
            }
            if(dialog){
//                MessageDialogFragment dialogFragment = new MessageDialogFragment();

                MessageDialogFragment dialogFragment = newInstance(getArguments().getString("message"));

//                Bundle args = new Bundle();
//                args.putString("message", getArguments().getString("message"));
//
//                dialogFragment.setArguments(args);
                dialogFragment.show(fm, "");
            }



        }
    }
    private void setFavorite(String idToken,String EstablishmentId) {
        System.gc();
        DetailFragment.SetFavoriteResponseListener listener = new DetailFragment.SetFavoriteResponseListener();
        APIRequest.getInstance(getActivity()).setFavorite(listener, listener, idToken,EstablishmentId);
    }



    public class SetFavoriteResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + error.getMessage());
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_discount_detail, container, false);
            mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
            initViews();
            setCompanyText();
            setDiscountText();
        }
        Button favoriteButton = (Button) mRootView.findViewById(R.id.favorite_button);

        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                        ((MainActivity) getActivity()).getContextOfApplication()
                );
                String idToken = sharedPref.getString("idToken", "");
                setFavorite(idToken,String.valueOf(establishment.getEstablishmentId()));
            }
        });
        Button claimButton = (Button) mRootView.findViewById(R.id.claim_button);

        claimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToScanner(establishment,dicountPosition);

            }
        });

        // Inflate the layout for this fragment
        return mRootView;
    }

    public void initViews() {
        companyImage = (NetworkImageView) mRootView.findViewById(R.id.company_detail_image);
        companyName = (TextView) mRootView.findViewById(R.id.company_name);
        companyDescription = (TextView) mRootView.findViewById(R.id.company_description);
        claimsLeft = (TextView) mRootView.findViewById(R.id.claims_left);
        timeLeft = (TextView) mRootView.findViewById(R.id.time_left);
        discountDescription = (TextView) mRootView.findViewById(R.id.discount_description);
    }

    public void setCompanyText() {
        companyImage.setImageUrl(establishment.getCompany().getLogo(), mImageLoader);

        companyName.setText(establishment.getCompany().getName());
        companyDescription.setText(establishment.getCompany().getDescription());
    }

    public void setDiscountText() {
        Log.i(TAG, discount.toString());
        claimsLeft.setText(Integer.toString(discount.getUserLimit()));
        timeLeft.setText(discount.getTimeRemaining());
        discountDescription.setText(discount.getDescription());
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void goToScanner(Establishment establishment, int discountPostion) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPostion", discountPostion);

        ScannerFragment scannerFragment = new ScannerFragment();

        scannerFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scannerFragment)
                .addToBackStack(null)
                .commit();
    }

}
