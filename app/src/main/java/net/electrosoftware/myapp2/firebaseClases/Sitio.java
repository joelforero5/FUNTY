package net.electrosoftware.myapp2.firebaseClases;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by CARLOS MAESTRE on 24/05/2017.
 */

public class Sitio {

    String key;
    GeoPunto geoPunto;
    Marker marker;

    public Sitio(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoPunto getGeoPunto() {
        return geoPunto;
    }

    public void setGeoPunto(GeoPunto geoPunto) {
        this.geoPunto = geoPunto;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
