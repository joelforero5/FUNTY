package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 23/05/2017.
 */

public class Evento {

    public String nombre;
    public String telefono;
    public String fechaIni;
    public String fechaFin;
    public String horaIni;
    public String horaFin;
    public String patrocinador;
    public String tipo;
    public String descripcion;
    public String rutaFoto;
    public String direccion;
    public int asistentes;
    public double lat;
    public double lng;
    //public Bitmap foto;

    public Evento() {
    }

    public String writeNewEvento(DatabaseReference dataRef) {
        String key = dataRef.push().getKey();
        dataRef.child(key).setValue(this);
        return key;
    }

    public Evento(String nombre, String telefono, String fechaIni, String fechaFin, String horaIni, String horaFin, String patrocinador, String tipo, String descripcion, String rutaFoto, String direccion, int asistentes, double lat, double lng) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.horaIni = horaIni;
        this.horaFin = horaFin;
        this.patrocinador = patrocinador;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.rutaFoto = rutaFoto;
        this.direccion = direccion;
        this.asistentes = asistentes;
        this.lat = lat;
        this.lng = lng;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(int asistentes) {
        this.asistentes = asistentes;
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

    /*public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }*/

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
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

    public String getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(String fechaIni) {
        this.fechaIni = fechaIni;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(String horaIni) {
        this.horaIni = horaIni;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getPatrocinador() {
        return patrocinador;
    }

    public void setPatrocinador(String patrocinador) {
        this.patrocinador = patrocinador;
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
}
