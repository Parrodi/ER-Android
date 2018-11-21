package com.example.carlo.androidapp.actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.adapters.ViewPagerAdapter;
import com.example.carlo.androidapp.modelos.Place;
import com.example.carlo.androidapp.modelos.Tour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ViewPager.OnPageChangeListener {

    public static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final float DEFAULT_ZOOM = 13f;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    //Request
    private RequestQueue mRequestQueue;
    String url = "https://ertourister.appspot.com/";
    String PATH_TO_LOCATION = "location";
    String PATH_TO_TOURS = "tour";
    String PATH_TO_PLACE_TYPE =  "placetype";
    String PATH_TO_PLACES =  "places";
    private List<Tour> listaDeTours;
    private List<Place> listaDePlaces;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        listaDeTours = new ArrayList<>();
        listaDePlaces = new ArrayList<>();
        getLocationPermission();
        Button botonPulsera = (Button)findViewById(R.id.buttonPulsera);

        botonPulsera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, UserMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tour", listaDeTours.get(0));
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sendRequest();
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
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
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

        mapFragment.getMapAsync(MapsActivity.this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

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
        }
    }

    private void sendRequest(){
        mRequestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest requestLocation = new JsonArrayRequest(url + PATH_TO_TOURS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++){

                    try {
                        jsonObject = response.getJSONObject(i);
                        String url = jsonObject.getString("image");

                        JSONArray jsonArrayPlace = jsonObject.getJSONArray("places");
                        for(int j = 0; j < jsonArrayPlace.length(); j++){
                            JSONObject jsonObjectPlaces = jsonArrayPlace.getJSONObject(j);

                            String name = jsonObjectPlaces.getString("name");
                            String description = jsonObjectPlaces.getString("description");
                            int placeType = jsonObjectPlaces.getInt("place_type_id");
                            double latitude = jsonObjectPlaces.getDouble("latitude");
                            double longitude = jsonObjectPlaces.getDouble("longitude");

                            Place lugar = new Place(name, description, placeType, latitude, longitude);
                            listaDePlaces.add(lugar);

                        }

                        Place lugares[] = new Place[listaDePlaces.size()];
                        lugares = listaDePlaces.toArray(lugares);

                        listaDeTours.add(new Tour(jsonObject.getInt("id"), jsonObject.getString("name"),
                                new URL(url), jsonObject.getString("description"), lugares));

                    } catch (JSONException e) {

                        Log.d(TAG, " Valio varriga :V (1) " + e);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                setupViewPager(listaDeTours);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, " Valio varriga :V (2)" + error);
            }
        });
        mRequestQueue.add(requestLocation);
    }

    private void setupViewPager(List<Tour> listaDeTours) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MapsActivity.this, listaDeTours);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Tour tour = listaDeTours.get(i);
        Place place [] = tour.getPlaces();

        for(Place p : place){
            makeMarker(new LatLng(p.getLatitude(),p.getLongitude()), tour.getDescription(), p.getPlaceTypeId());
        }
    }

    private void makeMarker(LatLng latLng, String description, int placeTypeId) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(description));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}

