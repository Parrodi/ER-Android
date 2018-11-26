package com.example.carlo.androidapp.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.Purchase;
import com.example.carlo.androidapp.modelos.Tour;

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

        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.botomNavigation);
        menu.setSelectedItemId(R.id.tickets);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tours :
                        Intent in = new Intent(showTicketActivity.this, MapsActivity.class);
                        startActivity(in);
                        break;
                    case R.id.tickets :
                        break;
                    case R.id.mapa :
                        Intent i = new Intent(showTicketActivity.this, UserMapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("tour", (Tour) getIntent().getExtras().getSerializable("tour"));
                        i.putExtras(bundle);
                        startActivity(i);
                        break;
                    case R.id.salidas :
                        Intent intent = new Intent(showTicketActivity.this, TimeIntervaleActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("tour", (Tour) getIntent().getExtras().getSerializable("tour"));
                        intent.putExtras(bundle2);
                        startActivity(intent);
                        break;
                    case R.id.menus :
                        Intent mIntent = new Intent(showTicketActivity.this, OptionsMenuActivity.class);
                        Bundle bundle3 = new Bundle();
                        bundle3.putSerializable("tour", (Tour) getIntent().getExtras().getSerializable("tour"));
                        mIntent.putExtras(bundle3);
                        startActivity(mIntent);
                        break;
                }
                return false;
            }
        });

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
