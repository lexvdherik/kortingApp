package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
        switch (errorCode) {
            case "NETWORK 400": msg = "Kon de korting niet claimen";
                break;
            case "NETWORK 401": msg = "U bent niet ingelogd";
                break;
            case "NETWORK 404": msg = "De aanbieding kon niet worden gevonden";
                break;
            case "NETWORK 409": msg = "U heeft deze aanbieding al te vaak geclaimd";
                break;
            case "NETWORK 500": msg = "Server buiten gebruik";
                break;
            case "WRONG QR":  msg = "Verkeerde QR code gescant";
                break;
            default: msg = "Onbekende error";
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
