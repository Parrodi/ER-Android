package com.example.carlo.androidapp.actividades;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        final int id = getIntent().getExtras().getInt("tour_id");

        ImageView image_tour = (ImageView) findViewById(R.id.foto_de_tour);
        TextView descripcion_tour = (TextView) findViewById(R.id.tour_description);
        TextView nombre_tour = (TextView) findViewById(R.id.nombre_tour);
        Button botonCompra = (Button)findViewById(R.id.botonCompra);

        descripcion_tour.setText(description);
        nombre_tour.setText(name);
        Picasso.with(this).load(image_url).into(image_tour);

        botonCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TourDescriptionActivity.this,pay_getdate.class);
                i.putExtra("tour_id", id);
                startActivity(i);
            }
        });
    }
}