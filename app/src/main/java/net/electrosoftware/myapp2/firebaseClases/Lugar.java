package net.electrosoftware.myapp2.firebaseClases;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 13/03/2017.
 */

public class Lugar {

    public String nombre;
    public String telefono;
    public String horario;
    public String tipo;
    public String descripcion;
    public String rutaFoto;
    public String direccion;
    public double lat;
    public double lng;
    public double calificacion;

    public Lugar() {
    }

    public String writeNewEvento(DatabaseReference dataRef) {
        String key = dataRef.push().getKey();
        dataRef.child(key).setValue(this);
        return key;
    }

    public Lugar(String nombre, String telefono, String horario, String tipo, String descripcion, String rutaFoto, String direccion, double lat, double lng, double calificacion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.horario = horario;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.rutaFoto = rutaFoto;
        this.direccion = direccion;
        this.lat = lat;
        this.lng = lng;
        this.calificacion = calificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }
}
