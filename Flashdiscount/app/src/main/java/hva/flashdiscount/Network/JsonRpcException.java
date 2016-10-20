package hva.flashdiscount.Network;

import com.android.volley.VolleyError;

public class JsonRpcException extends VolleyError {

    public JsonRpcException(String message) {
        super(message);
    }

    public JsonRpcException(String message, Throwable cause) {
        super(message, cause);
    }
}