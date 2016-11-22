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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

        DateTimeZone london = DateTimeZone.forID( "Europe/London" );
        DateTime current = DateTime.now( london );
        Calendar currentDate;
        currentDate = current.toCalendar(Locale.ENGLISH);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        Calendar expireDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        Log.e(TAG, "expireDate = " + sharedPref.getString("expire_date", ""));

        try{
            expireDate.setTime(sdf.parse(sharedPref.getString("expire_date", "")));
        } catch(ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        if(currentDate.compareTo(expireDate) != -1) {

            return false;
        } else {

            return true;
        }
    }

    public void refreshToken(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(applicationContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

            OptionalPendingResult<GoogleSignInResult> pendingResult =
                    Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (pendingResult.isDone()) {

                acct = pendingResult.get().getSignInAccount();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("idToken", acct.getIdToken());

                Calendar c = Calendar.getInstance();
                c.setTime(Calendar.getInstance().getTime());
                c.add(Calendar.MINUTE, +59);

                editor.putString("expireDate", c.getTime().toString());
                editor.apply();

            }

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
            Log.e("testresponse", responseData);
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