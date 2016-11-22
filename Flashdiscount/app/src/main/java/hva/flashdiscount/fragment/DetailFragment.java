package hva.flashdiscount.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.Utils.VolleySingleton;
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
    private ImageButton favoriteButton;
    private TextView companyName;
    private TextView companyDescription;
    private TextView claimsLeft;
    private TextView timeLeft;
    private TextView discountDescription;


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

            String gson = getArguments().getString("establishment");
            establishment = new Gson().fromJson(gson, Establishment.class);
            discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));



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
        favoriteButton = (ImageButton) mRootView.findViewById(R.id.btnFavorite);

        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
                String idToken = sharedPref.getString("idToken", "");
                setFavorite(idToken,String.valueOf(establishment.getEstablishmentId()));
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
        //String urlString = "http://www.arenapoort.nl/wp-content/uploads/2014/05/12874-proost.jpg";
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
}
