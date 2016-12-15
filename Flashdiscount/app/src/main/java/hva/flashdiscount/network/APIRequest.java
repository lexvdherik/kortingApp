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
import java.util.List;
import java.util.Map;

import hva.flashdiscount.model.Category;
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
    private static final String METHOD_GET_CATEGORIES = "category/";
    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";
    private static final String METHOD_CLAIM_DISCOUNT = "discount/claim";
    private static final String METHOD_POST_USER = "auth/login";
    private static final String METHOD_SET_SETTINGS = "favoriteestablishment/setnotifications";
    private static final String METHOD_POST_FIID = "device/token";

    private static APIRequest sInstance;
    private final RequestQueue mQueue;
    private LoginSingleton loginSingleton;

    private APIRequest(Context context) {
        mQueue = Volley.newRequestQueue(context);
        loginSingleton = LoginSingleton.getInstance(context);
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

    public boolean getCategories(Response.Listener<Category[]> responseListener, Response.ErrorListener errorListener) {

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_CATEGORIES, null,
                responseListener, errorListener, Category[].class).setTag(METHOD_GET_CATEGORIES).setShouldCache(true));
        return true;
    }


    public boolean postUser(Response.Listener<Token> responseListener, Response.ErrorListener errorListener) {
        Map<String, Object> params = loginSingleton.authorizedRequestParameters();

        Log.i(TAG, "start post");

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_USER, params,
                responseListener, errorListener, Token.class).setTag(METHOD_POST_USER));

        return true;
    }

    public boolean postDeviceToken(Response.Listener<String> responseListener, Response.ErrorListener errorListener, String deviceToken) {
        Map<String, Object> params = loginSingleton.authorizedRequestParameters();
        params.put("deviceToken", deviceToken);

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_FIID, params,
                responseListener, errorListener, Token.class).setTag(METHOD_POST_FIID));

        return true;
    }

    public boolean setFavorite(Response.Listener responseListener, Response.ErrorListener errorListener, String establishmentId) {
        Map<String, Object> params = loginSingleton.authorizedRequestParameters();
        params.put("establishmentId", establishmentId);
        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_FAVORITE, params,
                responseListener, errorListener, null).setTag(METHOD_SET_FAVORITE));

        return true;
    }

    public boolean getFavorites(Response.Listener<Favorite[]> responseListener, Response.ErrorListener errorListener) {
        Map<String, Object> params = loginSingleton.authorizedRequestParameters();

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_FAVORITES, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_GET_FAVORITES).setShouldCache(true));

        return true;
    }

    public boolean setSettings(Response.Listener responseListener, Response.ErrorListener errorListener, Favorite[] favorites) {
        List<Favorite> test = Arrays.asList(favorites);

        Gson gson = new GsonBuilder().create();
        JsonArray favoritesJson = gson.toJsonTree(test).getAsJsonArray();
        Log.i(TAG, favoritesJson.toString());

        Map<String, Object> params = loginSingleton.authorizedRequestParameters();
        params.put("favorites", favoritesJson.toString());


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_SETTINGS, params,
                responseListener, errorListener, Favorite[].class).setTag(METHOD_SET_SETTINGS).setShouldCache(true));

        return true;
    }

    public boolean claimDiscount(Response.Listener responseListener, Response.ErrorListener errorListener, String establishmentId, String discountId) {
        Map<String, Object> params = loginSingleton.authorizedRequestParameters();

        params.put("establishmentId", establishmentId);
        params.put("discountId", discountId);
        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_CLAIM_DISCOUNT, params,
                responseListener, errorListener, null).setTag(METHOD_CLAIM_DISCOUNT));

        return true;
    }
}
