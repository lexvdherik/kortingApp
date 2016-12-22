package hva.flashdiscount.utils;

import android.view.View;

/**
 * Created by jawad on 15/02/15.
 *
 * Defines exchange requests (for fragments to request the activity to change to another frag)
 * -> Better to put this by itself rather than stuffing ActivityMain with this
 */
public class TransactionHandler {
    public enum RequestType {
        MAIN_DRAWER,        // One of the main drawer items
        GOAL_ADDER          // The goal adding fragment
    }

    public interface FragmentTransactionHandler {
        void changeFragment(RequestType requestType, boolean addToBackstack);
        void changeFragment(RequestType requestType, boolean addToBackstack, int option);
        void fragmentHandlingMenus(boolean isFragmentHandingMenus, View.OnClickListener newHomeButtonListener);
    }
}
