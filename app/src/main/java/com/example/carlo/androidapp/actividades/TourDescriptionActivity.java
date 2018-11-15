package com.example.carlo.androidapp.actividades;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.squareup.picasso.Picasso;

public class TourDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_description);

        String name = getIntent().getExtras().getString("tour_name");
        String description = getIntent().getExtras().getString("tour_description");
        String image_url = getIntent().getExtras().getString("tour_image");

        ImageView image_tour = findViewById(R.id.foto_de_tour);
        TextView descripcion_tour = findViewById(R.id.tour_description);
        TextView nombre_tour = findViewById(R.id.nombre_tour);

        descripcion_tour.setText(description);
        nombre_tour.setText(name);
        Picasso.with(this).load(image_url).into(image_tour);
    }
}