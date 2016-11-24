package hva.flashdiscount.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public boolean setSettings(Response.Listener responseListener, Response.ErrorListener errorListener, String idToken, Favorite[] favorites) {

        List<Favorite> test = Arrays.asList(favorites);
//        JSONArray favoritesJson = new JSONArray();

        Gson gson = new GsonBuilder().create();
        JsonArray favoritesJson = gson.toJsonTree(test).getAsJsonArray();
        Log.e(TAG, favoritesJson.toString());
        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("settings", favoritesJson.toString());


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_SETTINGS, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_SET_SETTINGS).setShouldCache(true));

        return true;
    }


}
