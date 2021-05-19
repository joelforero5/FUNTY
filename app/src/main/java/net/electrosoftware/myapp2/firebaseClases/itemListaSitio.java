package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 23/05/2017.
 */

public class itemListaSitio {

    public String nombre;
    public String tipo;
    public String foto;

    public itemListaSitio() {
    }

    public void writeItemListaSitio(DatabaseReference dataRef){
        dataRef.setValue(this);
    }

    public itemListaSitio(String nombre, String tipo, String foto) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
