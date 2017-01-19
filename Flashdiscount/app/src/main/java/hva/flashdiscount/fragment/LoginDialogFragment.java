package hva.flashdiscount.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import hva.flashdiscount.MainActivity;
import hva.flashdiscount.R;
import hva.flashdiscount.model.Token;
import hva.flashdiscount.model.User;
import hva.flashdiscount.network.APIRequest;
import hva.flashdiscount.utils.GoogleApiFactory;
import hva.flashdiscount.utils.LoginSingleton;
import hva.flashdiscount.utils.VolleySingleton;

public class LoginDialogFragment extends DialogFragment {

    private static final String TAG = LoginDialogFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    private LinearLayout layout;
    private GoogleSignInAccount acct;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);

        getDialog().setTitle(R.string.login_popup_title);
        getDialog().setCanceledOnTouchOutside(true);

        rootView.findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        return rootView;
    }

    private void signIn() {
        GoogleApiClient mGoogleApiClient = GoogleApiFactory.getClient(getContext());
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
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            if (acct == null) {
                return;
            }
            LoginSingleton loginSingleton = LoginSingleton.getInstance(getContext());
            loginSingleton.refreshToken();
            postUser(acct.getIdToken());

        } else {
            Log.i(TAG, "Something went wrong... You signed out");
        }
    }

    private void postUser(String idToken) {
        LoginDialogFragment.PostUserResponseListener listener = new LoginDialogFragment.PostUserResponseListener();
        APIRequest.getInstance(getActivity().getApplicationContext()).postUser(idToken, listener, listener);
    }

    public class PostUserResponseListener implements Response.Listener<Token>, Response.ErrorListener {

        @Override
        public void onResponse(Token token) {
            Token t = new Token(getContext());
            t.setExpireDate(token.getExpireDate());

            Log.i(TAG, "Expire date of token = " + token.getExpireDate());

            User user = new User(acct);
            ((MainActivity) getActivity()).user = user;

            if (layout == null) {
                getDialog().dismiss();
            }

            ImageLoader mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
//            RoundNetworkImageView image = (RoundNetworkImageView) layout.findViewById(R.id.profile_picture);
//            if (image != null) {
//                image.setImageUrl(user.getPicture().toString(), mImageLoader);
//            }
//
//            ((TextView) layout.findViewById(R.id.naam)).setText(user.getName());
//            ((TextView) layout.findViewById(R.id.email)).setText(user.getEmail());

            getDialog().dismiss();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                Log.w(TAG, "No connection!");
            }
        }

    }

}