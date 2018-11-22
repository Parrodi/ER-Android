package com.example.carlo.androidapp.actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.modelos.Place;
import com.example.carlo.androidapp.modelos.Tour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String TAG = "UserMapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final float DEFAULT_ZOOM = 13f;

    //vars
    private Boolean mLocationPermissionGranted = true;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        getLocationPermission();

        tour = (Tour)Objects.requireNonNull(getIntent().getExtras()).getSerializable("tour");

        final Button puntosDeInteres = (Button)findViewById(R.id.buttonPI);
        final Button parada = (Button)findViewById(R.id.buttonParada);
        final Button recomendacion = (Button) findViewById(R.id.buttonRec);

        puntosDeInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                puntosDeInteres.setBackground(getDrawable(R.drawable.presspuntointeresbtn));
                parada.setBackground(getDrawable(R.drawable.paradasbtn));
                recomendacion.setBackground(getDrawable(R.drawable.recomendacionesbtn));
                for(Place p : tour.getPlaces()){
                    if(p.getPlaceTypeId() == 1) {
                        LatLng location = new LatLng(p.getLatitude(), p.getLongitude());
                        String name = p.getName();
                        mMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpuntodeinteres)));
                    }
                }

            }
        });

        parada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                parada.setBackground(getDrawable(R.drawable.presparadabtn));
                puntosDeInteres.setBackground(getDrawable(R.drawable.puntodeinteresbtn));
                recomendacion.setBackground(getDrawable(R.drawable.recomendacionesbtn));
                for(Place p : tour.getPlaces()){
                    if(p.getPlaceTypeId() == 2) {
                        LatLng location = new LatLng(p.getLatitude(), p.getLongitude());
                        String name = p.getName();
                        mMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinparada)));
                    }
                }
            }
        });

        recomendacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                recomendacion.setBackground(getDrawable(R.drawable.presrecomendacionbtn));
                parada.setBackground(getDrawable(R.drawable.paradasbtn));
                puntosDeInteres.setBackground(getDrawable(R.drawable.puntodeinteresbtn));
                for(Place p : tour.getPlaces()){
                    if(p.getPlaceTypeId() == 3) {
                        LatLng location = new LatLng(p.getLatitude(), p.getLongitude());
                        String name = p.getName();
                        mMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinrecomendacion)));
                    }
                }
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setOnMarkerClickListener(this);
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(19.0429, -98.198508), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(UserMapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(UserMapActivity.this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerLocation = marker.getPosition();
        for(Place p : tour.getPlaces()){
            LatLng placeLocation = new LatLng(p.getLatitude(), p.getLongitude());
            if(markerLocation.equals(placeLocation)) {
                Intent i = new Intent(UserMapActivity.this, PlacePopActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("place", p);
                i.putExtras(bundle);
                startActivity(i);
            }
        }
        return false;
    }
}