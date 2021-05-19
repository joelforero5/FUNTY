package net.electrosoftware.myapp2.firebaseClases;

/**
 * Created by CARLOS MAESTRE on 13/03/2017.
 */

public class GeoPunto {

    Double lat;
    Double lng;

    public GeoPunto(){

    }

    public GeoPunto(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
