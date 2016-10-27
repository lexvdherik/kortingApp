package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hva.flashdiscount.R;

public class LoginDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("doet", "het?");
        View rootView = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        getDialog().setTitle("Login Dialog");
        return rootView;
    }
}