package net.electrosoftware.myapp2.firebaseClases;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by CARLOS MAESTRE on 29/05/2017.
 */

public class Comentario {

    public String nombreUser;
    public String fecha;
    public String textComentario;
    public String fotoUser;

    public Comentario() {
    }
    public void writeNewComentario(DatabaseReference dataRef) {
        String key = dataRef.push().getKey();
        dataRef.child(key).setValue(this);
    }


    public Comentario(String nombreUser, String fecha, String textComentario, String fotoUser) {
        this.nombreUser = nombreUser;
        this.fecha = fecha;
        this.textComentario = textComentario;
        this.fotoUser = fotoUser;
    }

    public String getNombreUser() {
        return nombreUser;
    }

    public void setNombreUser(String nombreUser) {
        this.nombreUser = nombreUser;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTextComentario() {
        return textComentario;
    }

    public void setTextComentario(String textComentario) {
        this.textComentario = textComentario;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }
}
