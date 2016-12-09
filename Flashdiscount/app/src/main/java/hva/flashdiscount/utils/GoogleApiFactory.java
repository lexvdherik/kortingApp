package hva.flashdiscount.utils;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import hva.flashdiscount.R;

/**
 * Create a google Client.
 *
 * @author smillernl
 * @since 9-12-16
 */

public final class GoogleApiFactory {
    private GoogleApiFactory() {
        throw new AssertionError("Stop instantiating factories you moron!");
    }

    public static GoogleApiClient getClient(Context mContext) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestIdToken(mContext.getResources().getString(R.string.token))
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
}
