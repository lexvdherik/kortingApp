package hva.flashdiscount.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import hva.flashdiscount.model.Establishment;

public class APIRequest {
    private static final String TAG = APIRequest.class.getSimpleName();
    private static final String HOST = "https://amazon.seanmolenaar.eu/api/";
    private static final String METHOD_GET_ESTABLISHMENT = "establishment/";


    private Context mContext;
    private static APIRequest sInstance;

    private final RequestQueue mQueue;

    public static APIRequest getInstance(Context context) {
        Log.d(TAG, "APIRequest getInstance called");
        if (sInstance == null) {
            sInstance = new APIRequest(context);
        }
        return sInstance;
    }
    private APIRequest(Context context) {
        this.mContext = context;
        this.mQueue = Volley.newRequestQueue(context);
    }

    public void cancelRequest(String tag) {
        mQueue.cancelAll(tag);
    }

    public boolean getEstablishment(Response.Listener<Establishment[]> responseListener, Response.ErrorListener errorListener) {


        mQueue.add(new CustomRequest(Request.Method.POST, HOST + METHOD_GET_ESTABLISHMENT, null,
                responseListener, errorListener, Establishment[].class).setTag(METHOD_GET_ESTABLISHMENT).setShouldCache(true));

        return true;
    }




}
