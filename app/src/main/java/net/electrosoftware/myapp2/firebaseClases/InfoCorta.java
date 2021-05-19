package net.electrosoftware.myapp2.firebaseClases;

/**
 * Created by CARLOS MAESTRE on 15/03/2017.
 */

public class InfoCorta {

    String nombre;
    String direccion;
    Float calificacion;
    String foto_presentacion;

    public InfoCorta(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public String getFoto_presentacion() {
        return foto_presentacion;
    }

    public void setFoto_presentacion(String foto_presentacion) {
        this.foto_presentacion = foto_presentacion;
    }
}
