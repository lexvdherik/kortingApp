package hva.flashdiscount.network;


public final class JsonRpcRemoteException extends JsonRpcException {

    private final Integer code;
    private final String msg;
    private final String data;

    public JsonRpcRemoteException(String msg) {
        super(msg);
        this.code = null;
        this.msg = msg;
        this.data = null;
    }

    public JsonRpcRemoteException(Integer code, String msg, String data) {
        super(format(code, msg, data));
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    private static String format(Integer code, String message, String data) {
        StringBuilder str = new StringBuilder();
        str.append("jsonrpc error");
        if (code != null) {
            str.append("[").append(code).append("]");
        }
        str.append(" : ");
        if (message != null) {
            str.append(message);
        }
        if (data != null) {
            str.append("\n").append("Caused by ").append(data);
        }
        return str.toString();
    }

}