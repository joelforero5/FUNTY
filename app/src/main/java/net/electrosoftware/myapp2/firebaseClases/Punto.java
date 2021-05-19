package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 23/05/2017.
 */

public class Punto {

    public double lat;
    public double lng;

    public Punto() {
    }

    public void writePunto(DatabaseReference dataRef, String key) {
        dataRef.child(key).setValue(this);
    }

    public Punto(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
