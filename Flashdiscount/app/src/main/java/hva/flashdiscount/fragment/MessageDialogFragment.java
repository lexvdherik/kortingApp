package hva.flashdiscount.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import hva.flashdiscount.R;

/**
 * Created by Maiko on 24-11-2016.
 */

public class MessageDialogFragment extends DialogFragment {

    private LinearLayout layout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_message_dialog, container, false);
        layout = (LinearLayout) getActivity().findViewById(R.id.nav_header);

        getDialog().setTitle("SUCCESSS!!!");
        getDialog().setCanceledOnTouchOutside(true);

        rootView.findViewById(R.id.succes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();

            }
        });

        return rootView;
    }
}
