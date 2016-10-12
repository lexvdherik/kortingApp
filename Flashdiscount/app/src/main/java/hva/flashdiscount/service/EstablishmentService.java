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

import hva.flashdiscount.fragment.LineupFragment;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

/**
 * Created by Maiko on 12-10-2016.
 */

public class EstablishmentService extends APIService {

    public ArrayList<Establishment> establishments;
    private RequestQueue requestQueue;
    private LineupFragment.OnListDataListener listDataCallback;
    private Context context;


    public EstablishmentService(LineupFragment.OnListDataListener listDataCallback, Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        getAllEstablishments();
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
                                listDataCallback.onListDataChange();
                            } catch (Exception e) {
                                Log.e("FlashDiscount", "Forgot to notify");
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
}


