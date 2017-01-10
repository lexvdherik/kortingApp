package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hva.flashdiscount.R;

public class MessageDialogFragment extends DialogFragment {

    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.message = errorCodeToMessage(getArguments().getString("message"));

    }

    private String errorCodeToMessage(String errorCode) {
        String msg;
        Log.e("errorcode", errorCode);
        switch (errorCode) {
            case "400":
                msg = getString(R.string.couldnt_claim_discount);
                break;
            case "401":
                msg = getString(R.string.not_logged_in);
                break;
            case "404":
                msg = getString(R.string.discount_not_found);
                break;
            case "409":
                msg = getString(R.string.to_many_claims);
                break;
            case "500":
                msg = getString(R.string.server_internal_error);
                break;
            case "WRONG QR":
                msg = getString(R.string.wrong_qr_code);
                break;
            default:
                msg = getString(R.string.unknown_error);
                break;
        }
        return msg;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_message_dialog, container, false);

        ((TextView) rootView.findViewById(R.id.message_text)).setText(this.message);
        getDialog().setCanceledOnTouchOutside(true);

        rootView.findViewById(R.id.dismiss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }
}
