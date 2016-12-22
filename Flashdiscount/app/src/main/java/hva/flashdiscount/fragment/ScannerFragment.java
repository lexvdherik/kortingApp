package hva.flashdiscount.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.zxing.Result;

import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.utils.TransactionHandler;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    private ZXingScannerView mScannerView;
    private Establishment establishment;
    private int dicountPosition;
    private Discount discount;
    private boolean wrongQr;
    private TransactionHandler.FragmentTransactionHandler mFragHandler;
    private View.OnClickListener mToolbarListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        wrongQr = false;
        if (getArguments() != null) {
            String gson = getArguments().getString("establishment");
            dicountPosition = getArguments().getInt("discountPosition");
            establishment = new Gson().fromJson(gson, Establishment.class);
            discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Log.i(TAG, sharedPref.getString("expire_date", ""));

        return mScannerView;
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
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
    }

    // Cleans up the UI changes and anything else necessary for the
    private void cleanUp() {
        // Tell the Activity that it can now handle menu events once again
        // Note that this also resets the Drawer icon+functionality
        mFragHandler.fragmentHandlingMenus(false, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        setUp();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up the UI
        cleanUp();
    }

    @Override
    public void handleResult(Result rawResult) {
        String establishmentId = rawResult.toString().substring(rawResult.toString().lastIndexOf("/") + 1);
        try {
            Integer.parseInt(establishmentId);
        } catch (NumberFormatException e) {
            goToDetailView(establishment, dicountPosition, true, "WRONG QR");
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerFragment.this);
            }
        }, 2000);

        Log.i(TAG, establishmentId);
        claimDiscount(establishmentId, String.valueOf(discount.getDiscountId()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void goToDetailView(Establishment establishment, int discountPosition, boolean success, String message) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPosition", discountPosition);
        arguments.putBoolean("dialog", true);
        arguments.putBoolean("success", success);
        arguments.putString("message", message);
        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void claimDiscount(String establishmentId, String discountId) {
        ClaimDiscountResponseListener listener = new ClaimDiscountResponseListener();
        APIRequest.getInstance(getActivity()).claimDiscount(listener, listener, establishmentId, discountId);
    }


    public class ClaimDiscountResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
            goToDetailView(establishment, dicountPosition, true, "SUCCESS");
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + String.valueOf(error.networkResponse.statusCode));
            goToDetailView(establishment, dicountPosition, false, String.valueOf(error.networkResponse.statusCode));

            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

}