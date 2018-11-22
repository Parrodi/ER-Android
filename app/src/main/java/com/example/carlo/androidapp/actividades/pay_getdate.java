package com.example.carlo.androidapp.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.carlo.androidapp.R;

import java.util.Calendar;

public class pay_getdate extends AppCompatActivity {
    private CalendarView mCalendar;
    private TextView txt;
    private long date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payform1);
        mCalendar = (CalendarView)findViewById(R.id.MyCalendar);
        txt = (TextView)findViewById(R.id.textEscribeEmail);
        mCalendar.setMinDate(mCalendar.getDate());
        date = mCalendar.getDate()/1000;
        final Calendar c = Calendar.getInstance();
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                date = c.getTimeInMillis()/1000;

            }
        });
    }

    public void dateSelected(View V){
        Intent i = new Intent(pay_getdate.this, pay_gettickets.class);
        i.putExtra("dateselected",date);
        startActivity(i);


    }

}
