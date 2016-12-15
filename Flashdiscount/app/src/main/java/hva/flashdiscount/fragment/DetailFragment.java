package hva.flashdiscount.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.User;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.VolleySingleton;


public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();
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
    private int discountPostion;
    private boolean dialog;
    private boolean success;
    private LoginSingleton loginSingleton;
    private boolean isFavorite;


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

        loginSingleton = LoginSingleton.getInstance(getContext());
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (getArguments() != null) {
            String gson = getArguments().getString("establishment");
            discountPostion = getArguments().getInt("discountPosition");
            establishment = new Gson().fromJson(gson, Establishment.class);
            discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));
            success = getArguments().getBoolean("success");
            dialog = getArguments().getBoolean("dialog");
        }
        if (dialog) {
            MessageDialogFragment dialogFragment = newInstance(getArguments().getString("message"));
            dialogFragment.show(fm, "");
        }

        GetFavorite(String.valueOf(establishment.getEstablishmentId()));

    }


    private void setFavorite(String establishmentId) {
        System.gc();
        DetailFragment.SetFavoriteResponseListener listener = new DetailFragment.SetFavoriteResponseListener();
        APIRequest.getInstance(getActivity()).setFavorite(listener, listener, establishmentId);
    }

    private void GetFavorite(String establishmentId) {
        System.gc();
        DetailFragment.GetFavoriteResponseListener listener = new DetailFragment.GetFavoriteResponseListener();
        APIRequest.getInstance(getActivity()).getFavoriteById(listener, listener, establishmentId);
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

        ImageButton favoriteButton = (ImageButton) mRootView.findViewById(R.id.favorite_button);
        favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24px);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setFavorite(String.valueOf(establishment.getEstablishmentId()));
            }
        });
        FloatingActionButton claimButton = (FloatingActionButton) mRootView.findViewById(R.id.claim_button);

        claimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
                    User user = loginSingleton.silentLogin();
                    if (user != null) {
                        goToScannerIfGranted();
                    } else {
                        loginSingleton.showLoginDialog();
                    }
                } else if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
                    goToScannerIfGranted();
                } else {
                    loginSingleton.showLoginDialog();
                }

            }
        });


        // Inflate the layout for this fragment
        return mRootView;
    }

    private void goToScannerIfGranted() {
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            goToScanner(establishment, discountPostion);
        } else {
            requestCameraPermissions();
        }
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(getContext(), R.string.camera_explanation, Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MainActivity.REQUEST_CAMERA_PERMISSION);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MainActivity.REQUEST_CAMERA_PERMISSION);
            }
        }
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
        claimsLeft.setText(Integer.toString(discount.getAmountLimit() - discount.getAmount()));
        timeLeft.setText(discount.getTimeRemaining(getContext()));
        discountDescription.setText(discount.getDescription());
    }


    private void goToScanner(Establishment establishment, int discountPostion) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPostion", discountPostion);

        ScannerFragment scannerFragment = new ScannerFragment();

        scannerFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scannerFragment, "scannerfrag")
                .addToBackStack(null)
                .commit();
    }

    public class SetFavoriteResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
            Log.e(TAG, "succes");
            NetworkResponse networkResponse = (NetworkResponse) response;

            JSONObject jsonHeaders = new JSONObject(networkResponse.headers);

            try {
                String header = jsonHeaders.get("X-Android-Response-Source").toString();

                isFavorite(header);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil 263" + error.toString());

//            JSONObject jsonHeaders = new JSONObject(error.networkResponse.headers);
//
//            try {
//                String errorCode = jsonHeaders.get("X-Android-Response-Source").toString();
//                Log.e(TAG,"Setfavorite Error: "+ errorCode);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

    }
    public void isFavorite(String response){
        ImageButton favoriteButton = (ImageButton) mRootView.findViewById(R.id.favorite_button);

        if (response.equals("NETWORK 201")){
            favoriteButton.setImageResource(R.drawable.ic_favorite_red_24px);
        }else{
            favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24px);
        }

    }
    public class GetFavoriteResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
            NetworkResponse networkResponse = (NetworkResponse) response;

            JSONObject jsonHeaders = new JSONObject(networkResponse.headers);

            try {
                String header = jsonHeaders.get("X-Android-Response-Source").toString();
                Log.e(TAG, " getFavorite word aangeroepen" + header);
                isFavorite(header);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {

//            JSONObject jsonHeaders = new JSONObject(error.networkResponse.headers);
//
//            try {
//                String errorCode = jsonHeaders.get("X-Android-Response-Source").toString();
//                Log.e(TAG,"Setfavorite Error: "+ errorCode);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

    }

}
