package com.example.carlo.androidapp.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.Place;
import com.example.carlo.androidapp.modelos.Tour;

import java.util.Objects;

public class PlaceDescriptionActivity extends AppCompatActivity {

    Place place;
    Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_description);

        place = (Place) getIntent().getExtras().getSerializable("place");
        tour = (Tour)Objects.requireNonNull(getIntent().getExtras()).getSerializable("tour");

        TextView placeName = (TextView)findViewById(R.id.namePlace);
        TextView placeDescription = (TextView)findViewById(R.id.placeDescription);

        placeName.setText(place.getName());
        placeDescription.setText(place.getDescription());

        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.botomNavigation);
        menu.setSelectedItemId(R.id.mapa);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tours :
                        Intent i = new Intent(PlaceDescriptionActivity.this, MapsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.tickets :
                        Intent mIntent = new Intent(PlaceDescriptionActivity.this, showPurchaseActivity.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("tour", tour);
                        mIntent.putExtras(bundle2);
                        startActivity(mIntent);
                        break;
                    case R.id.mapa :
                        finish();
                        break;
                    case R.id.salidas :
                        Intent in = new Intent(PlaceDescriptionActivity.this, TimeIntervaleActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putSerializable("tour", tour);
                        in.putExtras(bundle);
                        startActivity(in);
                        break;
                    case R.id.menus :
                        Intent intent = new Intent(PlaceDescriptionActivity.this, OptionsMenuActivity.class);
                        Bundle bundle3 = new Bundle();
                        bundle3.putSerializable("tour", tour);
                        intent.putExtras(bundle3);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        /*ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this, place.getImagesOfPlaces());
        viewPager.setAdapter(adapter);*/

    }
}
