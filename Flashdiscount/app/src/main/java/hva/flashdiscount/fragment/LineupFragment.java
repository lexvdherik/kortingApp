package hva.flashdiscount.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.DiscountAdapter;
import hva.flashdiscount.adapter.ExpandableListAdapter;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

/**
 * Created by Anthony on 09-Oct-16.
 */

public class LineupFragment extends Fragment {

    View rootView;
    ExpandableListView lv;
    private String[] groups;
    private String[][] children;

    private ArrayList<Establishment> establishments;
    private DiscountAdapter discountAdapter;
    private final String server_url = "http://145.28.236.97/api/establishment/getall";
    private RequestQueue requestQueue;
    private Context context;
    OnListDataListener listDataCallback;


    public LineupFragment() {

    }

    public interface OnListDataListener {
        public void onListDataChange(DiscountAdapter da);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        requestQueue = Volley.newRequestQueue(context);

        establishments = new ArrayList<Establishment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lineup, container, false);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject e = jsonArray.getJSONObject(i);

                                JSONObject c = e.getJSONObject("company");
                                JSONArray ds = e.getJSONArray("discounts");

                                Company company = new Company(Integer.valueOf(c.getString("categoryId")), c.getString("name"));
                                ArrayList<Discount> discounts = new ArrayList<Discount>();
                                for(int j = 0; j < ds.length(); j++){

                                    JSONObject dsObj = ds.getJSONObject(j);


                                    Discount d = new Discount(dsObj.getString("description"), dsObj.getString("endTime"));

                                    discounts.add(d);
                                }

                                establishments.add(new Establishment(company, discounts));


                            }

                            lv = (ExpandableListView) getActivity().findViewById(R.id.expListView);
                            lv.setAdapter(new ExpandableListAdapter(establishments, getActivity()));
                            lv.setGroupIndicator(null);
                            lv.deferNotifyDataSetChanged();

                            try {
                                listDataCallback = (LineupFragment.OnListDataListener) context;
                                listDataCallback.onListDataChange(discountAdapter);
                            } catch (ClassCastException e) {
                                throw new ClassCastException(context.toString()
                                        + " must implement OnListDataListener");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPERRORR", error.toString());
            }
        }

        );
        requestQueue.add(jsonObjectRequest);
    }
}