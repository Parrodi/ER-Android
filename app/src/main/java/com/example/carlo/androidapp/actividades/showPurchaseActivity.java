package com.example.carlo.androidapp.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.adapters.RecyclerViewAdapterPurchase;
import com.example.carlo.androidapp.modelos.Purchase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class showPurchaseActivity extends AppCompatActivity {
    private ArrayList<Purchase> purchases = new ArrayList<>();
    private int userid;
    private String accesstoken;
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpurchase);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        userid =pref.getInt("userid",0);
        accesstoken = pref.getString("token",null);
        getPurchases();


    }

    private void getPurchases(){
        RequestQueue mqueue = Volley.newRequestQueue(this);
        String url = "https://er-citytourister.appspot.com/Purchase?user_id=" + userid;

        final JsonArrayRequest requestPurchases = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject purchase = response.getJSONObject(i);
                        int id = purchase.getInt("id");
                        long unixtime = purchase.getLong("date_tour");
                        Purchase actualpurchase = new Purchase(id,unixtime);
                        actualpurchase.setTotal(purchase.getDouble("total"));
                        JSONArray tickets = purchase.getJSONArray("tickets");
                        JSONObject firstticket = tickets.getJSONObject(0);
                        int priceid = firstticket.getInt("price_id");
                        for (int j=0;j<tickets.length();j++){
                            JSONObject ticket = tickets.getJSONObject(j);
                            actualpurchase.addCountToMap(ticket.getString("name"));
                        }
                        purchases.add(actualpurchase);
                        getPurchaseTourname(i,priceid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("auth", accesstoken);
                return headers;
            }
        };
        mqueue.add(requestPurchases);

    }

    private void getPurchaseTourname(final int index, int priceid){
        RequestQueue mqueue = Volley.newRequestQueue(this);
        String url = "https://er-citytourister.appspot.com/price?id=" + priceid;
        final JsonArrayRequest requestPrices = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject price = response.getJSONObject(0);
                    JSONObject tour = price.getJSONObject("tour_id");
                    String name = tour.getString("name");
                    purchases.get(index).setTourname(name);
                    if(index == purchases.size()-1)
                        initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("auth", accesstoken);
                return headers;
            }
        };
        mqueue.add(requestPrices);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerviewpurchases);
        final RecyclerViewAdapterPurchase adapter = new RecyclerViewAdapterPurchase(purchases,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
