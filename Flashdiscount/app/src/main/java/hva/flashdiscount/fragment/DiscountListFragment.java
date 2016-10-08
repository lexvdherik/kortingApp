package hva.flashdiscount.fragment;


import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
import java.util.zip.Inflater;

import hva.flashdiscount.R;
import hva.flashdiscount.adapter.DiscountAdapter;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;


public class DiscountListFragment extends ListFragment {

    private ArrayList<Discount> discounts;
    private DiscountAdapter discountAdapter;
    private final String serverUrl = "https://amazon.seanmolenaar.eu/api/discount/getall";
    private RequestQueue requestQueue;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("WWWWWWWWWWWWW", "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        context = getActivity();
        super.onActivityCreated(savedInstanceState);
        requestQueue = Volley.newRequestQueue(context);

        discounts = new ArrayList<Discount>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, serverUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESP", "onRespons");
                        try {
                            Log.e("RESP", "try");

                            JSONArray jsonArray = response.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject discount = jsonArray.getJSONObject(i);

                                JSONObject e = discount.getJSONObject("establishment");
                                JSONObject c = e.getJSONObject("company");

                                Company company = new Company(Integer.valueOf(c.getString("categoryId")), c.getString("name"));
                                Establishment establishment = new Establishment(company);
                                Discount d = new Discount(discount.getString("description"), establishment, discount.getString("endTime"));


                                discounts.add(d);
                                Log.e("RESP", "dicountsAdd");
                            }
                            Log.e("RESP", "0");
                            discountAdapter = new DiscountAdapter(getActivity(), discounts);
                            setListAdapter(discountAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("test", "ERRRRRORRRR");
                        }
                        Log.e("RESP", "--1");
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RESPERROR", error.toString());
                    }
                }

        );
        requestQueue.add(jsonObjectRequest);


//        swipeRefreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.e("REFRESH", "IT WORKS!");
//            }
//        });

//        Log.e("SWIPEREFRESHLAYOUT", swipeRefreshLayout.toString());

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

}
