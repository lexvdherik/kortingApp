package hva.flashdiscount.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.Token;

public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();

    private static final String HOST = "https://amazon.seanmolenaar.eu/api/";
    //private static final String HOST = "http://145.28.186.199/api/";

    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";
    private static final String METHOD_SET_FAVORITE = "favoriteestablishment/favorite";
    private static final String METHOD_POST_USER = "auth/login";

    private static APIRequest sInstance;
    private final RequestQueue mQueue;
    private Context mContext;
    private String token = "444953407805-n5m9qitvfcnrm8k3muc73sqv5g91dmmi.apps.googleusercontent.com";
    GoogleApiClient mGoogleApiClient;

    private GoogleSignInAccount acct;

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

    public boolean postUser(Response.Listener<Token> responseListener, Response.ErrorListener errorListener, String idToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);

        Log.e(TAG, "start post");

        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_POST_USER, params,
                responseListener, errorListener, Token.class).setTag(METHOD_POST_USER));

        return true;
    }

    public boolean setFavorite(Response.Listener responseListener, Response.ErrorListener errorListener, String idToken, String establishmentId) {
        if (loginExpired()) {
            idToken = refreshToken();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);
        params.put("establishmentId", establishmentId);
        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_SET_FAVORITE, params,
                responseListener, errorListener, null).setTag(METHOD_SET_FAVORITE));

        return true;
    }

    private Boolean loginExpired() {

        DateTimeZone london = DateTimeZone.forID("Europe/London");
        DateTime current = DateTime.now(london);
        Calendar currentDate;
        currentDate = current.toCalendar(Locale.ENGLISH);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        Calendar expireDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        try {
            expireDate.setTime(sdf.parse(sharedPref.getString("expire_date", "")));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return currentDate.compareTo(expireDate) == -1;
    }

    public String refreshToken() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {

            acct = pendingResult.get().getSignInAccount();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("idToken", acct.getIdToken());

            editor.apply();

        }

        return acct.getIdToken();
    }




}
