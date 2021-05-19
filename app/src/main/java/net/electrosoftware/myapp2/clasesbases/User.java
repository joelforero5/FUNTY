package net.electrosoftware.myapp2.clasesbases;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {

    String correo_usuario,nombrePerfil_usuario,perfil,contraseña;
    Bitmap foto;

    public User(String correo_usuario, String nombrePerfil_usuario, String perfil, String contraseña) {
        this.correo_usuario = correo_usuario;
        this.nombrePerfil_usuario = nombrePerfil_usuario;
        this.perfil = perfil;
        this.contraseña = contraseña;
    }

    public String getCorreo_usuario() {
        return correo_usuario;
    }

    public void setCorreo_usuario(String correo_usuario) {
        this.correo_usuario = correo_usuario;
    }

    public String getNombrePerfil_usuario() {
        return nombrePerfil_usuario;
    }

    public void setNombrePerfil_usuario(String nombrePerfil_usuario) {
        this.nombrePerfil_usuario = nombrePerfil_usuario;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public User(String correo_usuario, String nombrePerfil_usuario, String perfil, String contraseña, Bitmap foto) {
        this.correo_usuario = correo_usuario;
        this.nombrePerfil_usuario = nombrePerfil_usuario;
        this.perfil = perfil;
        this.contraseña = contraseña;
        this.foto = foto;
    }
}
