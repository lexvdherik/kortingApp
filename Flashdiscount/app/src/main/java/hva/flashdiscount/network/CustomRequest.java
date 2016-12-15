package hva.flashdiscount.network;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
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
import java.util.Objects;

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
        JsonObject resp = getJSONcontentOfResponse(response);
        if (response.statusCode > 300 && response.statusCode < 200) {
            String error = (resp == null)
                    ? "NO ERROR MESSAGE FOUND"
                    : resp.get("message").toString();

            return Response.error(new JsonRpcRemoteException(error));
        }

        if (resp == null) {
            return Response.success((T) response, HttpHeaderParser.parseCacheHeaders(response));
        }

        JsonElement result = resp.get("result");
        if (result == null) {
            result = resp;
        }

        if (mClass == null) {
            return Response.success((T) resp, HttpHeaderParser.parseCacheHeaders(response));
        }

        if (mClass.getSimpleName().equals(Boolean.class.getSimpleName())) {
            return Response.success((T) mGson.fromJson("true", mClass), HttpHeaderParser.parseCacheHeaders(response));
        }

        return Response.success((T) mGson.fromJson(result.toString(), mClass), HttpHeaderParser.parseCacheHeaders(response));
    }


    /**
     * Parse a network response to an object.
     *
     * @param response {@link NetworkResponse} Networkresponse to parse
     * @return null|{@link JsonObject} Object or null if failure
     */
    @Nullable
    private JsonObject getJSONcontentOfResponse(NetworkResponse response) {
        String responseData = null;

        try {
            responseData = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (responseData == null || Objects.equals(responseData, "")) {
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonObject resp;

        try {
            Log.i(TAG, responseData);
            resp = (JsonObject) parser.parse(new StringReader(responseData));
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return resp;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}