package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
    TextView mdate,thistotal;
    ArrayList<String> names;
    ArrayList<Integer> ages;
    ArrayList<String> types;
    ArrayList<Integer> count;
    LinearLayout layouttypes;
    LinearLayout layoutprices;
    private int tourid;
    private double temptotal;
    private String accesstoken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payform4);
        mintent = getIntent();
        mdate = (TextView)findViewById(R.id.tourDate);
        names = mintent.getStringArrayListExtra("Names array");
        ages = mintent.getIntegerArrayListExtra("Ages array");
        types = mintent.getStringArrayListExtra("Types array");
        count = mintent.getIntegerArrayListExtra("Count array");
        layouttypes = (LinearLayout)findViewById(R.id.ticketTypes);
        layoutprices = (LinearLayout)findViewById(R.id.ticketPrices);
        thistotal =(TextView)findViewById(R.id.totalAmount);
        accesstoken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNhcmxvc3BhcnJvZGlAZW1haWwuY29tIiwiaWF0IjoxNTQyODM2MTM1fQ.8eLCfpGucCmbxJUKdRmiFwjEFsWvNks5ySrkXiO7fjk";
        tourid=1;
        temptotal=0;
        long unixtimestamp = mintent.getLongExtra("dateselected",0);
        int datasize = mintent.getIntExtra("datasize",0);
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
        String url = "http://er-citytourister.appspot.com/Price?tour_id="+tourid;

        final JsonArrayRequest requestPrices = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject price = response.getJSONObject(i);
                        JSONObject tickettype = price.getJSONObject("ticket_type_id");
                        String name = tickettype.getString("name");
                        Double mprice = price.getDouble("priceAmount");
                        if(types.contains(name))
                            createTextviews(name,mprice);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }
        };
        mQueue.add(requestPrices);
    }

    private void createTextviews(String name, Double price){
        Log.d(TAG, "createTextviews: entro alcreateTextviews");
        int mcount = count.get(types.indexOf(name));
        String tickettype = name + "X" + mcount;
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
        Log.d(TAG, "createTextviews: acabo");
        thistotal.setText(String.valueOf(temptotal));
    }
}
