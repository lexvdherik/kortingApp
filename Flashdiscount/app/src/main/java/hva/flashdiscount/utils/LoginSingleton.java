package hva.flashdiscount.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import hva.flashdiscount.R;
import hva.flashdiscount.fragment.LoginDialogFragment;
import hva.flashdiscount.layout.RoundNetworkImageView;
import hva.flashdiscount.model.User;

public class LoginSingleton {
    private static final String TAG = LoginSingleton.class.getSimpleName();
    private static LoginSingleton mInstance = null;
    private static Context mContext;
    private SharedPreferences sharedPref;
    private GoogleSignInAccount acct;

    private LoginSingleton(final Context context) {
        this.mContext = context;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static LoginSingleton getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new LoginSingleton(context);
        }
        return mInstance;
    }

    public boolean loggedIn () {
        return sharedPref.contains("idToken");
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

    public String refreshToken() {
        String token = mContext.getString(R.string.token);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            acct = pendingResult.get().getSignInAccount();
            User user = new User(acct);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("idToken", user.getGoogleId());
            editor.putString("name", user.getName());
            editor.putString("email", user.getEmail());
            editor.putString("picture", user.getPicture().toString());

            editor.apply();
        }

        return acct.getIdToken();
    }

    public void showLoginDialog() {
        LoginDialogFragment dialogFragment = new LoginDialogFragment();
        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        dialogFragment.show(fm, "Login Fragment");
    }

    public User silentLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getResources().getString(R.string.token))
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            GoogleSignInAccount acct = pendingResult.get().getSignInAccount();

            return new User(acct);
        } else {
            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
            LoginDialogFragment dialogFragment = new LoginDialogFragment();
            dialogFragment.show(fm, "Login Fragment");

            return null;
        }
    }
}
