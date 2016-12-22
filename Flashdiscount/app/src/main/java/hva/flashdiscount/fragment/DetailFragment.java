package hva.flashdiscount.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;

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
import com.google.gson.JsonObject;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.User;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.TransactionHandler;
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
    private int discountPosition;
    private boolean dialog;
    private LoginSingleton loginSingleton;
    private boolean isFavorite;
    private ActionBarDrawerToggle mDrawerToggle;

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
        if (getArguments() != null) {
            String gson = getArguments().getString("establishment");
            discountPosition = getArguments().getInt("discountPosition");
            establishment = new Gson().fromJson(gson, Establishment.class);
            discount = establishment.getDiscounts().get(discountPosition);
            dialog = getArguments().getBoolean("dialog");
        }
        if (dialog) {
            MessageDialogFragment dialogFragment = newInstance(getArguments().getString("message"));
            dialogFragment.show(fm, "");
        }

        if (loginSingleton.loggedIn()) {
            getFavorite(String.valueOf(establishment.getEstablishmentId()));
        }
        setUp();
    }


    private void setFavorite(String establishmentId) {
        DetailFragment.SetFavoriteResponseListener listener = new DetailFragment.SetFavoriteResponseListener();
        APIRequest.getInstance(getActivity()).setFavorite(listener, listener, establishmentId);
    }

    private void getFavorite(String establishmentId) {
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

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
                    setFavorite(String.valueOf(establishment.getEstablishmentId()));
                    return;
                }
                if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
                    User user = loginSingleton.silentLogin();
                    if (user != null) {
                        setFavorite(String.valueOf(establishment.getEstablishmentId()));
                        return;
                    }
                }
                loginSingleton.showLoginDialog();
            }
        });

        FloatingActionButton claimButton = (FloatingActionButton) mRootView.findViewById(R.id.claim_button);

        claimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loginSingleton.loggedIn() && !loginSingleton.loginExpired()) {
                    goToScannerIfGranted();
                    return;
                }
                if (loginSingleton.loggedIn() && loginSingleton.loginExpired()) {
                    User user = loginSingleton.silentLogin();
                    if (user != null) {
                        goToScannerIfGranted();
                        return;
                    }
                }
                loginSingleton.showLoginDialog();
            }
        });


        // Inflate the layout for this fragment
        return mRootView;
    }

    private void setUp() {
        // Cache the Activity as the frag handler, if necessary
        if (mFragHandler == null) {
            mFragHandler = (TransactionHandler.FragmentTransactionHandler) getActivity();
        }
        // Create the Toolbar home/close listener, if necessary
        if (mToolbarListener == null) {
            mToolbarListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            };
        }
        // Tell the Activity to let fragments handle the menu events
        mFragHandler.fragmentHandlingMenus(true, mToolbarListener);

        // Get a reference to the ActionBar only once
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Set up the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_btn_speak_now);
    }

    private void goToScannerIfGranted() {
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            goToScanner(establishment, discountPosition);
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

    @Override
    public void onStop() {
        super.onStop();

        // Clean up the UI
        mFragHandler.fragmentHandlingMenus(false, null);
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

    @SuppressLint("SetTextI18n")
    public void setDiscountText() {
        Log.i(TAG, discount.toString());
        claimsLeft.setText(discount.getAmountRemaining() + " " + getString(R.string.left));
        timeLeft.setText(discount.getTimeRemaining(getContext()));
        discountDescription.setText(discount.getDescription());
    }


    private void goToScanner(Establishment establishment, int discountPostion) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPosition", discountPostion);

        ScannerFragment scannerFragment = new ScannerFragment();

        scannerFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scannerFragment, "scannerfrag")
                .addToBackStack(null)
                .commit();
    }

    public void isFavorite(Boolean response) {
        ImageButton favoriteButton = (ImageButton) mRootView.findViewById(R.id.favorite_button);

        if (response) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_red_24px);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24px);
        }

    }

    public class SetFavoriteResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
            if (response instanceof NetworkResponse) {
                NetworkResponse networkResponse = (NetworkResponse) response;
                isFavorite((networkResponse.statusCode == 201));
            } else {
                JsonObject object = (JsonObject) response;
                isFavorite(((object.get("status").getAsInt() == 201)));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
                return;
            }
            error.printStackTrace();
        }

    }

    public class GetFavoriteResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
            if (response instanceof NetworkResponse) {
                NetworkResponse networkResponse = (NetworkResponse) response;
                isFavorite((networkResponse.statusCode == 200));
            } else {
                JsonObject object = (JsonObject) response;
                isFavorite(((object.get("status").getAsInt() == 200)));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

    }

}
