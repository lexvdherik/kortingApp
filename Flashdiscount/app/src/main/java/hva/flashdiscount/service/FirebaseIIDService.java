package hva.flashdiscount.service;

import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import hva.flashdiscount.network.APIRequest;

/**
 * TODO: Description.
 * @author Dr.Chruc
 * @since 1-12-2016
 */

public class FirebaseIIDService extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseIIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        System.gc();
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        FirebaseIIDService.PostFirebaseIIDResponseListener listener = new FirebaseIIDService.PostFirebaseIIDResponseListener();

        APIRequest.getInstance(getApplicationContext()).postDeviceToken(listener, listener, refreshedToken);
    }

    public class PostFirebaseIIDResponseListener implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onResponse(String token) {

            Log.i(TAG, token);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }


}


