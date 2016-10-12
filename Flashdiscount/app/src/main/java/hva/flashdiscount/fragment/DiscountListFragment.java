package hva.flashdiscount.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import hva.flashdiscount.model.Discount;


public class DiscountListFragment extends ListFragment {
    public static final String ARG_PAGE = "LIST";
    private ArrayList<Discount> discounts;
    private DiscountAdapter discountAdapter;
    private RequestQueue requestQueue;
    private Context context;
    private String title;
    private int page;


    public static DiscountListFragment newInstance(int page, String title){
        DiscountListFragment discountListFragment = new DiscountListFragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("List", title);
        discountListFragment.setArguments(args);
        return discountListFragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        requestQueue = Volley.newRequestQueue(context);

        discounts = new ArrayList<Discount>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, serverUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject discount = jsonArray.getJSONObject(i);

                                JSONObject e = discount.getJSONObject("establishment");
                                JSONObject c = e.getJSONObject("company");

//                                Company company = new Company(Integer.valueOf(c.getString("categoryId")), c.getString("name"));
//                                Establishment establishment = new Establishment(company);
//                                Discount d = new Discount(discount.getString("description"), establishment, discount.getString("endTime"));

//                                discounts.add(d);
                            }

                            if (discountAdapter == null) {
                                discountAdapter = new DiscountAdapter(getActivity(), discounts);
                                setListAdapter(discountAdapter);
                            } else {
                                discountAdapter.clear();
                                discountAdapter.addAll(discounts);
                                discountAdapter.notifyDataSetChanged();
                            }

                            try {
                                listDataCallback = (OnListDataListener) context;
                                listDataCallback.onListDataChange(discountAdapter);
                            } catch (ClassCastException e) {
                                throw new ClassCastException(context.toString()
                                        + " must implement OnListDataListener");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPERROR", error.toString());
            }
        }

        );
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    public interface OnListDataListener {
        void onListDataChange(DiscountAdapter da);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_PAGE);
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        onActivityCreated(savedInstanceState);
//        View view = inflater.inflate(R.layout.list_discount_row, container, false);
//        return view;
//
//    }

}
