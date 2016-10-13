package hva.flashdiscount.service;

import android.content.Context;
import android.util.Log;

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

import hva.flashdiscount.fragment.DiscountListFragment;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

/**
 * Created by Maiko on 12-10-2016.
 */

public class EstablishmentService extends APIService {

    public ArrayList<Establishment> establishments;
    private RequestQueue requestQueue;
    private Context context;
    OnListDataListener listDataCallback;
    DiscountListFragment frg;

    public EstablishmentService(Context context, DiscountListFragment frg) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        this.frg = frg;
        getAllEstablishments();
        Log.e("context", context.toString());
    }

    public void getAllEstablishments() {

        establishments = new ArrayList<>();
        String url = baseUrl + "establishment" + listUri;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
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
                                ArrayList<Discount> discounts = new ArrayList<>();
                                for (int j = 0; j < ds.length(); j++) {

                                    JSONObject dsObj = ds.getJSONObject(j);


                                    Discount d = new Discount(dsObj.getString("description"), dsObj.getString("endTime"), company);

                                    discounts.add(d);
                                }
                                establishments.add(new Establishment(company, discounts));
                            }
                            try {
                                listDataCallback = (OnListDataListener) context;
                                listDataCallback.onListDataChange(frg);
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
                Log.e("RESPERRORR", error.toString());
            }
        }

        );
        requestQueue.add(jsonObjectRequest);
    }

    public ArrayList<Establishment> getEstablishments() {
        return establishments;
    }

    public interface OnListDataListener {
        void onListDataChange(DiscountListFragment frg);
    }
}


