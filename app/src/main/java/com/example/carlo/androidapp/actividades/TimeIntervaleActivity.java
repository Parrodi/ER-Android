package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.DateInformation;
import com.example.carlo.androidapp.modelos.DateInterval;
import com.example.carlo.androidapp.modelos.Tour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeIntervaleActivity extends AppCompatActivity {

    public static final String TAG = "TimeIntervalActivity";

    private static String accesstoken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjE5IiwiZW1haWwiOiJkQGdtYWkuY29tYyIsInR5cGUiOiJVc2VyIiwiaWF0IjoxNTQyOTIzNDYzfQ.L8bgLtBeJx3EtdZYhLq16obFxRnqtLfrJ8T0WyqtNWc";

    private RequestQueue mRequestQueue;
    String url = "https://er-citytourister.appspot.com/";
    String PATH_TO_DATE_INTERVAL = "dateinterval";
    String PATH_TO_HOUR_INTERVAL = "hourinterval";

    Tour tour;

    DateInformation dates[];
    SharedPreferences prf;

    List<DateInterval> dateIntervals;
    List<DateInformation> dateInformations;

    TextView fecha;
    LinearLayout layoutHorarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_intervale);

        dateIntervals = new ArrayList<>();
        dateInformations = new ArrayList<>();

        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        accesstoken = prf.getString("token", null);

        tour = (Tour) getIntent().getExtras().getSerializable("tour");


        dates = tour.getDateInformations();

        layoutHorarios = (LinearLayout)findViewById(R.id.layout);

        fecha = (TextView)findViewById(R.id.fecha);

        sendRequest(dates);

        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.botomNavigation);
        menu.setSelectedItemId(R.id.salidas);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tours :
                        Intent i = new Intent(TimeIntervaleActivity.this, MapsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.tickets :
                        break;
                    case R.id.mapa :
                        Intent in = new Intent(TimeIntervaleActivity.this, UserMapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("tour", tour);
                        in.putExtras(bundle);
                        startActivity(in);
                        break;
                    case R.id.salidas :
                        break;
                    case R.id.menus :
                        Intent intent = new Intent(TimeIntervaleActivity.this, OptionsMenuActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
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

                            if (jsonObject.getInt("id") == aDate.getDateId()) {
                                String startDate = jsonObject.getString("start_date");
                                String endDate = jsonObject.getString("end_date");
                                boolean service = jsonObject.getBoolean("service");
                                int id = jsonObject.getInt("id");

                                DateInterval di = new DateInterval(Long.valueOf(startDate), Long.valueOf(endDate), service);
                                di.setId(id);

                                if (checkDay(di.getStartDate(), di.getEndDate())) {
                                    @SuppressLint("SimpleDateFormat")SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    String todayDate;
                                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
                                    int year = Calendar.getInstance().get(Calendar.YEAR);
                                    todayDate = day+"/"+month+"/"+year;
                                    fecha.setText(todayDate);
                                    dateInformations.add(aDate);
                                }

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
                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        for(DateInformation di: dateInformations) {
                            if (jsonObject.getInt("id") == di.getHourId()) {
                                long startTime = jsonObject.getLong("start_time");
                                long endTime = jsonObject.getLong("end_time");
                                int freq = jsonObject.getInt("frequency");


                                Date start = new java.util.Date(startTime*1000L);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm");
                                String formattedDateStart = sdf.format(start);

                                Date end = new java.util.Date(endTime*1000L);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("hh:mm");
                                String formattedDateEnd = sdf2.format(end);

                                String horario = formattedDateStart + "-" + formattedDateEnd + "                         " + freq;
                                TextView lineaDeHorario = new TextView(TimeIntervaleActivity.this);
                                lineaDeHorario.setText(horario);
                                lineaDeHorario.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                layoutHorarios.addView(lineaDeHorario);

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