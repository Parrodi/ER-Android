package com.example.carlo.androidapp.actividades;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.botomNavigation);

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tours :
                        break;
                    case R.id.tickets :
                        break;
                    case R.id.mapa :
                        break;
                    case R.id.salidas :
                        break;
                    case R.id.menus :
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
