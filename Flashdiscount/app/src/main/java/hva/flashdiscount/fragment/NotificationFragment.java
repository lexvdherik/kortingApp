package hva.flashdiscount.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import hva.flashdiscount.R;

public class NotificationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listItems;

    private static final String TAG = NotificationFragment.class.getSimpleName();

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        listItems = fillList();
        ListView lv = (ListView) rootView.findViewById(R.id.notification_list);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listItems);

//        arrayAdapter =
//                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(itemsAdapter);

        return rootView;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public ArrayList<String> fillList() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("CoffeeCompany");
        temp.add("Cafe Noire");
        temp.add("Starbucks");
        temp.add("De Roeter");

        return temp;
    }
}
