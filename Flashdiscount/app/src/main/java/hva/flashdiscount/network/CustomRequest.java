package hva.flashdiscount.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

class CustomRequest<T> extends Request<T> {

    private static final String TAG = CustomRequest.class.getSimpleName();

    private Map<String, String> params;
    private Gson mGson = new GsonBuilder().serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private Class<?> mClass;
    private Response.Listener<T> listener;


    CustomRequest(int method, String url, Map<String, String> params,
                  Response.Listener<T> responseListener, Response.ErrorListener errorListener,
                  Class<?> aClass) {
        super(method, url, errorListener);

        this.listener = responseListener;
        this.params = params;
        this.mClass = aClass;
        setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


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
            Log.i(TAG, responseData);
            resp = (JsonObject) parser.parse(new StringReader(responseData));
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }

        JsonElement result = resp.get("result");
        if (result == null) {
            result = resp;
        }

        if (mClass == null && resp.get("message").toString().replace("\"", "").equals("OK")) {

            return Response.success((T) response, HttpHeaderParser.parseCacheHeaders(response));
        } else if (!resp.get("message").toString().replace("\"", "").equals("OK")) {
            return Response.error(new JsonRpcRemoteException(resp.get("message").toString()));
        } else if (resp.get("message").toString().replace("\"", "").equals("OK") && result.toString().equals("[]") && mClass.getSimpleName().equals("Boolean")) {
            return Response.success((T) mGson.fromJson("true", mClass), HttpHeaderParser.parseCacheHeaders(response));
        } else if (resp.get("message").toString().replace("\"", "").equals("OK") && result.toString().contains("house_id") && mClass.getSimpleName().equals("Boolean")) {
            return Response.success((T) mGson.fromJson("true", mClass), HttpHeaderParser.parseCacheHeaders(response));
        }

        return Response.success((T) mGson.fromJson(result.toString(), mClass), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}