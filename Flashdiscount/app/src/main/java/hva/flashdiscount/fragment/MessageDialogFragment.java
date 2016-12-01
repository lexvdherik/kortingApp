package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hva.flashdiscount.R;

public class MessageDialogFragment extends DialogFragment {

//    private LinearLayout layout;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.message = getArguments().getString("message");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_message_dialog, container, false);
//        layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);



//        String msg = "unknown error";
//        if(savedInstanceState != null) {
//            msg = savedInstanceState.get("message").toString();
//        }

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
