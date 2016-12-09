package hva.flashdiscount.network;

import com.android.volley.VolleyError;

class JsonRpcException extends VolleyError {

    JsonRpcException(String message) {
        super(message);
    }

    JsonRpcException(String message, Throwable cause) {
        super(message, cause);
    }
}