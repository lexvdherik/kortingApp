package hva.flashdiscount.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.Favorite;
import hva.flashdiscount.model.Token;
import hva.flashdiscount.utils.LoginSingleton;

public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();

    private static final String HOST = "https://amazon.seanmolenaar.eu/api/";
    //private static final String HOST = "http://145.28.186.199/api/";

    private static final String METHOD_GET_FAVORITES = "favoriteestablishment/";
    private static final String METHOD_SET_FAVORITE = "favoriteestablishment/favorite";
    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";
    private static final String METHOD_CLAIM_DISCOUNT = "discount/claim";
    private static final String METHOD_POST_USER = "auth/login";
    private static final String METHOD_SET_SETTINGS = "favoriteestablishment/setnotifications";
    private static final String METHOD_POST_FIID = "device/token";

    private static APIRequest sInstance;
    private final RequestQueue mQueue;
    private Context mContext;
    private LoginSingleton loginSingleton;

    private APIRequest(Context context) {
        mContext = context;
        mQueue = Volley.newRequestQueue(context);
        loginSingleton = LoginSingleton.getInstance(mContext);
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

    public boolean postUser(Response.Listener<Token> responseListener, Response.ErrorListener errorListener, String idToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);

        Log.i(TAG, "start post");

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_USER, params,
                responseListener, errorListener, Token.class).setTag(METHOD_POST_USER));

        return true;
    }

    public boolean postDeviceToken(Response.Listener<String> responseListener, Response.ErrorListener errorListener, String idToken, String deviceToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceToken", deviceToken);
        params.put("idToken", idToken);

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_FIID, params,
                responseListener, errorListener, Token.class).setTag(METHOD_POST_FIID));

        return true;
    }

    public boolean setFavorite(Response.Listener responseListener, Response.ErrorListener errorListener, String idToken, String establishmentId) {
        if (loginSingleton.loginExpired()) {
            idToken = loginSingleton.refreshToken();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("establishmentId", establishmentId);
        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_FAVORITE, params,
                responseListener, errorListener, null).setTag(METHOD_SET_FAVORITE));

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
        Log.i(TAG, favoritesJson.toString());
        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("settings", favoritesJson.toString());


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_SETTINGS, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_SET_SETTINGS).setShouldCache(true));

        return true;
    }

    public boolean claimDiscount(Response.Listener responseListener, Response.ErrorListener errorListener, String idToken, String establishmentId, String discountId) {
        if (loginSingleton.loginExpired()) {
            idToken = loginSingleton.refreshToken();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("establishmentId", establishmentId);
        params.put("discountId", discountId);
        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_CLAIM_DISCOUNT, params,
                responseListener, errorListener, null).setTag(METHOD_CLAIM_DISCOUNT));

        return true;
    }
}
