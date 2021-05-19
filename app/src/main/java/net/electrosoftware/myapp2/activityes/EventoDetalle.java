package net.electrosoftware.myapp2.activityes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.ComentariosAdapter;
import net.electrosoftware.myapp2.clasesbases.ComentariosData;
import net.electrosoftware.myapp2.clasesbases.RuteoTask;
import net.electrosoftware.myapp2.firebaseClases.Comentario;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.RutaRef;
import net.electrosoftware.myapp2.firebaseClases.SitioFavorito;
import net.electrosoftware.myapp2.firebaseClases.UserAsistencia;
import net.electrosoftware.myapp2.firebaseClases.itemListaSitio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EventoDetalle extends AppCompatActivity {
    ToggleButton btn_evento_favoritos;
    Button btn_evento_comentar;//, btn_evento_act;
    ToggleButton btn_evento_asistencia;
    TextView txt_evento_nombre;
    // COMENTARIO
    TextView txt_dial_comentario_titulo, txt_evento_direccion, text_evento_horario_ini,
            text_evento_asistentes, text_evento_categoria, text_evento_telefono, txt_evento_descripcion,
            text_evento_quien_organiza, text_evento_horario_fin;
    EditText et_dial_comentario_agregar;
    Button btn_dial_comentario_cancelar, btn_dial_comentario_aceptar;
    ImageView imv_evento_foto;
    RutaRef rutaRefComentarios = null;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView rv_evento_comentarios;
    List<ComentariosData> dataModels;
    FloatingActionButton fab_actualizar_evento;
    ImageView imv_evento_ruteo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_evento_detalle);

        imv_evento_foto = (ImageView) findViewById(R.id.imv_evento_foto);

        txt_evento_nombre = (TextView) findViewById(R.id.txt_evento_nombre);
        txt_evento_direccion = (TextView) findViewById(R.id.txt_evento_direccion);
        text_evento_horario_ini = (TextView) findViewById(R.id.text_evento_horario_ini);
        text_evento_horario_fin = (TextView) findViewById(R.id.text_evento_horario_fin);
        text_evento_asistentes = (TextView) findViewById(R.id.text_evento_asistentes);
        text_evento_categoria = (TextView) findViewById(R.id.text_evento_categoria);
        text_evento_telefono = (TextView) findViewById(R.id.text_evento_telefono);
        text_evento_quien_organiza = (TextView) findViewById(R.id.text_evento_quien_organiza);
        txt_evento_descripcion = (TextView) findViewById(R.id.txt_evento_descripcion);
        rv_evento_comentarios = (RecyclerView) findViewById(R.id.rv_evento_comentarios);
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(EventoDetalle.this);
        rv_evento_comentarios.setLayoutManager(linearlayoutmanager);
        rv_evento_comentarios.setHasFixedSize(true);


        btn_evento_favoritos = (ToggleButton) findViewById(R.id.btn_evento_favoritos);
        btn_evento_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference FavoritoUserEventRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                        .child(user.getUid())
                        .child(FirebaseReferences.EVENTO_REFERENCE)
                        .child(Comunicador.getIdEvento());
                if (btn_evento_favoritos.isChecked()) {
                    itemListaSitio is = new itemListaSitio(Comunicador.getEvento().getNombre(), Comunicador.getEvento().getTipo(), Comunicador.getEvento().getRutaFoto());
                    is.writeItemListaSitio(FavoritoUserEventRef);
                    Toast.makeText(EventoDetalle.this, "Guardado en tus Favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    FavoritoUserEventRef.removeValue();
                    Toast.makeText(EventoDetalle.this, "Eliminado de tus Favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_evento_comentar = (Button) findViewById(R.id.btn_evento_comentar);
        btn_evento_comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comentarPromo(txt_evento_nombre.getText().toString());
            }
        });

        fab_actualizar_evento = (FloatingActionButton) findViewById(R.id.fab_actualizar_evento);

        fab_actualizar_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarEventoInFragment();
            }
        });

        /*btn_evento_act = (Button) findViewById(R.id.btn_evento_act);
        btn_evento_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarEventoInFragment();
            }
        });*/

        btn_evento_asistencia = (ToggleButton) findViewById(R.id.btn_evento_asistencia);
        btn_evento_asistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference AsistenciaRef = database.getReference(FirebaseReferences.ASISTENCIA_REFERENCE)
                        .child(Comunicador.getIdEvento())
                        .child(user.getUid());
                if (btn_evento_asistencia.isChecked()) {
                    UserAsistencia ua = new UserAsistencia(true);
                    ua.writeNewUserAsistencia(AsistenciaRef);
                    text_evento_asistentes.setText("Asistentes: " + (Comunicador.getEvento().getAsistentes() + 1));

                } else {
                    UserAsistencia ua = new UserAsistencia(false);
                    ua.writeNewUserAsistencia(AsistenciaRef);
                    int asist = (Comunicador.getEvento().getAsistentes() - 1);
                    if (asist < 0) {
                        asist = 0;
                    }
                    text_evento_asistentes.setText("Asistentes: " + asist);
                }

                Comunicador.actualizarAsistentesEvento(Comunicador.getIdEvento());


            }
        });
        //Comunicador.actualizarAsistentesEvento(Comunicador.getIdEvento());
        cargarEventoInFragment();

        imv_evento_foto.setImageResource(R.drawable.loading);

        imv_evento_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventoDetalle.this, Galeria.class));
            }
        });

        if (Comunicador.getEvento() != null) {
            //Toast.makeText(getActivity(), ic.getNombre(), Toast.LENGTH_SHORT).show();

            if (Comunicador.getEvento().getRutaFoto() != null && !(Comunicador.getEvento().getRutaFoto().equalsIgnoreCase("Sin imagen"))) {

                StorageReference imagesRef = storage.getReference("foto sitios/evento/" + Comunicador.getEvento().getRutaFoto());
                imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imv_evento_foto.setImageBitmap(b);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        imv_evento_foto.setImageResource(R.drawable.no_image_found);
                    }
                });
            } else {
                imv_evento_foto.setImageResource(R.drawable.no_image_found);
            }
        }

        database.getReference(FirebaseReferences.ASISTENCIA_REFERENCE)
                .child(Comunicador.getIdEvento())
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAsistencia ua = dataSnapshot.getValue(UserAsistencia.class);
                        if (ua != null && ua.getAsistencia()) {
                            btn_evento_asistencia.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.EVENTO_REFERENCE)
                .child(Comunicador.getIdEvento())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SitioFavorito sf = dataSnapshot.getValue(SitioFavorito.class);
                        if (sf != null) {
                            btn_evento_favoritos.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        cargarComentarios();
        initializeAdapter();

        imv_evento_ruteo = (ImageView) findViewById(R.id.imv_evento_ruteo);
        imv_evento_ruteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuteoTask.Parametros(Comunicador.getContextMapa(), Comunicador.getMiPosicion(), Comunicador.getMiPosicionDestino(), Comunicador.getGoogleMap(), EventoDetalle.this);
                new RuteoTask().execute();
            }
        });
    }


    public void comentarPromo(String nombreSitio) {
        final Dialog dialog = new Dialog(EventoDetalle.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_comentario);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_dial_comentario_titulo = (TextView) dialog.findViewById(R.id.txt_dial_comentario_titulo);
        et_dial_comentario_agregar = (EditText) dialog.findViewById(R.id.et_dial_comentario_agregar);

        btn_dial_comentario_cancelar = (Button) dialog.findViewById(R.id.btn_dial_comentario_cancelar);
        btn_dial_comentario_aceptar = (Button) dialog.findViewById(R.id.btn_dial_comentario_aceptar);

        txt_dial_comentario_titulo.setText(nombreSitio);

        btn_dial_comentario_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });

        btn_dial_comentario_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference FavoritoUserEventRef = database.getReference(FirebaseReferences.COMENTARIO_REFERENCE)
                        .child(Comunicador.getIdEvento())
                        .child(user.getUid());

                Comentario c = new Comentario(Comunicador.getUsuario().getNombre(), getDatePhone(), et_dial_comentario_agregar.getText().toString(), Comunicador.getUsuario().getRutaFoto());
                c.writeNewComentario(FavoritoUserEventRef);

                Toast.makeText(EventoDetalle.this, "Comentario agregado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private String getDatePhone() {

        Calendar cal = new GregorianCalendar();

        Date date = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        String formatteDate = df.format(date);

        return formatteDate;

    }

    private void cargarComentarios() {
        dataModels = new ArrayList<>();
        final Bitmap icon = BitmapFactory.decodeResource(EventoDetalle.this.getResources(),
                R.drawable.usuario);
        if (rutaRefComentarios != null) {
            rutaRefComentarios.getdatabaseReference().removeEventListener(rutaRefComentarios.getvalueEventListener());
            rutaRefComentarios = null;
        }
        rutaRefComentarios = new RutaRef();
        rutaRefComentarios.setdatabaseReference(database.getReference(FirebaseReferences.COMENTARIO_REFERENCE)
                .child(Comunicador.getIdEvento()).child(user.getUid()));
        rutaRefComentarios.setvalueEventListener(rutaRefComentarios.getdatabaseReference().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataModels.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Comentario c = ds.getValue(Comentario.class);
                            String[] fecha = c.getFecha().split("-");
                            String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", " ;Septiembre"
                                    , "Octubre", "Noviembre", "Diciemrbre"};
                            String mes = meses[Integer.parseInt(fecha[1]) - 1];
                            String fech = fecha[0] + " " + mes + " " + fecha[2];
                            dataModels.add(new ComentariosData(icon, c.getNombreUser(), fech, c.getTextComentario(), null));
                        }
                        ComentariosAdapter adapter = new ComentariosAdapter(dataModels);
                        rv_evento_comentarios.setAdapter(adapter);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }));
    }

    private void initializeAdapter() {
        ComentariosAdapter adapter = new ComentariosAdapter(dataModels);
        rv_evento_comentarios.setAdapter(adapter);
    }


    private void cargarEventoInFragment() {
        Comunicador.cargarEvento(Comunicador.getIdEvento());

        txt_evento_nombre.setText(Comunicador.getEvento().getNombre());
        txt_evento_direccion.setText(Comunicador.getEvento().getDireccion());
        text_evento_horario_ini.setText("Inicio: " + Comunicador.getEvento().getFechaIni() + " " + Comunicador.getEvento().getHoraIni());
        text_evento_horario_fin.setText("Fin: " + Comunicador.getEvento().getFechaFin() + " " + Comunicador.getEvento().getHoraFin());
        text_evento_asistentes.setText("Asistentes: " + Comunicador.getEvento().getAsistentes());
        text_evento_categoria = (TextView) findViewById(R.id.text_evento_categoria);

        text_evento_telefono.setText(Comunicador.getEvento().getTelefono());
        text_evento_quien_organiza.setText(Comunicador.getEvento().getPatrocinador());
        txt_evento_descripcion.setText(Comunicador.getEvento().getDescripcion());

        String tipo = "";
        switch (Comunicador.getEvento().getTipo()) {
            case "restaurante":
                tipo = "Restaurante y Gastronomía";
                break;
            case "rumba":
                tipo = "Rumba, Bares y Discotecas";
                break;
            case "cultura":
                tipo = "Arte y Cultura";
                break;
            case "musica":
                tipo = "Música y Conciertos";
                break;
            case "deporte":
                tipo = "Deporte y Salud";
                break;
            case "ropa":
                tipo = "Ropa y Accesorios";
                break;
            case "religion":
                tipo = "Religión";
                break;
        }

        text_evento_categoria.setText(tipo);
    }
}
