package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.carlo.androidapp.modelos.DateInformation;
import com.example.carlo.androidapp.modelos.DateInterval;
import com.example.carlo.androidapp.modelos.HourInterval;
import com.example.carlo.androidapp.modelos.Place;
import com.example.carlo.androidapp.modelos.Tour;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TimeIntervaleActivity extends AppCompatActivity {

    public static final String TAG = "TimeIntervalActivity";

    private static final String accesstoken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjE5IiwiZW1haWwiOiJkQGdtYWkuY29tYyIsInR5cGUiOiJVc2VyIiwiaWF0IjoxNTQyOTIzNDYzfQ.L8bgLtBeJx3EtdZYhLq16obFxRnqtLfrJ8T0WyqtNWc";

    private RequestQueue mRequestQueue;
    String url = "https://er-citytourister.appspot.com/";
    String PATH_TO_DATE_INTERVAL = "dateinterval";
    String PATH_TO_HOUR_INTERVAL = "hourinterval";

    Tour tour;
    int dateId;
    int hourId;

    DateInformation dates[];

    List<DateInterval> dateIntervals;
    List<DateInformation> dateInformations;
    List<HourInterval> hourIntervals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_intervale);

        dateIntervals = new ArrayList<>();
        dateInformations = new ArrayList<>();
        hourIntervals = new ArrayList<>();

        tour = (Tour) getIntent().getExtras().getSerializable("tour");

        dates = tour.getDateInformations();

        Date currentTime = Calendar.getInstance().getTime();

        sendRequest(dates);

        TextView fecha = (TextView)findViewById(R.id.fecha);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (HourInterval hi : hourIntervals){
                    Log.d(TAG, hi.getStartTime() + " " + hi.getEndTime() + " " + hi.getFrequency());
                }
            }
        });

    }

    private void sendRequest(final DateInformation[] date) {

            mRequestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest( url + PATH_TO_DATE_INTERVAL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        for (DateInformation aDate : date) {
                            Log.d(TAG, jsonObject.getInt("id") + "  " + aDate.getDateId());
                            if (jsonObject.getInt("id") == aDate.getDateId()) {
                                String startDate = jsonObject.getString("start_date");
                                String endDate = jsonObject.getString("end_date");
                                boolean service = jsonObject.getBoolean("service");
                                int id = jsonObject.getInt("id");

                                DateInterval di = new DateInterval(Long.valueOf(startDate), Long.valueOf(endDate), service);
                                di.setId(id);

                                if (checkDay(di.getStartDate(), di.getEndDate())) {
                                    dateInformations.add(aDate);
                                } else Log.d(TAG, "Tambien se pudo pero diferente");

                            }
                        }

                    } catch (JSONException e) {

                        Log.d(TAG, " Valio varriga :V (1) " + e);

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, " Valio varriga :V (2)" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("auth", accesstoken);
                return headers;
            }
        };

        mRequestQueue.add(request);
        sendNextRequest();
    }

    private void sendNextRequest(){
        Log.d(TAG, "What");
        mRequestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest( url + PATH_TO_HOUR_INTERVAL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "K chingadoz prro :V          " + response.length() + "   " + dateInformations.size());
                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        for(DateInformation di: dateInformations) {
                            Log.d(TAG, "MIRAMEEEEE: " + jsonObject.getInt("id") + "  " + di.getHourId());
                            if (jsonObject.getInt("id") == di.getHourId()) {
                                Log.d(TAG, "Si se pudo maldita piruja");
                                long startTime = jsonObject.getLong("start_time");
                                long endTime = jsonObject.getLong("end_time");
                                int freq = jsonObject.getInt("frequency");
                                HourInterval hi = new HourInterval(startTime, endTime, freq);
                                hourIntervals.add(hi);
                            }
                        }

                    } catch (JSONException e) {

                        Log.d(TAG, " Valio varriga :V (1) " + e);

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, " Valio varriga :V (2)" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("auth", accesstoken);
                return headers;
            }
        };

        mRequestQueue.add(request);
    }

    private boolean checkDay(long starDate, long endDate){
        long epoch = System.currentTimeMillis()/1000;
        return epoch > starDate && epoch < endDate;
    }

}