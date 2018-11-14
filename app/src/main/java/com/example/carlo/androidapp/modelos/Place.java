package com.example.carlo.androidapp.modelos;

import java.net.URL;

public class Place {

    private String name;
    private String description;
    private int locationId;
    private int placeTypeId;
    private double latitude;
    private double longitude;
    private String narrativeUrl;
    private Tour[] tours;

    public Place(String name, String description, int locationId, int placeTypeId) {
        this.name = name;
        this.description = description;
        this.locationId = locationId;
        this.placeTypeId = placeTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getPlaceTypeId() {
        return placeTypeId;
    }

    public void setPlaceTypeId(int placeTypeId) {
        this.placeTypeId = placeTypeId;
    }
}
