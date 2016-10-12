package hva.flashdiscount.fragment;


import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        context = getActivity();
        super.onActivityCreated(savedInstanceState);
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

                                Company company = new Company(Integer.valueOf(c.getString("categoryId")), c.getString("name"));
                                Establishment establishment = new Establishment(company);
                                Discount d = new Discount(discount.getString("description"), establishment, discount.getString("endTime"));


                                discounts.add(d);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("test", "ERRRRRORRRR");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );
        Log.e("VOLey", "ERROR");
        requestQueue.add(jsonObjectRequest);
        discountAdapter = new DiscountAdapter(getActivity(), discounts);
        setListAdapter(discountAdapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


}
