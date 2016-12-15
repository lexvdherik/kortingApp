package hva.flashdiscount.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Token {

    private String expireDate;
    private Context mContext;

    public Token(Context context) {
        this.mContext = context;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("expire_date", expireDate);
        editor.apply();
    }
}
