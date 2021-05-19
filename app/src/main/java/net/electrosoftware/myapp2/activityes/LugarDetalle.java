package net.electrosoftware.myapp2.activityes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.ComentariosAdapter;
import net.electrosoftware.myapp2.clasesbases.ComentariosData;
import net.electrosoftware.myapp2.clasesbases.RuteoTask;
import net.electrosoftware.myapp2.firebaseClases.Comentario;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.RutaRef;
import net.electrosoftware.myapp2.firebaseClases.SitioFavorito;
import net.electrosoftware.myapp2.firebaseClases.itemListaSitio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class LugarDetalle extends AppCompatActivity {
    private static final String TAG = "TAG";
    ToggleButton btn_lugar_favoritos, btn_lugar_calificar;
    Button btn_lugar_comentar;
    List<ComentariosData> dataModels;
    RecyclerView rv_lugar_comentarios;
    TextView txt_lugar_nombre, txt_lugar_direccion, text_lugar_calificacion, text_lugar_horario, text_lugar_categoria, text_lugar_telefono, txt_lugar_descripcion, txt_lugar_ver_mas_comentarios;
    ImageView imv_lugar_foto;

    // CALIFICACION
    TextView txt_dial_titulo_rate, txt_dial_puntaje_rate;
    SmileRating rtng_dial_smile_rate;
    Button btn_dial_cancelar_rate, btn_dial_aceptar_rate;

    // COMENTARIO
    TextView txt_dial_comentario_titulo;
    EditText et_dial_comentario_agregar;
    Button btn_dial_comentario_cancelar, btn_dial_comentario_aceptar;
    //Button  btn_lugar_eventos, btn_lugar_promociones;
    ImageView imv_lugar_ruteo;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RutaRef rutaRefComentarios = null;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_lugar_detalle);

        btn_lugar_favoritos = (ToggleButton) findViewById(R.id.btn_lugar_favoritos);

        btn_lugar_calificar = (ToggleButton) findViewById(R.id.btn_lugar_calificar);
        btn_lugar_comentar = (Button) findViewById(R.id.btn_lugar_comentar);

        btn_lugar_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference FavoritoUserEventRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                        .child(user.getUid())
                        .child(FirebaseReferences.LUGAR_REFERENCE)
                        .child(Comunicador.getIdEvento());
                if (btn_lugar_favoritos.isChecked()) {
                    itemListaSitio is = new itemListaSitio(Comunicador.getLugar().getNombre(),
                            Comunicador.getLugar().getTipo(),
                            Comunicador.getLugar().getRutaFoto());
                    is.writeItemListaSitio(FavoritoUserEventRef);
                    Toast.makeText(LugarDetalle.this, "Guardado en tus Favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    FavoritoUserEventRef.removeValue();
                    Toast.makeText(LugarDetalle.this, "Eliminado de tus Favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_lugar_calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_lugar_calificar.isChecked()) {
                    calificarSitio(txt_lugar_nombre.getText().toString());
                    //Toast.makeText(LugarDetalle.this, "Califica este Lugar", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(LugarDetalle.this).setTitle("Confirmación")
                            .setMessage("¿Está seguro de eliminar su calificación?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setCancelable(true)
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btn_lugar_calificar.setChecked(false);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    btn_lugar_calificar.setChecked(true);
                                }
                            })
                            .show();
                }
            }
        });

        btn_lugar_comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(LugarDetalle.this, AgregarComentarioActivity.class));
                comentarPromo(txt_lugar_nombre.getText().toString());
            }
        });

        rv_lugar_comentarios = (RecyclerView) findViewById(R.id.rv_lugar_comentarios);
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(LugarDetalle.this);
        rv_lugar_comentarios.setLayoutManager(linearlayoutmanager);
        rv_lugar_comentarios.setHasFixedSize(true);



        txt_lugar_nombre = (TextView) findViewById(R.id.txt_lugar_nombre);
        txt_lugar_direccion = (TextView) findViewById(R.id.txt_lugar_direccion);
        //text_lugar_calificacion = (TextView) findViewById(R.id.text_lugar_calificacion);
        text_lugar_horario = (TextView) findViewById(R.id.text_lugar_horario);
        text_lugar_categoria = (TextView) findViewById(R.id.text_lugar_categoria);
        text_lugar_telefono = (TextView) findViewById(R.id.text_lugar_telefono);
        txt_lugar_descripcion = (TextView) findViewById(R.id.txt_lugar_descripcion);

        imv_lugar_foto = (ImageView) findViewById(R.id.imv_lugar_foto);
        imv_lugar_foto.setImageResource(R.drawable.loading);
        imv_lugar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LugarDetalle.this, Galeria.class));
            }
        });
        /*btn_lugar_eventos = (Button) findViewById(R.id.btn_lugar_eventos);
        btn_lugar_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LugarDetalle.this, EventoDetalle.class));
            }
        });

        btn_lugar_promociones = (Button) findViewById(R.id.btn_lugar_promociones);
        btn_lugar_promociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LugarDetalle.this, PromoDetalle.class));
            }
        });*/

        txt_lugar_ver_mas_comentarios = (TextView) findViewById(R.id.txt_lugar_ver_mas_comentarios);
        txt_lugar_ver_mas_comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LugarDetalle.this, Comentarios.class));
            }
        });

        imv_lugar_ruteo = (ImageView) findViewById(R.id.imv_lugar_ruteo);
        imv_lugar_ruteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuteoTask.Parametros(Comunicador.getContextMapa(), Comunicador.getMiPosicion(),
                        Comunicador.getMiPosicionDestino(), Comunicador.getGoogleMap(), LugarDetalle.this);
                new RuteoTask().execute();
            }
        });

        cargarLugarInFragment();

        if (Comunicador.getLugar() != null) {
            //Toast.makeText(getActivity(), ic.getNombre(), Toast.LENGTH_SHORT).show();

            if (Comunicador.getLugar().getRutaFoto() != null && !(Comunicador.getLugar().getRutaFoto().equalsIgnoreCase("Sin imagen"))) {

                StorageReference imagesRef = storage.getReference("foto sitios/lugar/" + Comunicador.getLugar().getRutaFoto());
                imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imv_lugar_foto.setImageBitmap(b);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        imv_lugar_foto.setImageResource(R.drawable.no_image_found);
                    }
                });
            } else {
                imv_lugar_foto.setImageResource(R.drawable.no_image_found);
            }
        }

        database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.LUGAR_REFERENCE)
                .child(Comunicador.getIdEvento())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SitioFavorito sf = dataSnapshot.getValue(SitioFavorito.class);
                        if (sf != null) {
                            btn_lugar_favoritos.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        cargarComentarios();
        initializeAdapter();
    }

    private void initializeData() {
        dataModels = new ArrayList<>();

        Bitmap icon = BitmapFactory.decodeResource(LugarDetalle.this.getResources(),
                R.drawable.usuario);

        dataModels.add(new ComentariosData(icon, "Joletta Spurdens", "11/09/2016", "Integer non velit", null));
        dataModels.add(new ComentariosData(icon, "Inessa Cutts", "04/19/2017", "Quisque ut erat", null));
        dataModels.add(new ComentariosData(icon, "Fairlie Yuryichev", "07/06/2016", "Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo", null));
        dataModels.add(new ComentariosData(icon, "Clem Ealam", "12/10/2016", "Nulla mollis molestie lorem", null));
        dataModels.add(new ComentariosData(icon, "Barbie Walklot", "01/06/2017", "Nullam molestie nibh in lectus", null));
        dataModels.add(new ComentariosData(icon, "Lief Gofford", "06/25/2016", "Fusce posuere felis sed lacus", null));
        dataModels.add(new ComentariosData(icon, "Noah Spurdens", "01/24/2017", "Mauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis", null));
        dataModels.add(new ComentariosData(icon, "Collete Dupoy", "11/05/2016", "Aliquam sit amet diam in magna bibendum imperdiet", null));
        dataModels.add(new ComentariosData(icon, "Charin Pluvier", "12/18/2016", "Morbi ut odio", null));
    }

    private void initializeAdapter() {
        ComentariosAdapter adapter = new ComentariosAdapter(dataModels);
        rv_lugar_comentarios.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void calificarSitio(String nombreSitio) {

        final Dialog dialog = new Dialog(LugarDetalle.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_calificacion);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_dial_titulo_rate = (TextView) dialog.findViewById(R.id.txt_dial_titulo_rate);
        txt_dial_puntaje_rate = (TextView) dialog.findViewById(R.id.txt_dial_puntaje_rate);

        rtng_dial_smile_rate = (SmileRating) dialog.findViewById(R.id.rtng_dial_smile_rate);

        btn_dial_cancelar_rate = (Button) dialog.findViewById(R.id.btn_dial_cancelar_rate);
        btn_dial_aceptar_rate = (Button) dialog.findViewById(R.id.btn_dial_aceptar_rate);

        txt_dial_titulo_rate.setText(nombreSitio);
        //txt_dial_puntaje_rate.setText("Tu calificación: " + level);

        rtng_dial_smile_rate.setNameForSmile(BaseRating.TERRIBLE, "Muy Malo");
        rtng_dial_smile_rate.setNameForSmile(BaseRating.BAD, "Malo");
        rtng_dial_smile_rate.setNameForSmile(BaseRating.OKAY, "Normal");
        rtng_dial_smile_rate.setNameForSmile(BaseRating.GOOD, "Bueno");
        rtng_dial_smile_rate.setNameForSmile(BaseRating.GREAT, "Muy Bueno");

        rtng_dial_smile_rate.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                int level = rtng_dial_smile_rate.getRating(); //level is from 1 to 5

                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        txt_dial_puntaje_rate.setText("Tu calificación: " + level);
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        txt_dial_puntaje_rate.setText("Tu calificación: " + level);
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        txt_dial_puntaje_rate.setText("Tu calificación: " + level);
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        txt_dial_puntaje_rate.setText("Tu calificación: " + level);
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        txt_dial_puntaje_rate.setText("Tu calificación: " + level);
                        break;
                }
            }
        });

        btn_dial_cancelar_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                btn_lugar_calificar.setChecked(false);
            }
        });

        btn_dial_aceptar_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LugarDetalle.this, "Sitio Calificado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                dialog.cancel();
                btn_lugar_calificar.setChecked(true);
            }
        });

        dialog.show();
    }

    private void cargarComentarios() {
        dataModels = new ArrayList<>();
        final Bitmap icon = BitmapFactory.decodeResource(LugarDetalle.this.getResources(),
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
                        rv_lugar_comentarios.setAdapter(adapter);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }));
    }

    public void comentarPromo(String nombreSitio) {
        final Dialog dialog = new Dialog(LugarDetalle.this);
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

                Toast.makeText(LugarDetalle.this, "Comentario agregado", Toast.LENGTH_SHORT).show();
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

    private void cargarLugarInFragment() {
        Comunicador.cargarLugar(Comunicador.getIdEvento());

        txt_lugar_nombre.setText(Comunicador.getLugar().getNombre());
        txt_lugar_direccion.setText(Comunicador.getLugar().getDireccion());
        text_lugar_horario.setText(Comunicador.getLugar().getHorario());
        text_lugar_categoria = (TextView) findViewById(R.id.text_lugar_categoria);

        text_lugar_telefono.setText(Comunicador.getLugar().getTelefono());
        txt_lugar_descripcion.setText(Comunicador.getLugar().getDescripcion());

        String tipo = "";
        switch (Comunicador.getLugar().getTipo()) {
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

        text_lugar_categoria.setText(tipo);
    }
}

