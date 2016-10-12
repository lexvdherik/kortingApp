package hva.flashdiscount.service;

import android.content.Context;
import android.location.Location;
import android.util.Log;
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

import hva.flashdiscount.fragment.LineupFragment;
import hva.flashdiscount.model.Company;
import hva.flashdiscount.model.Discount;
import hva.flashdiscount.model.Establishment;

/**
 * Created by Maiko on 12-10-2016.
 */

public class EstablishmentService {

    public ArrayList<Establishment> establishments;
    private final String server_url = "http://145.28.191.18/api/establishment/getall";
    private RequestQueue requestQueue;
    private LineupFragment.OnListDataListener listDataCallback;
    private Context context;


    public EstablishmentService(LineupFragment.OnListDataListener listDataCallback,Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.listDataCallback = listDataCallback;
        this.context = context;
    }

    public void getAllEstablishments() {

        establishments = new ArrayList<>();
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
                                ArrayList<Discount> discounts = new ArrayList<>();
                                for (int j = 0; j < ds.length(); j++) {

                                    JSONObject dsObj = ds.getJSONObject(j);


                                    Discount d = new Discount(dsObj.getString("description"), dsObj.getString("endTime"), company);

                                    discounts.add(d);
                                }

                                establishments.add(new Establishment(company, discounts));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            listDataCallback = (LineupFragment.OnListDataListener) context;
                            listDataCallback.onListDataChange();
                        } catch (ClassCastException e) {
                            throw new ClassCastException(context.toString()
                                    + " must implement OnListDataListener");
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


