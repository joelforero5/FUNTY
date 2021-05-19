package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 22/05/2017.
 */

public class Usuario {

    public String perfil;
    public String nombre;
    public String rutaFoto;

    public Usuario() {
    }

    public Usuario(String perfil, String nombre, String rutaFoto) {
        this.perfil = perfil;
        this.nombre = nombre;
        this.rutaFoto = rutaFoto;
    }

    public void writeNewUser(DatabaseReference dataRef, String idUser) {
        dataRef.child(idUser).setValue(this);
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

}
