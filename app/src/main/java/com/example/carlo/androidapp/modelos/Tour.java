package com.example.carlo.androidapp.modelos;

import java.net.URL;

public class Tour {

    private int id;
    private String name;
    private URL image;
    private String description;
    private Place[] places;

    public Tour() {

    }

    public Tour(int id, String name, URL image, String description/*, Place[] places*/) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        //this.places = places;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place[] getPlaces() {
        return places;
    }

    public void setPlaces(Place[] places) {
        this.places = places;
    }
}
