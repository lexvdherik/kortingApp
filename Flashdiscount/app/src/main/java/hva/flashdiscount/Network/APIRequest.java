package hva.flashdiscount.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.Favorite;

public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();
//    private static final String HOST = "https://amazon.seanmolenaar.eu/api/";
    private static final String HOST = "http://145.28.159.215/api/";
    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";
    private static final String METHOD_POST_USER = "auth/login";
    private static final String METHOD_GET_FAVORITES = "favoriteestablishment/";
    private static final String METHOD_SET_SETTINGS = "favoriteestablishment/setnotifications";

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

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
        APIRequest.getInstance(getActivity()).cancelRequest(APIRequest.METHOD_GET_SETTINGS);
        getMainActivity().inProgress(false);
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

        return true;
    }

    public boolean getFavorites(Response.Listener<Favorite[]> responseListener, Response.ErrorListener errorListener, String idToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_FAVORITES, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_GET_FAVORITES).setShouldCache(true));

        return true;
    }

    public boolean setSettings(Response.Listener<> responseListener, Response.ErrorListener errorListener, String idToken, Favorite[] favorites) {

        JSONArray favoritesJson = new JSONArray(Arrays.asList(favorites));

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("favorites", favoritsJson);


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_SETTINGS, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_SET_SETTINGS).setShouldCache(true));

        return true;
    }


}
