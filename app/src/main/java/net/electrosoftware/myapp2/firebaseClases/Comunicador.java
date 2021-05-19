package net.electrosoftware.myapp2.firebaseClases;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by CARLOS MAESTRE on 23/05/2017.
 */

public class Comunicador {

    private static String TipoPerfil;
    private static Object objeto1 = null;
    private static Object objeto2 = null;
    private static Usuario usuario = null;
    private static Evento evento = null;
    private static Lugar lugar = null;
    private static String idEvento = null;
    private static String tipoSitio = null;
    private static Bitmap fotoEvento = null;
    private static Drawable fotoGaleriaItem = null;
    private static LatLng MiPosicion = null;
    private static LatLng MiPosicionDestino = null;
    private static GoogleMap googleMap = null;
    private static Context contextMapa = null;

    public static Lugar getLugar() {
        return lugar;
    }

    public static void setLugar(Lugar lugar) {
        Comunicador.lugar = lugar;
    }

    public static String getTipoSitio() {
        return tipoSitio;
    }

    public static void setTipoSitio(String tipoSitio) {
        Comunicador.tipoSitio = tipoSitio;
    }

    public static void setObjeto1(Object newObjeto) {
        objeto1 = newObjeto;
    }

    public static Object getObjeto1() {
        return objeto1;
    }

    public static void setObjeto2(Object newObjeto) {
        objeto2 = newObjeto;
    }

    public static Object getObjeto2() {
        return objeto2;
    }

    public static String getTipoPerfil() {
        return TipoPerfil;
    }

    public static void setTipoPerfil(String tipoPerfil) {
        TipoPerfil = tipoPerfil;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Comunicador.usuario = usuario;
    }

    public static Evento getEvento() {
        return evento;
    }

    public static void setEvento(Evento evento) {
        Comunicador.evento = evento;
    }

    public static String getIdEvento() {
        return idEvento;
    }

    public static void setIdEvento(String idEvento) {
        Comunicador.idEvento = idEvento;
    }

    public static Bitmap getFotoEvento() {
        return fotoEvento;
    }

    public static void setFotoEvento(Bitmap fotoEvento) {
        Comunicador.fotoEvento = fotoEvento;
    }

    public static void actualizarAsistentesEvento(final String idEvento) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dr = database.getReference(FirebaseReferences.ASISTENCIA_REFERENCE).child(idEvento);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cont = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserAsistencia ua = snapshot.getValue(UserAsistencia.class);
                    if (ua.getAsistencia()) {
                        cont = cont + 1;
                    }
                }
                database.getReference(FirebaseReferences.SITIO_REFERENCE)
                        .child(idEvento).child("asistentes").setValue(cont);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void cargarEvento(String idEvento) {

        actualizarAsistentesEvento(idEvento);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dr = database.getReference(FirebaseReferences.SITIO_REFERENCE).child(idEvento);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                evento = dataSnapshot.getValue(Evento.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void cargarLugar(String idLugar) {

        actualizarAsistentesEvento(idLugar);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dr = database.getReference(FirebaseReferences.SITIO_REFERENCE).child(idEvento);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lugar = dataSnapshot.getValue(Lugar.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static Drawable getFotoGaleriaItem() {
        return fotoGaleriaItem;
    }

    public static void setFotoGaleriaItem(Drawable fotoGaleriaItem) {
        Comunicador.fotoGaleriaItem = fotoGaleriaItem;
    }

    public static LatLng getMiPosicion() {
        return MiPosicion;
    }

    public static void setMiPosicion(LatLng miPosicion) {
        MiPosicion = miPosicion;
    }

    public static LatLng getMiPosicionDestino() {
        return MiPosicionDestino;
    }

    public static void setMiPosicionDestino(LatLng miPosicionDestino) {
        MiPosicionDestino = miPosicionDestino;
    }

    public static GoogleMap getGoogleMap() {
        return googleMap;
    }

    public static void setGoogleMap(GoogleMap googleMap) {
        Comunicador.googleMap = googleMap;
    }

    public static Context getContextMapa() {
        return contextMapa;
    }

    public static void setContextMapa(Context contextMapa) {
        Comunicador.contextMapa = contextMapa;
    }
}