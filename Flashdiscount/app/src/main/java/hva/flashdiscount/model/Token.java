package hva.flashdiscount.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Maiko on 22-11-2016.
 */

public class Token {

    private String expireDate;

    public Token(String expireDate, Context context) {
        this.expireDate = expireDate;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("expire_date", expireDate);
        editor.apply();
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
