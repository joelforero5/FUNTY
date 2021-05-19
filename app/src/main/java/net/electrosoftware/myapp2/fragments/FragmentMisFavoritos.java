package net.electrosoftware.myapp2.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import net.electrosoftware.myapp2.clasesbases.MisEventosAdapter;
import net.electrosoftware.myapp2.clasesbases.MisSitiosData;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.SitioFavorito;
import net.electrosoftware.myapp2.firebaseClases.itemListaSitio;

import java.util.ArrayList;
import java.util.List;

public class FragmentMisFavoritos extends Fragment {
    View view;
    TextView txt_mis_favoritos_crear;
    RecyclerView rv_mis_favoritos;
   // private FirebaseRecyclerAdapter<itemListaSitio,PostViewHolder> mPostAdapter;
    private DatabaseReference listaLugarFavRef;
    private DatabaseReference listaEventosFavRef;
    private DatabaseReference listaPromoFavRef;

    List<MisSitiosData> LugaresDataModels;
    List<MisSitiosData> EventosDataModels;
    List<MisSitiosData> PromocionesDataModels;

    MisEventosAdapter Lugaresadapter;
    MisEventosAdapter Eventosadapter;
    MisEventosAdapter Promocionesadapter;
    Toolbar mToolbar;

    Button btn_favoritos_lugares, btn_favoritos_eventos, btn_favoritos_promociones;
    Bitmap icon;

    //final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //final FirebaseStorage storage = FirebaseStorage.getInstance();

    public FragmentMisFavoritos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mis_favoritos, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbarMisfavoritos);
        mToolbar.setTitle("Mis Favoritos");

        rv_mis_favoritos = (RecyclerView) view.findViewById(R.id.rv_mis_favoritos);
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(getActivity());
        rv_mis_favoritos.setLayoutManager(linearlayoutmanager);
        rv_mis_favoritos.setHasFixedSize(true);
        //initializeData();
        //initializeAdapter(1);

        btn_favoritos_lugares = (Button) view.findViewById(R.id.btn_favoritos_lugares);
        btn_favoritos_eventos = (Button) view.findViewById(R.id.btn_favoritos_eventos);
        btn_favoritos_promociones = (Button) view.findViewById(R.id.btn_favoritos_promociones);

        btn_favoritos_lugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAdapter(1);
            }
        });

        btn_favoritos_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAdapter(2);
            }
        });

        btn_favoritos_promociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAdapter(3);
            }
        });

        initialiseScreen();

        return view;
    }

    private void initialiseScreen() {
       // final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        rv_mis_favoritos = (RecyclerView) view.findViewById(R.id.rv_mis_favoritos);
        rv_mis_favoritos.setLayoutManager(new LinearLayoutManager(getActivity()));
       /* listaLugarFavRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.LUGAR_REFERENCE);
        listaEventosFavRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.EVENTO_REFERENCE);
        listaPromoFavRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.PROMOCION_REFERENCE);
        setupAdapter(1);

        */

    }

    private void setupAdapter(int adapter) {

       /* switch (adapter) {
            case 1:
                mPostAdapter = new FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder>(
                        itemListaSitio.class,
                        R.layout.item_mis_sitios,
                        PostViewHolder.class,
                        listaLugarFavRef
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, final itemListaSitio model, int position) {
                        StorageReference storageReference = storage.getReference().child("foto sitios/lugar/" + model.getFoto());
                        // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getFoto());
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(viewHolder.imv_dial_mis_eventos_foto);

                        viewHolder.setTxt_dial_mis_eventos_nombre(model.getNombre());
                        viewHolder.setTxt_dial_mis_eventos_tipo(model.getTipo());

                    }
                };
                break;
            case 2:
                mPostAdapter = new FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder>(
                        itemListaSitio.class,
                        R.layout.item_mis_sitios,
                        PostViewHolder.class,
                        listaEventosFavRef
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, final itemListaSitio model, int position) {
                        StorageReference storageReference = storage.getReference().child("foto sitios/evento/" + model.getFoto());
                        // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getFoto());
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(viewHolder.imv_dial_mis_eventos_foto);

                        viewHolder.setTxt_dial_mis_eventos_nombre(model.getNombre());
                        viewHolder.setTxt_dial_mis_eventos_tipo(model.getTipo());

                    }
                };
                break;
            case 3:
                mPostAdapter = new FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder>(
                        itemListaSitio.class,
                        R.layout.item_mis_sitios,
                        PostViewHolder.class,
                        listaPromoFavRef
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, final itemListaSitio model, int position) {
                        StorageReference storageReference = storage.getReference().child("foto sitios/promocion/" + model.getFoto());
                        // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getFoto());
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(viewHolder.imv_dial_mis_eventos_foto);

                        viewHolder.setTxt_dial_mis_eventos_nombre(model.getNombre());
                        viewHolder.setTxt_dial_mis_eventos_tipo(model.getTipo());

                    }
                };
                break;
            default:
                mPostAdapter = new FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder>(
                        itemListaSitio.class,
                        R.layout.item_mis_sitios,
                        PostViewHolder.class,
                        listaLugarFavRef
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, final itemListaSitio model, int position) {
                        StorageReference storageReference = storage.getReference().child("foto sitios/lugar/" + model.getFoto());
                        // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getFoto());
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(viewHolder.imv_dial_mis_eventos_foto);

                        viewHolder.setTxt_dial_mis_eventos_nombre(model.getNombre());
                        viewHolder.setTxt_dial_mis_eventos_tipo(model.getTipo());

                    }
                };
                break;
        }
        rv_mis_favoritos.setAdapter(mPostAdapter);
        mPostAdapter.notifyDataSetChanged();*/

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView imv_dial_mis_eventos_foto;
        public TextView txt_dial_mis_eventos_nombre;
        public TextView txt_dial_mis_eventos_tipo;


        public PostViewHolder(View itemView) {
            super(itemView);

            imv_dial_mis_eventos_foto = (ImageView) itemView.findViewById(R.id.imv_dial_mis_eventos_foto);
            txt_dial_mis_eventos_nombre = (TextView) itemView.findViewById(R.id.txt_dial_mis_eventos_nombre);
            txt_dial_mis_eventos_tipo = (TextView) itemView.findViewById(R.id.txt_dial_mis_eventos_tipo);
        }

        public void setTxt_dial_mis_eventos_nombre(String txt_dial_mis_eventos_nombre) {
            this.txt_dial_mis_eventos_nombre.setText(txt_dial_mis_eventos_nombre);
        }

        public void setTxt_dial_mis_eventos_tipo(String txt_dial_mis_eventos_tipo) {
            String tipo = "";
            switch (txt_dial_mis_eventos_tipo) {
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
            this.txt_dial_mis_eventos_tipo.setText(tipo);
        }
    }

    private void initializeData() {
        LugaresDataModels = new ArrayList<>();
        EventosDataModels = new ArrayList<>();
        PromocionesDataModels = new ArrayList<>();

        icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.kamran);

        /*final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference listaFavoritoEventoRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.EVENTO_REFERENCE);
        final DatabaseReference listaFavoritoLugarRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.LUGAR_REFERENCE);
        final DatabaseReference listaFavoritoPromoRef = database.getReference(FirebaseReferences.FAVORITO_REFERENCE)
                .child(user.getUid())
                .child(FirebaseReferences.PROMOCION_REFERENCE);

        listaFavoritoEventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.kamran);
                EventosDataModels.clear();


                String tipo = "";
                //Iterable<DataSnapshot> items = dataSnapshot.getChildren();
                try {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        //String idEvento = i.getKey();
                        SitioFavorito item = i.getValue(SitioFavorito.class);

                        if (!item.getNombreFoto().equalsIgnoreCase("Sin imagen")) {
                            StorageReference fotoRef = storage.getReference().child("foto usuarios/" + item.getNombreFoto());
                            fotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    // Use the bytes to display the image
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    //imageCard.setImageResource(R.drawable.no_image_found);
                                }
                            });
                        }
                            switch (item.gettipo()) {
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
                            EventosDataModels.add(new MisSitiosData(icon, item.getNombreSitio(), tipo));


                            if (EventosDataModels.size() == 1) {
                                Eventosadapter = new MisEventosAdapter(EventosDataModels);
                                //rv_mis_eventos.setAdapter(Eventosadapter);
                            } else if (EventosDataModels.size() > 1) {
                                Eventosadapter.notifyDataSetChanged();
                            }

                    }
                } catch (Exception e) {
                    //Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
*/
        /*    @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

        /*listaFavoritoLugarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.kamran);
                LugaresDataModels.clear();


                String tipo = "";
                //Iterable<DataSnapshot> items = dataSnapshot.getChildren();
                try {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        //String idEvento = i.getKey();
                        SitioFavorito item = i.getValue(SitioFavorito.class);

                        if (!item.getNombreFoto().equalsIgnoreCase("Sin imagen")) {
                            StorageReference fotoRef = storage.getReference().child("foto usuarios/" + item.getNombreFoto());
                            fotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    // Use the bytes to display the image
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    //imageCard.setImageResource(R.drawable.no_image_found);
                                }
                            });
                        }
                            switch (item.gettipo()) {
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
                            LugaresDataModels.add(new MisSitiosData(icon, item.getNombreSitio(), tipo));


                            if (LugaresDataModels.size() == 1) {
                                Lugaresadapter= new MisEventosAdapter(LugaresDataModels);
                                //rv_mis_eventos.setAdapter(Eventosadapter);
                            } else if (LugaresDataModels.size() > 1) {
                                Lugaresadapter.notifyDataSetChanged();
                            }

                    }
                } catch (Exception e) {
                    //Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

        /*listaFavoritoPromoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.kamran);
                PromocionesDataModels.clear();


                String tipo = "";
                //Iterable<DataSnapshot> items = dataSnapshot.getChildren();
                *//*try {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        //String idEvento = i.getKey();
                        SitioFavorito item = i.getValue(SitioFavorito.class);

                        if (!item.getNombreFoto().equalsIgnoreCase("Sin imagen")) {
                            StorageReference fotoRef = storage.getReference().child("foto usuarios/" + item.getNombreFoto());
                            fotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    // Use the bytes to display the image
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    //imageCard.setImageResource(R.drawable.no_image_found);
                                }
                            });
                        }

                            switch (item.gettipo()) {
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
                            PromocionesDataModels.add(new MisSitiosData(icon, item.getNombreSitio(), tipo));


                            if (PromocionesDataModels.size() == 1) {
                                Promocionesadapter = new MisEventosAdapter(PromocionesDataModels);
                                //rv_mis_eventos.setAdapter(Eventosadapter);
                            } else if (PromocionesDataModels.size() > 1) {
                                Promocionesadapter.notifyDataSetChanged();
                            }
                        }

                } catch (Exception e) {
                    //Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
*//*
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

        /*LugaresDataModels.add(new MisSitiosData(icon, "Paraiso", "8 David Alley"));
        LugaresDataModels.add(new MisSitiosData(icon, "Dois Vizinhos", "3599 Morning Hill"));
        LugaresDataModels.add(new MisSitiosData(icon, "Manaoag", "94 Orin Plaza"));
        LugaresDataModels.add(new MisSitiosData(icon, "Chengzhong", "57873 Sachs Drive"));
        LugaresDataModels.add(new MisSitiosData(icon, "Paldit", "5 Loomis Way"));
        LugaresDataModels.add(new MisSitiosData(icon, "Asker", "2 Veith Street"));
        LugaresDataModels.add(new MisSitiosData(icon, "Jinshan", "6 Longview Parkway"));
        LugaresDataModels.add(new MisSitiosData(icon, "Aourir", "699 Pawling Junction"));
        LugaresDataModels.add(new MisSitiosData(icon, "Dajin", "38467 Lerdahl Trail"));

        EventosDataModels.add(new MisSitiosData(icon, "Twitterwire", "8 Arrowood Drive"));
        EventosDataModels.add(new MisSitiosData(icon, "Oyoloo", "34 Jay Hill"));
        EventosDataModels.add(new MisSitiosData(icon, "Avavee", "9 Forest Lane"));
        EventosDataModels.add(new MisSitiosData(icon, "Twinder", "511 Sauthoff Pass"));
        EventosDataModels.add(new MisSitiosData(icon, "Thoughtstorm", "06444 Columbus Pass"));
        EventosDataModels.add(new MisSitiosData(icon, "Skajo", "3699 Sunfield Crossing"));
        EventosDataModels.add(new MisSitiosData(icon, "Twitternation", "840 Parkside Terrace"));
        EventosDataModels.add(new MisSitiosData(icon, "Youspan", "74 Doe Crossing Hill"));
        EventosDataModels.add(new MisSitiosData(icon, "Quire", "8 Nevada Plaza"));

        PromocionesDataModels.add(new MisSitiosData(icon, "Jewelery", "9 Thackeray Terrace"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Automotive", "7 Hanson Drive"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Jewelery", "08 Debs Drive"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Sports", "86 Sauthoff Center"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Kids", "88864 Lyons Hill"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Industrial", "619 Dryden Point"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Automotive", "1 Almo Hill"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Toys", "54362 Montana Avenue"));
        PromocionesDataModels.add(new MisSitiosData(icon, "Electronics", "2497 Garrison Junction"));*/
    }

    private void initializeAdapter(int adapter) {
        Lugaresadapter = new MisEventosAdapter(LugaresDataModels);
        Eventosadapter = new MisEventosAdapter(EventosDataModels);
        Promocionesadapter = new MisEventosAdapter(PromocionesDataModels);

        switch (adapter) {
            case 1:
                rv_mis_favoritos.setAdapter(Lugaresadapter);
                break;
            case 2:
                rv_mis_favoritos.setAdapter(Eventosadapter);
                break;
            case 3:
                rv_mis_favoritos.setAdapter(Promocionesadapter);
                break;
            default:
                rv_mis_favoritos.setAdapter(Lugaresadapter);
                break;
        }

    }
}
