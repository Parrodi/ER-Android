package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlo.androidapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pay_showticket extends AppCompatActivity {
    private static final String TAG = "pay_showticket";
    Intent mintent;
    TextView mdate,thistotal, mtourname;
    Button confirmpurchase;
    ArrayList<String> names;
    ArrayList<Integer> ages;
    ArrayList<String> types;
    ArrayList<Integer> count;
    ArrayList<Integer> priceidlist = new ArrayList<>();
    LinearLayout layouttypes;
    LinearLayout layoutprices;
    private int tourid, userid,datasize;
    private double temptotal;
    private String accesstoken;
    long unixtimestamp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payform4);
        mintent = getIntent();
        mdate = (TextView)findViewById(R.id.tourDate);
        mtourname = (TextView)findViewById(R.id.tourName);
        confirmpurchase = (Button)findViewById(R.id.Continuar);
        confirmpurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPurchase();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        names = mintent.getStringArrayListExtra("Names array");
        ages = mintent.getIntegerArrayListExtra("Ages array");
        types = mintent.getStringArrayListExtra("Types array");
        count = mintent.getIntegerArrayListExtra("Count array");
        layouttypes = (LinearLayout)findViewById(R.id.ticketTypes);
        layoutprices = (LinearLayout)findViewById(R.id.ticketPrices);
        thistotal =(TextView)findViewById(R.id.totalAmount);
        accesstoken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjE0IiwiZW1haWwiOiJjYXJsb3NwYXJyb2RpQGVtYWlsLmNvbSIsInR5cGUiOiJVc2VyIiwiaWF0IjoxNTQyOTIyOTUxfQ.P1yZzKv8fm_OFfkO8JNd3ywM4cKnTaZNWnA7NHf0HEY";
        tourid=mintent.getIntExtra("tour_id",0);
        userid = 14;
        unixtimestamp = mintent.getLongExtra("dateselected",0);
        datasize = mintent.getIntExtra("datasize",0);
        for(int i=0; i<datasize; i++){
            Log.d(TAG,names.get(i));
            Log.d(TAG, ages.get(i) + "");
        }
        Log.d(TAG, "date: " + unixtimestamp);
        Date date = new java.util.Date(unixtimestamp*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(date);
        mdate.setText(formattedDate);

        getPrices();
    }

    private void getPrices(){
        RequestQueue mQueue = Volley.newRequestQueue(this);
        String urlprices = "http://er-citytourister.appspot.com/Price?tour_id="+tourid;
        Log.d(TAG, "getPrices: "+tourid);

        final JsonArrayRequest requestPrices = new JsonArrayRequest(Request.Method.GET, urlprices, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject price = response.getJSONObject(i);
                        JSONObject tickettype = price.getJSONObject("ticket_type_id");
                        String name = tickettype.getString("name");
                        Double mprice = price.getDouble("priceAmount");
                        if(types.contains(name)){
                            createTextviews(name,mprice);
                            priceidlist.add(price.getInt("id"));
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: No se pudo primerjson");
                    }

                }
                try {
                    JSONObject tour = response.getJSONObject(0);
                    JSONObject mtour = tour.getJSONObject("tour_id");
                    String tourname = mtour.getString("name");
                    mtourname.setText(tourname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: failed second json");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("auth", accesstoken);
                return headers;
            }
        };





        mQueue.add(requestPrices);
    }

    private void createTextviews(String name, Double price){
        int mcount = count.get(types.indexOf(name));
        String tickettype = name + " X" + mcount;
        TextView type = new TextView(this);
        TextView cost = new TextView(this);
        price=price*mcount;
        temptotal += price;
        String mprice = price.toString();
        type.setText(tickettype);
        cost.setText(mprice);
        type.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cost.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layouttypes.addView(type);
        layoutprices.addView(cost);
        Log.d(TAG, "createTextviews: "+temptotal);
        thistotal.setText(String.valueOf(temptotal));
    }

    private void createPurchase() throws JSONException {
    String url = "https://er-citytourister.appspot.com/purchase/add";
    JSONObject postparams = new JSONObject();
    postparams.put("user_id", userid);
    postparams.put("company_id",1);
    postparams.put("date_tour",unixtimestamp);

    RequestQueue postqueue = Volley.newRequestQueue(this);

        JsonObjectRequest createMyPurchase = new JsonObjectRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int id = response.getInt("id");
                    Log.d(TAG, "onResponse: SE CREO PRUCHASE!");
                    int counter =0;
                    for(int i=0; i<count.size(); i++){
                        for(int j=0; j<count.get(i); j++){
                            String name = names.get(counter);
                            int priceid = priceidlist.get(i);
                            counter +=1;
                            createTickets(id,name,priceid);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("auth",accesstoken);
                return headers;
            }
        };
        postqueue.add(createMyPurchase);


    }

    private void createTickets(int id, String name, int priceid) throws JSONException {
        Log.d(TAG, "ID de purchase: "+id);
        String url = "https://er-citytourister.appspot.com/ticket/add";
        RequestQueue ticketrequest = Volley.newRequestQueue(this);
        JSONObject postparams = new JSONObject();
        postparams.put("purchase_id",id);
        postparams.put("name", name);
        postparams.put("price_id", priceid);
        postparams.put("qr_code", "12345678910");
        Log.d(TAG, "params" + postparams);


        JsonObjectRequest createTicket = new JsonObjectRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: SELOGROTICKET!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("auth",accesstoken);
                return headers;
            }
        };
        ticketrequest.add(createTicket);
    }
}
