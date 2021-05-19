package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 29/05/2017.
 */

public class SitioFavorito {

    public String nombreSitio;
    public String tipo;
    public String nombreFoto;

    public SitioFavorito() {
    }

    public void writeNewSitioFavorito(DatabaseReference dataRef) {
        dataRef.setValue(this);
    }

    public SitioFavorito(String nombreSitio, String tipo, String nombreFoto) {
        this.nombreSitio = nombreSitio;
        this.tipo = tipo;
        this.nombreFoto = nombreFoto;
    }

    public String getNombreSitio() {
        return nombreSitio;
    }

    public void setNombreSitio(String nombreSitio) {
        this.nombreSitio = nombreSitio;
    }

    public String gettipo() {
        return tipo;
    }

    public void settipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreFoto() {
        return nombreFoto;
    }

    public void setNombreFoto(String nombreFoto) {
        this.nombreFoto = nombreFoto;
    }
}