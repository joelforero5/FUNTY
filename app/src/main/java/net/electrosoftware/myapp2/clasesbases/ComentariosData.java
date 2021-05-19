package net.electrosoftware.myapp2.clasesbases;

import android.graphics.Bitmap;

/**
 * Created by Jonathan on 13/05/2017.
 */

public class ComentariosData {

    Bitmap fotoUsuario;
    String nombreUsuario, fechaComentario, comentarioTexto, calificacionComentario;

    public ComentariosData(Bitmap fotousuario, String nombreusuario, String fechacomentario, String comentariotexto, String calificacioncomentario) {
        this.fotoUsuario = fotousuario;
        this.nombreUsuario = nombreusuario;
        this.fechaComentario = fechacomentario;
        this.comentarioTexto = comentariotexto;
        this.calificacionComentario = calificacioncomentario;
    }

    public Bitmap getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(Bitmap fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public String getComentarioTexto() {
        return comentarioTexto;
    }

    public void setComentarioTexto(String comentarioTexto) {
        this.comentarioTexto = comentarioTexto;
    }

    public String getCalificacionComentario() {
        return calificacionComentario;
    }

    public void setCalificacionComentario(String calificacionComentario) {
        this.calificacionComentario = calificacionComentario;
    }
}
