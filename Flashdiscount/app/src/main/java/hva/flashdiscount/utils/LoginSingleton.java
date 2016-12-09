package hva.flashdiscount.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hva.flashdiscount.fragment.LoginDialogFragment;
import hva.flashdiscount.model.User;

public class LoginSingleton {
    private static final String TAG = LoginSingleton.class.getSimpleName();
    private static LoginSingleton mInstance = null;
    private Context mContext;
    private GoogleSignInAccount acct = null;

    private LoginSingleton(final Context context) {
        mContext = context;
    }

    public static LoginSingleton getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new LoginSingleton(context);
        }
        return mInstance;
    }

    public boolean loggedIn() {
        return (acct != null);
    }

    public Boolean loginExpired() {

        DateTimeZone london = DateTimeZone.forID("Europe/London");
        DateTime current = DateTime.now(london);
        Calendar currentDate;
        currentDate = current.toCalendar(Locale.ENGLISH);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        Calendar expireDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        try {
            expireDate.setTime(sdf.parse(sharedPref.getString("expire_date", "")));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return currentDate.compareTo(expireDate) == -1;
    }

    @Nullable
    public String refreshToken() {
        GoogleApiClient mGoogleApiClient = GoogleApiFactory.getClient(mContext);

        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (!pendingResult.isDone()) {
            return null;
        }
        acct = pendingResult.get().getSignInAccount();
        User user = new User(acct);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("picture", user.getPicture().toString());

        editor.apply();

        return acct.getIdToken();
    }

    public void showLoginDialog() {
        LoginDialogFragment dialogFragment = new LoginDialogFragment();
        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        dialogFragment.show(fm, "Login Fragment");
    }

    public Map<String, Object> authorizedRequestParameters() {
        String idToken = null;
        if (!loginExpired()) {
            idToken = refreshToken();
        }

        if (idToken == null) {
            silentLogin();
            return authorizedRequestParameters();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("idToken", idToken);

        return params;
    }

    public User silentLogin() {

        GoogleApiClient mGoogleApiClient = GoogleApiFactory.getClient(mContext);

        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (!pendingResult.isDone()) {
            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
            LoginDialogFragment dialogFragment = new LoginDialogFragment();
            dialogFragment.show(fm, "Login Fragment");

            return null;
        }
        acct = pendingResult.get().getSignInAccount();

        return new User(acct);
    }
}
