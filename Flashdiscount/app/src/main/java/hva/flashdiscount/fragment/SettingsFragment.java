package hva.flashdiscount.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import hva.flashdiscount.Network.APIRequest;
import hva.flashdiscount.R;
import hva.flashdiscount.adapter.SettingsAdapter;
import hva.flashdiscount.model.Establishment;

public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listItems;
    ListView listView;

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
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
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.notification_list);

        lv.setAdapter(new SettingsAdapter(getActivity()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getFavouritesFromAPI();
    }

    private void getFavouritesFromAPI() {
        System.gc();
        GetFavouritesResponseListener listener = new GetFavouritesResponseListener();
        APIRequest.getInstance(getActivity()).getFavourites(listener, listener);

    }

    public class GetFavouritesResponseListener implements Response.Listener<Establishment[]>, Response.ErrorListener {

        @Override
        public void onResponse(final Establishment[] establishments) {

            listView = (ListView) getActivity().findViewById(R.id.notification_list);
            listView.setAdapter(new SettingsAdapter(getActivity()));

        }

        @Override
        public void onErrorResponse(VolleyError error) {

            if (error instanceof NoConnectionError) {
                Log.e(TAG, "No connection!");
            }
        }


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
