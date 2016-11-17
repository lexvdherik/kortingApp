package hva.flashdiscount.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import hva.flashdiscount.model.Establishment;

public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();
    private static final String HOST = "https://amazon.seanmolenaar.eu/api/";
    //private static final String HOST = "http://145.28.144.168/api/";
    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";
    private static final String METHOD_POST_USER = "auth/login";
    private static final String METHOD_GET_FAVOURITES = "favouriteEstablishment/";
    private Context mContext;

    private static APIRequest sInstance;
    private final RequestQueue mQueue;
    private Context mContext;

    private APIRequest(Context context) {
        this.mContext = context;
        this.mQueue = Volley.newRequestQueue(context);
    }

    public static APIRequest getInstance(Context context) {
        Log.d(TAG, "APIRequest getInstance called");
        if (sInstance == null) {
            sInstance = new APIRequest(context);
        }
        return sInstance;
    }

    public void cancelRequest(String tag) {
        mQueue.cancelAll(tag);
    }

    public boolean getEstablishment(Response.Listener<Establishment[]> responseListener, Response.ErrorListener errorListener) {

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_ESTABLISHMENT, null,
                responseListener, errorListener, Establishment[].class).setTag(METHOD_GET_ESTABLISHMENT).setShouldCache(true));

        return true;
    }

    public boolean postUser(Response.Listener responseListener, Response.ErrorListener errorListener, String idToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_USER, params,
                responseListener, errorListener, null).setTag(METHOD_POST_USER));

//        Log.e(TAG, );

        return true;
    }

    public boolean getFavourites(Response.Listener<Establishment[]> responseListener, Response.ErrorListener errorListener) {


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_FAVOURITES, null,
                responseListener, errorListener, Establishment[].class).setTag(METHOD_GET_FAVOURITES).setShouldCache(true));

        return true;
    }



}
