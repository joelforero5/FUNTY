package net.electrosoftware.myapp2.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import net.electrosoftware.myapp2.activityes.AgregarEvento;
import net.electrosoftware.myapp2.activityes.AgregarLugar;
import net.electrosoftware.myapp2.clasesbases.MisEventosAdapter;
import net.electrosoftware.myapp2.clasesbases.MisSitiosData;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.Usuario;
import net.electrosoftware.myapp2.firebaseClases.itemListaSitio;

import java.util.List;

public class FragmentListaSitios extends Fragment {
    View view;
    TextView txt_crear_sitio;


    List<MisSitiosData> dataModels;
    MisEventosAdapter adapter;
    Toolbar mToolbar;
    //final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //final FirebaseStorage storage = FirebaseStorage.getInstance();
    Bitmap icon = null;

    private RecyclerView rv_mis_eventos;
    private FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder> mPostAdapter;
    private DatabaseReference listaEventosRef;

    public FragmentListaSitios() {
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
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mis_eventos, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbarMisEventos);
        txt_crear_sitio = (TextView) view.findViewById(R.id.txt_crear_sitio);
        if(Comunicador.getTipoSitio() != null){

            switch (Comunicador.getTipoSitio()){
                case "lugar":
                    mToolbar.setTitle("Mis Lugares");
                    txt_crear_sitio.setText("Crear Lugar");
                    break;
                case "evento":
                    mToolbar.setTitle("Mis Eventos");
                    txt_crear_sitio.setText("Crear Evento");
                    break;
                case "promocion":
                    mToolbar.setTitle("Mis Promociones");
                    txt_crear_sitio.setText("Crear Promoción");
                    break;
            }
        }else{
            Toast.makeText(getActivity(), "No podemos tramitar la solicitud, tipo sitio null", Toast.LENGTH_SHORT).show();
        }

       // final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        txt_crear_sitio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), AgregarEvento.class));

              /* final DatabaseReference userRef = database.getReference(FirebaseReferences.USUARIOS_REFERENCE);

                userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        if (usuario.getPerfil().equalsIgnoreCase("Consumidor")) {
                            Comunicador.setTipoPerfil("Consumidor");
                        } else if (usuario.getPerfil().equalsIgnoreCase("Empresario")) {
                            Comunicador.setTipoPerfil("Empresario");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                if(Comunicador.getTipoSitio() != null){

                    switch (Comunicador.getTipoSitio()){
                        case "lugar":
                            startActivity(new Intent(getActivity(), AgregarLugar.class));
                            break;
                        case "evento":
                            startActivity(new Intent(getActivity(), AgregarEvento.class));
                            break;
                        case "promocion":
                            //startActivity(new Intent(getActivity(), AgregarEvento.class));
                            break;
                    }
                }else{
                    Toast.makeText(getActivity(), "No podemos tramitar la solicitud, tipo sitio null", Toast.LENGTH_SHORT).show();
                }
*/
            }
        });


        //LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(getActivity());
        //rv_mis_eventos.setLayoutManager(linearlayoutmanager);
        //rv_mis_eventos.setHasFixedSize(true);


        //initializeData();
        //initializeAdapter();
        initialiseScreen();

        return view;
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

    private void initialiseScreen() {
        if(Comunicador.getTipoSitio() != null){
           /* final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            rv_mis_eventos = (RecyclerView) view.findViewById(R.id.rv_mis_eventos);
            rv_mis_eventos.setLayoutManager(new LinearLayoutManager(getActivity()));
            listaEventosRef = database.getReference(FirebaseReferences.LISTA_REFERENCE)
                    .child(user.getUid())
                    .child(Comunicador.getTipoSitio());
            setupAdapter();
            rv_mis_eventos.setAdapter(mPostAdapter);

            */
        }else{
            Toast.makeText(getActivity(), "No podemos tramitar la solicitud, tipo sitio null", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupAdapter() {
        mPostAdapter = new FirebaseRecyclerAdapter<itemListaSitio, PostViewHolder>(
                itemListaSitio.class,
                R.layout.item_mis_sitios,
                PostViewHolder.class,
                listaEventosRef
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final itemListaSitio model, int position) {
                /*StorageReference storageReference = storage.getReference()
                        .child("foto sitios/"+Comunicador.getTipoSitio()+"/" + model.getFoto());
               // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getFoto());
                Glide.with(getActivity())
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(viewHolder.imv_dial_mis_eventos_foto);

                 */

                viewHolder.setTxt_dial_mis_eventos_nombre(model.getNombre());
                viewHolder.setTxt_dial_mis_eventos_tipo(model.getTipo());

            }
        };
    }



}
