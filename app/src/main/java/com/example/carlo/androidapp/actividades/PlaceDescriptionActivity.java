package com.example.carlo.androidapp.actividades;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.adapters.ImageAdapter;
import com.example.carlo.androidapp.modelos.Place;

public class PlaceDescriptionActivity extends AppCompatActivity {

    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_description);

        place = (Place) getIntent().getExtras().getSerializable("place");

        TextView placeName = (TextView)findViewById(R.id.namePlace);
        TextView placeDescription = (TextView)findViewById(R.id.placeDescription);

        placeName.setText(place.getName());
        placeDescription.setText(place.getDescription());

        /*ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this, place.getImagesOfPlaces());
        viewPager.setAdapter(adapter);*/

    }
}
