package hva.flashdiscount.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.zxing.Result;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.network.APIRequest;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final String TAG = ScannerFragment.class.getSimpleName();

    private ZXingScannerView mScannerView;
    private Establishment establishment;
    private int dicountPosition;
    private Discount discount;
    private String idToken;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());

        if (getArguments() != null) {
            String gson = getArguments().getString("establishment");
            dicountPosition = getArguments().getInt("discountPosition");
            establishment = new Gson().fromJson(gson, Establishment.class);
            discount = establishment.getDiscounts().get(getArguments().getInt("discountPosition"));
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                ((MainActivity) getActivity()).getContextOfApplication()
        );
        Log.e(TAG,sharedPref.getString("expire_date",""));
        idToken = sharedPref.getString("idToken", "");
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

       String establishmentId = rawResult.toString().substring(rawResult.toString().lastIndexOf("/") + 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerFragment.this);
            }
        }, 2000);
        Log.e(TAG,establishmentId);
        claimDiscount(idToken,establishmentId,String.valueOf(discount.getDiscountId()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void goToDetailView(Establishment establishment, int discountPostion) {

        Bundle arguments = new Bundle();
        arguments.putString("establishment", new Gson().toJson(establishment));
        arguments.putInt("discountPostion", discountPostion);
        arguments.putBoolean("dialog",true);
        arguments.putBoolean("succes", true);
        DetailFragment detailFragment = new DetailFragment();

        detailFragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void claimDiscount(String idToken,String EstablishmentId,String discountId) {
        System.gc();
        ScannerFragment.claimDiscountResponseListener listener = new ScannerFragment.claimDiscountResponseListener();
        APIRequest.getInstance(getActivity()).claimDisount(listener, listener, idToken,EstablishmentId,discountId);
    }



    public class claimDiscountResponseListener implements Response.Listener, Response.ErrorListener {

        @Override
        public void onResponse(Object response) {
           // NetworkResponse response1 = (NetworkResponse) response;
            //Log.e(TAG,response.);
            goToDetailView(establishment,dicountPosition);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + error.getMessage());
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

}