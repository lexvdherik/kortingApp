package hva.flashdiscount.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.MenuDrawerAdapter;
import hva.flashdiscount.model.Establishment;
import hva.flashdiscount.model.User;

public class LoginDialogFragment extends DialogFragment {

    private final int RC_SIGN_IN = 1;
    private static final String TAG = LoginDialogFragment.class.getSimpleName();
    private LinearLayout layout;

    GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_dialog, container, false);

        getDialog().setTitle("Login Dialog");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

         mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                 .addScope(Plus.SCOPE_PLUS_LOGIN)
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
            Log.e(TAG,"Ik chill hem in hier");
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, acct.getDisplayName());
            PostUser(acct.getId(),acct.getDisplayName(),acct.getEmail(), acct.getPhotoUrl().toString());

            this.dismiss();

           // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
           // updateUI(true);
        } else {

            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }

    private void PostUser(String googleId, String name , String email, String picture) {
        System.gc();
        LoginDialogFragment.PostUserResponseListener listener = new LoginDialogFragment.PostUserResponseListener();
        APIRequest.getInstance(getActivity()).postUser(listener, listener, googleId , email, name,picture);
    }

    public class PostUserResponseListener implements Response.Listener<User>, Response.ErrorListener {

        @Override
        public void onResponse(User user) {
            Log.e(TAG, user.getGoogleId());
            layout = (LinearLayout) getView().findViewById(R.id.nav_header);
            ((TextView) layout.findViewById(R.id.naam)).setText(user.getName());
            ((TextView) layout.findViewById(R.id.email)).setText(user.getEmailAddress());
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG + " content", error.getMessage());
            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }

    }

}