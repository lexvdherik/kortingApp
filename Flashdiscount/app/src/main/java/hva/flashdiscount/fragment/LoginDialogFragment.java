package hva.flashdiscount.fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Token;

public class LoginDialogFragment extends DialogFragment {

    private static final String TAG = LoginDialogFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleApiClient;
    private String token = "444953407805-n5m9qitvfcnrm8k3muc73sqv5g91dmmi.apps.googleusercontent.com";
    private LinearLayout layout;
    private GoogleSignInAccount acct;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);

        getDialog().setTitle("Login Dialog");
        getDialog().setCanceledOnTouchOutside(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    default:
                        Log.e(TAG, "WTF");
                }
            }
        });

        return rootView;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();

            postUser(acct.getIdToken());
            Log.e(TAG,acct.getIdToken().toString());

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("idToken", acct.getIdToken());

            Calendar c = Calendar.getInstance();
            c.setTime(Calendar.getInstance().getTime());
            c.add(Calendar.MINUTE, +59);

            editor.putString("expireDate", c.getTime().toString());
            editor.apply();

            this.dismiss();

            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            // updateUI(true);
        } else {

            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void postUser(String idToken) {
        System.gc();
        LoginDialogFragment.PostUserResponseListener listener = new LoginDialogFragment.PostUserResponseListener();
        APIRequest.getInstance(getActivity()).postUser(listener, listener, idToken);
    }

    public class PostUserResponseListener implements Response.Listener<Token>, Response.ErrorListener {

        @Override
        public void onResponse(Token token) {
            token.getExpireDate();

            ((ImageView) layout.findViewById(R.id.profile_picture)).setImageURI(acct.getPhotoUrl());
            String firstName = acct.getGivenName().substring(0, 1).toUpperCase() + acct.getGivenName().substring(1);
            String lastName = acct.getFamilyName().substring(0, 1).toUpperCase() + acct.getFamilyName().substring(1);

            ((TextView) layout.findViewById(R.id.naam)).setText(firstName + " " + lastName);

            ((TextView) layout.findViewById(R.id.email)).setText(acct.getEmail());
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", " joil" + error.getMessage());
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

}