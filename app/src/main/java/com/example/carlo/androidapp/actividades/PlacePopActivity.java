package com.example.carlo.androidapp.actividades;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.Place;

import org.w3c.dom.Text;

public class PlacePopActivity extends Activity {

    private TextView name;
    private TextView description;
    private Button placeButton;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_pop);

        place = (Place) getIntent().getExtras().getSerializable("place");

        name = (TextView)findViewById(R.id.namePlace);
        description = (TextView)findViewById(R.id.descriptionPlace);
        placeButton = (Button)findViewById(R.id.botonPlace);

        name.setText(place.getName());
        description.setText(place.getDescription());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.2));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = 350;

        getWindow().setAttributes(params);

        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlacePopActivity.this, PlaceDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("place", place);
                i.putExtras(bundle);
                startActivity(i);
            }
        });


    }
}
