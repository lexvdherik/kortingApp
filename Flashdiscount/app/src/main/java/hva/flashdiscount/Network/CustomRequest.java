package hva.flashdiscount.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import hva.flashdiscount.MainActivity;

class CustomRequest<T> extends Request<T> {

    private static final String TAG = CustomRequest.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleApiClient;
    private Map<String, String> params;
    private Gson mGson = new GsonBuilder().serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private GoogleSignInAccount acct;
    private Class<?> mClass;
    private Response.Listener<T> listener;
    private Context applicationContext;
    private String idToken;
    private String token = "444953407805-n5m9qitvfcnrm8k3muc73sqv5g91dmmi.apps.googleusercontent.com";


    CustomRequest(int method, String url, Map<String, String> params, Response.Listener<T> reponseListener, Response.ErrorListener errorListener, Class<?> clazz) {
        super(method, url, errorListener);
        this.applicationContext = MainActivity.getContextOfApplication();
        this.listener = reponseListener;
        this.params = params;
        this.mClass = clazz;
        setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        loginExpired();

    }

    private Boolean loginExpired() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(applicationContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(Calendar.getInstance().getTime());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        Calendar expireDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        try{
            expireDate.setTime(sdf.parse(sharedPref.getString("expireDate", "")));
        } catch(ParseException e) {
            Log.e(TAG, e.getMessage());
        }

//        if(currentDate.compareTo(expireDate) != -1) {

            OptionalPendingResult<GoogleSignInResult> pendingResult =
                    Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (pendingResult.isDone()) {
                // There's immediate result available.
                pendingResult.get();
//            }


        } else {
            this.idToken = sharedPref.getString("idToken", "");
        }

        return true;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }


    @Override
    public Response<T> parseNetworkResponse(NetworkResponse response) {
        String responseData = null;

        try {
            responseData = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        JsonObject resp;

        try {
            assert responseData != null;
            resp = (JsonObject) parser.parse(new StringReader(responseData));
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }

        Log.e("JsonRpcRequest Response", responseData);

        JsonElement result = resp.get("result");
        if (result == null) {
            result = resp;
        }

        Log.e(TAG, "message: " + resp.get("message").toString().replace("\"", ""));

        if (mClass == null && resp.get("message").toString().replace("\"", "").equals("OK")) {
            return Response.success((T) response, HttpHeaderParser.parseCacheHeaders(response));
        } else if (!resp.get("message").toString().replace("\"", "").equals("OK")) {
            return Response.error(new JsonRpcRemoteException(resp.get("message").toString()));
        } else if (resp.get("message").toString().replace("\"", "").equals("OK") && result.toString().equals("[]") && mClass.getSimpleName().equals("Boolean")) {
            return Response.success((T) mGson.fromJson("true", mClass), HttpHeaderParser.parseCacheHeaders(response));
        } else if (resp.get("message").toString().replace("\"", "").equals("OK") && result.toString().contains("house_id") && mClass.getSimpleName().equals("Boolean")) {
            return Response.success((T) mGson.fromJson("true", mClass), HttpHeaderParser.parseCacheHeaders(response));
        }

        Log.e("da", mGson.fromJson(result.toString(), mClass).toString());
        return Response.success((T) mGson.fromJson(result.toString(), mClass), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}