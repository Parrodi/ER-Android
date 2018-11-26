package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.Purchase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class showTicketActivity extends AppCompatActivity {
    private static final String TAG = "showTicketActivity";
    private LinearLayout passengers;
    private HashMap<String, Integer> typecount;
    private Purchase purchase;
    private TextView tour,mdate,total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showticket);
        purchase = (Purchase) getIntent().getExtras().getSerializable("purchase");
        typecount = purchase.getTypecount();
        tour = (TextView)findViewById(R.id.nombreTour);
        mdate = (TextView)findViewById(R.id.textoDeFecha);
        total = (TextView)findViewById(R.id.Pagado);
        passengers = (LinearLayout)findViewById(R.id.listaPasajeros);
        Log.d(TAG, "onCreate: ENTRO!!!!!");
        setTextViews();

    }

    private void setTextViews(){
        Log.d(TAG, "setTextViews: HOLAAAAAAAAAAAA");
        tour.setText(purchase.getTourname());
        long unixtimestamp = purchase.getUnixdate();
        Log.d(TAG, "setTextViews: ANTES DE DATE");
        Date date = new java.util.Date(unixtimestamp*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(date);
        Log.d(TAG, "setTextViews: ANTES DE SET TEXT");
        mdate.setText(formattedDate);
        Log.d(TAG, "setTextViews: DESPUES DE SET TEXT! "+purchase.getTotal());
        double mtotal = purchase.getTotal();
        total.setText(String.valueOf(mtotal));
        Log.d(TAG, "setTextViews: CACAAAAAAAAA");
        for(String key : typecount.keySet()){
            int count = typecount.get(key);
            TextView passenger = new TextView(this);
            String mpassenger = key+" X" + count;
            passenger.setText(mpassenger);
            passenger.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            passenger.setTextColor(Color.parseColor("#043764"));
            passengers.addView(passenger);

        }
        Log.d(TAG, "setTextViews: ADIOOOOOOOOOOS");


    }
}
