package net.electrosoftware.myapp2.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.activityes.EventoDetalle;
import net.electrosoftware.myapp2.activityes.LugarDetalle;
import net.electrosoftware.myapp2.activityes.PromoDetalle;
import net.electrosoftware.myapp2.clasesbases.MultiSelectionSpinner;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.Empresa;
import net.electrosoftware.myapp2.firebaseClases.Evento;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.GeoPunto;
import net.electrosoftware.myapp2.firebaseClases.Lugar;
import net.electrosoftware.myapp2.firebaseClases.RutaRef;
import net.electrosoftware.myapp2.firebaseClases.Sitio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.makeramen.roundedimageview.RoundedImageView.TAG;

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    View view, dialogo_filtro;
    private GoogleMap miMapa;
    double latitud = 0.0;
    double longitud = 0.0;

    List<Sitio> Sitios = new ArrayList<>();
    //List<Sitio> Eventos = new ArrayList<>();
    //List<Sitio> Promociones = new ArrayList<>();
    List<RutaRef> rutasReferenciadas = new ArrayList<>();
    String filtroSitio = "lugar";
    String fechaFiltro = "";

    List<Empresa> empresas = new ArrayList<>();
    List<String> tipos = new ArrayList<>();

    RelativeLayout cardView;
    ImageView imageCard;
    TextView NameCard, DirectionCard, RatingCard;

    FloatingActionsMenu fab_menu;
    FloatingActionButton fabLugares, fabEventos, fabPromociones;

    TextView txt_titulo_filtro;
    ImageButton btn_filtro_fecha;
    EditText txt_fecha_filtro;
    ArrayAdapter adapterCategorias;
    Button btn_cancelar, btn_filtar;
    LinearLayout linear_filtro_calendario;
    ImageView searchMarker;

    String markerSelect = "";
    RutaRef rutaRefSitio = null;

    int[] itensCategoriaVistos = {0, 1, 2, 3, 4, 5, 6};

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();

    PlaceAutocompleteFragment autocompleteFragment;

    MultiSelectionSpinner spinner_categoria_multi;
    Location myLocation;

    LocationManager locationManager;

    public FragmentMapa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mapa, container, false);
        dialogo_filtro = getActivity().getLayoutInflater().inflate(R.layout.dialogo_filtro, null);

        imageCard = (ImageView) view.findViewById(R.id.img_card_detail);
        NameCard = (TextView) view.findViewById(R.id.text_name);
        DirectionCard = (TextView) view.findViewById(R.id.text_direction);
        RatingCard = (TextView) view.findViewById(R.id.text_rating);

        mMapView = (MapView) view.findViewById(R.id.frag_mapa);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        cardView = (RelativeLayout) view.findViewById(R.id.RLCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                switch (filtroSitio) {
                    case "lugar":
                        intent = new Intent(getActivity(), LugarDetalle.class);
                        break;
                    case "evento":
                        if (rutaRefSitio != null) {
                            rutaRefSitio.getdatabaseReference().removeEventListener(rutaRefSitio.getvalueEventListener());
                            rutaRefSitio = null;
                        }
                        limpiarMapa();
                        intent = new Intent(getActivity(), EventoDetalle.class);
                        break;
                    case "promocion":
                        intent = new Intent(getActivity(), PromoDetalle.class);
                        break;
                }
                fade_out();
                intent.putExtra("NombreSitio", NameCard.getText().toString());
                intent.putExtra("DireccionSitio", DirectionCard.getText().toString());
                intent.putExtra("RatingSitio", RatingCard.getText().toString());
                Comunicador.setContextMapa(getActivity());
                Comunicador.setGoogleMap(miMapa);
                startActivity(intent);
            }
        });

        // FAB MENU
        fab_menu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        fabLugares = (FloatingActionButton) view.findViewById(R.id.fabLugares);
        fabEventos = (FloatingActionButton) view.findViewById(R.id.fabEventos);
        fabPromociones = (FloatingActionButton) view.findViewById(R.id.fabPromociones);

        searchMarker = (ImageView) view.findViewById(R.id.searchMarker);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment);
        if (!getActivity().isFinishing()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        fabLugares.setColorNormal(Color.parseColor("#F44336"));
        fabLugares.setColorPressed(Color.parseColor("#D32F2F"));

        fabEventos.setColorNormal(Color.parseColor("#F44336"));
        fabEventos.setColorPressed(Color.parseColor("#D32F2F"));

        fabPromociones.setColorNormal(Color.parseColor("#F44336"));
        fabPromociones.setColorPressed(Color.parseColor("#D32F2F"));

        fabLugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_menu.collapse();
                filtroSitio = "lugar";
                mostrarFiltro("Filtro de Lugares", 1);
            }
        });

        fabEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_menu.collapse();
                filtroSitio = "evento";
                mostrarFiltro("Filtro de Eventos", 0);
            }
        });

        fabPromociones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_menu.collapse();
                filtroSitio = "promocion";
                mostrarFiltro("Filtro de Promociones", 0);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Localización GPS apagada")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void ubicarCamaraMapa(){
        if (!checkLocation()){
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            myLocation = locationManager.getLastKnownLocation(provider);
            if (myLocation != null){
                miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15f));
                miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17f));
                latitud = myLocation.getLatitude();
                longitud = myLocation.getLongitude();
                Comunicador.setMiPosicion(new LatLng(latitud, longitud));
            }
            miMapa.setMyLocationEnabled(true);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        miMapa = googleMap;
        miMapa.getUiSettings().setZoomControlsEnabled(true);
        miMapa.getUiSettings().setCompassEnabled(true);
        miMapa.getUiSettings().setMyLocationButtonEnabled(true);
        miMapa.getUiSettings().setAllGesturesEnabled(true);
        miMapa.getUiSettings().setMapToolbarEnabled(true);

        ubicarCamaraMapa();

        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        criteria = new Criteria();

        // Get the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);

        myLocation = locationManager.getLastKnownLocation(provider);

        miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15f));
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17f));

        // Get latitude of the current location // Get longitude of the current location
        latitud = myLocation.getLatitude();
        longitud = myLocation.getLongitude();

        Comunicador.setMiPosicion(new LatLng(latitud, longitud));*/

        miMapa.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (cardView.getVisibility() == View.VISIBLE) {
                    fade_out();
                }
                searchMarker.setVisibility(View.GONE);
            }
        });

        miMapa.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                searchMarker.setVisibility(View.GONE);
                if (cardView.getVisibility() == View.VISIBLE) {
                    fade_out();
                }
                return false;
            }
        });

        miMapa.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });


        //empresas = new ArrayList<>();
        //tipos.add("business");
        //tipos.add("clothings");
        //tipos.add("hotels");
        //tipos.add("shopping");

        tipos.add("deporte");
        tipos.add("restaurante");
        tipos.add("religion");
        tipos.add("rumba");
        tipos.add("cultura");
        tipos.add("musica");
        tipos.add("ropa");

        filtrarSitios();

        /*for (final String t : tipos) {
            final DatabaseReference EmpresasTipoRef = database.getReference(
                    FirebaseReferences.EMPRESAS_REFERENCE)
                    .child(FirebaseReferences.TIPOS_REFERENCE)
                    .child(t);
            EmpresasTipoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GeoPunto g = snapshot.getValue(GeoPunto.class);
                        Empresa e = new Empresa();
                        e.setKey(snapshot.getKey());
                        e.setGeoPunto(g);
                        empresas.add(e);

                        final int id = getResources().getIdentifier("icon_lugar_" + t, "drawable", "net.electrosoftware.myapp2");

                        e.setMarker(miMapa.addMarker(new MarkerOptions()
                                .position(new LatLng(g.getLat(), g.getLng()))
                                .title(e.getKey())
                                .icon(BitmapDescriptorFactory.fromResource(id))));
                        //BitmapDescriptorFactory
                        //.fromBitmap(resizedBitmap("map-icons/" + t + ".png", 80, 100)))));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        final DatabaseReference EmpresaInfoCortaRef = database.getReference(
                FirebaseReferences.EMPRESAS_REFERENCE).child(FirebaseReferences.INFOCORTA_REFERENCE);
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Evento ev = dataSnapshot.getValue(Evento.class);
                if (ev != null) {
                    //Toast.makeText(getActivity(), ic.getNombre(), Toast.LENGTH_SHORT).show();

                    imageCard.setImageResource(R.drawable.loading);
                    if (ev.getRutaFoto() != null && !(ev.getRutaFoto().equalsIgnoreCase("Sin imagen"))) {
                        StorageReference imagesRef = storage.getReference("foto sitios/" + ev.getRutaFoto());
                        imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imageCard.setImageBitmap(b);
                                // Use the bytes to display the image
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                imageCard.setImageResource(R.drawable.no_image_found);
                            }
                        });
                    } else {
                        imageCard.setImageResource(R.drawable.no_image_found);
                    }

                    NameCard.setText(ev.getNombre());
                    DirectionCard.setText(ev.getTipo());
                    RatingCard.setText(ev.getTelefono());

                } else {
                    imageCard.setImageResource(R.drawable.no_image_found);
                    NameCard.setText("Lo sentimos, no tenemos información de este punto");
                    DirectionCard.setText("X_X");
                    RatingCard.setText("0");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };*/

        miMapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!(marker.getTitle().equalsIgnoreCase(markerSelect))) {
                    markerSelect = marker.getTitle();
                    if (rutaRefSitio != null) {
                        rutaRefSitio.getdatabaseReference().removeEventListener(rutaRefSitio.getvalueEventListener());
                        rutaRefSitio = null;
                    }
                    rutaRefSitio = new RutaRef();
                    rutaRefSitio.setdatabaseReference(database.getReference(FirebaseReferences.SITIO_REFERENCE).child(markerSelect));

                    for (Sitio s : Sitios) {
                        if (s.getMarker().getTitle().equalsIgnoreCase(marker.getTitle())) {
                            rutaRefSitio.setvalueEventListener(
                                    rutaRefSitio.getdatabaseReference().addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    switch (filtroSitio) {
                                                        case "lugar":
                                                            cargarInfoTargetaLugar(dataSnapshot);
                                                            break;
                                                        case "evento":
                                                            cargarInfoTargetaEvento(dataSnapshot);
                                                            break;
                                                        case "promocion":
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            }));
                        }
                    }
                }

                if (cardView.getVisibility() == View.GONE) {
                    fade_in();
                }

                searchMarker.setVisibility(View.GONE);
                return true;
            }
        });

        adapterCategorias = ArrayAdapter.createFromResource(getActivity(), R.array.Categorias, android.R.layout.simple_spinner_item);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(-79.85749609375, -5.100027762345325), new LatLng(-66.59973828124998, 12.737772434600243)));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17f));
                searchMarker.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void limpiarMapa() {
        for (Sitio s : Sitios) {
            s.getMarker().remove();
        }
        Sitios.clear();

        for (RutaRef rr : rutasReferenciadas) {
            rr.getdatabaseReference().removeEventListener(rr.getvalueEventListener());
        }
        rutasReferenciadas.clear();
    }

    public void cargarInfoTargetaEvento(DataSnapshot dataSnapshot){
        try {
            Evento ev = dataSnapshot.getValue(Evento.class);
            Comunicador.setMiPosicionDestino(new LatLng(ev.getLat(), ev.getLng()));
            Comunicador.setMiPosicionDestino(new LatLng(ev.getLat(), ev.getLng()));
            if (ev != null) {
                //Toast.makeText(getActivity(), ic.getNombre(), Toast.LENGTH_SHORT).show();
                imageCard.setImageResource(R.drawable.loading);
                if (ev.getRutaFoto() != null && !(ev.getRutaFoto().equalsIgnoreCase("Sin imagen"))) {
                    StorageReference imagesRef = storage.getReference("foto sitios/" + filtroSitio + "/" + ev.getRutaFoto());
                    imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageCard.setImageBitmap(b);
                            // Use the bytes to display the image
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            imageCard.setImageResource(R.drawable.no_image_found);
                        }
                    });
                } else {
                    imageCard.setImageResource(R.drawable.no_image_found);
                }

                cardView.setClickable(true);
                Comunicador.setObjeto1(imageCard);
                Comunicador.setIdEvento(markerSelect);
                Comunicador.setEvento(ev);



                NameCard.setText(ev.getNombre());
                DirectionCard.setText(ev.getDireccion());
                RatingCard.setText(ev.getAsistentes() + "");

            } else {
                cardView.setClickable(false);
                imageCard.setImageResource(R.drawable.no_image_found);
                NameCard.setText("Lo sentimos, no tenemos información de este punto");
                DirectionCard.setText("X_X");
                RatingCard.setText("0");
            }
        }catch (Exception e){
            cardView.setClickable(false);
            imageCard.setImageResource(R.drawable.no_image_found);
            NameCard.setText("Lo sentimos, no tenemos información de este punto");
            DirectionCard.setText("X_X");
            RatingCard.setText("0");
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void cargarInfoTargetaLugar(DataSnapshot dataSnapshot){
        try {

            Lugar lg = dataSnapshot.getValue(Lugar.class);
            Comunicador.setMiPosicionDestino(new LatLng(lg.getLat(), lg.getLng()));
            if (lg != null) {
                //Toast.makeText(getActivity(), ic.getNombre(), Toast.LENGTH_SHORT).show();
                imageCard.setImageResource(R.drawable.loading);
                if (lg.getRutaFoto() != null && !(lg.getRutaFoto().equalsIgnoreCase("Sin imagen"))) {
                    StorageReference imagesRef = storage.getReference("foto sitios/" + filtroSitio + "/" + lg.getRutaFoto());
                    imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageCard.setImageBitmap(b);
                            // Use the bytes to display the image
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            imageCard.setImageResource(R.drawable.no_image_found);
                        }
                    });
                } else {
                    imageCard.setImageResource(R.drawable.no_image_found);
                }

                cardView.setClickable(true);
                Comunicador.setObjeto1(imageCard);
                Comunicador.setIdEvento(markerSelect);
                Comunicador.setLugar(lg);



                NameCard.setText(lg.getNombre());
                DirectionCard.setText(lg.getDireccion());
                RatingCard.setText("");

            } else {
                cardView.setClickable(false);
                imageCard.setImageResource(R.drawable.no_image_found);
                NameCard.setText("Lo sentimos, no tenemos información de este punto");
                DirectionCard.setText("X_X");
                RatingCard.setText("0");
            }
        }catch (Exception e){
            cardView.setClickable(false);
            imageCard.setImageResource(R.drawable.no_image_found);
            NameCard.setText("Lo sentimos, no tenemos información de este punto");
            DirectionCard.setText("X_X");
            RatingCard.setText("0");
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void filtrarSitios() {

        limpiarMapa();
        for (final String t : tipos) {
            final DatabaseReference sitiosRef;
            if (filtroSitio.equalsIgnoreCase("lugar")) {
                sitiosRef = database.getReference(
                        FirebaseReferences.FILTRO_REFERENCE)
                        .child(filtroSitio)
                        .child(t);
            } else {
                sitiosRef = database.getReference(
                        FirebaseReferences.FILTRO_REFERENCE)
                        .child(filtroSitio).child(fechaFiltro)
                        .child(t);
            }

            ValueEventListener VEL = sitiosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GeoPunto g = snapshot.getValue(GeoPunto.class);
                        Sitio e = new Sitio();
                        e.setKey(snapshot.getKey());
                        e.setGeoPunto(g);
                        Sitios.add(e);

                        Comunicador.setMiPosicionDestino(new LatLng(g.getLat(), g.getLng()));
                        final int id = getResources().getIdentifier("icon_" + filtroSitio + "_" + t, "drawable", "net.electrosoftware.myapp2");

                        e.setMarker(miMapa.addMarker(new MarkerOptions()
                                .position(new LatLng(g.getLat(), g.getLng()))
                                .title(e.getKey())
                                .icon(BitmapDescriptorFactory.fromResource(id))));
                        //BitmapDescriptorFactory
                        //.fromBitmap(resizedBitmap("map-icons/" + t + ".png", 80, 100)))));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            rutasReferenciadas.add(new RutaRef(sitiosRef, VEL));

        }

        /*
        switch (filtro){
            case "lugares":
                for (final String t : tipos) {
                    final DatabaseReference sitiosRef = database.getReference(
                            FirebaseReferences.FILTRO_REFERENCE)
                            .child(filtro)
                            .child(t);
                    ValueEventListener VEL = sitiosRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                GeoPunto g = snapshot.getValue(GeoPunto.class);
                                Sitio e = new Sitio();
                                e.setKey(snapshot.getKey());
                                e.setGeoPunto(g);
                                Lugares.add(e);

                                final int id = getResources().getIdentifier("icon_lugar_" + t, "drawable", "net.electrosoftware.myapp2");

                                e.setMarker(miMapa.addMarker(new MarkerOptions()
                                        .position(new LatLng(g.getLat(), g.getLng()))
                                        .title(e.getKey())
                                        .icon(BitmapDescriptorFactory.fromResource(id))));
                                //BitmapDescriptorFactory
                                //.fromBitmap(resizedBitmap("map-icons/" + t + ".png", 80, 100)))));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    rutasReferenciadas.add(new RutaRef(sitiosRef, VEL));

                }
                break;
            case "eventos":
                break;
            case "promos":
                break;

        }*/
    }

    public Bitmap resizedBitmap(String archiveName, int x, int y) {
        InputStream is = null;
        AssetManager mngr = getActivity().getAssets();
        try {
            is = mngr.open(archiveName);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            //BitmapFactory is an Android graphics utility for images
            bitmap = BitmapFactory.decodeStream(is);

        } finally {
            //Always clear and close
            try {
                is.close();
                is = null;
            } catch (IOException e) {
            }
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, x, y, false);
        return resizedBitmap;
    }

    public void fade_in() {
        cardView.setVisibility(View.VISIBLE);
        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        cardView.startAnimation(animation1);

        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
        fab_menu.startAnimation(animation2);
        fab_menu.setVisibility(View.GONE);
    }

    public void fade_out() {
        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
        cardView.startAnimation(animation1);
        cardView.setVisibility(View.GONE);

        fab_menu.setVisibility(View.VISIBLE);
        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        fab_menu.startAnimation(animation2);
    }

    public void mostrarFiltro(String nombreFiltro, final int ocultarSpiner) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_filtro);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        spinner_categoria_multi = (MultiSelectionSpinner) dialog.findViewById(R.id.spinner_categoria_multi);
        String[] array = {"Restaurante y Gastronomía", "Rumba, Bares y Discotecas", "Arte y Cultura", "Música y Conciertos", "Deporte y Salud", "Ropa y Accesorios", "Religión"};
        spinner_categoria_multi.setItems(array);
        spinner_categoria_multi.setSelection(itensCategoriaVistos);

        txt_titulo_filtro = (TextView) dialog.findViewById(R.id.txt_titulo_filtro);
        btn_filtro_fecha = (ImageButton) dialog.findViewById(R.id.btn_filtro_fecha);
        txt_fecha_filtro = (EditText) dialog.findViewById(R.id.txt_fecha_filtro);
        //spinner_categoria = (MaterialSpinner) dialog.findViewById(R.id.spinner_categoria);
        btn_cancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
        btn_filtar = (Button) dialog.findViewById(R.id.btn_filtar);
        linear_filtro_calendario = (LinearLayout) dialog.findViewById(R.id.linear_filtro_calendario);


        txt_titulo_filtro.setText(nombreFiltro);
        //spinner_categoria.setAdapter(adapterCategorias);

        spinner_categoria_multi.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {
                //Toast.makeText(getActivity(),"aaaaaaaaaaaa", Toast.LENGTH_LONG).show();

                tipos.clear();
                //itensCategoriaVistos = new int[indices.size()];

                for (Integer i : indices) {
                    //itensCategoriaVistos[i] = i;
                    String tipo = "";
                    switch (i) {
                        case 0:
                            tipo = "restaurante";
                            break;
                        case 1:
                            tipo = "rumba";
                            break;
                        case 2:
                            tipo = "cultura";
                            break;
                        case 3:
                            tipo = "musica";
                            break;
                        case 4:
                            tipo = "deporte";
                            break;
                        case 5:
                            tipo = "ropa";
                            break;
                        case 6:
                            tipo = "religion";
                            break;
                    }
                    tipos.add(tipo);
                }
                Toast.makeText(getActivity(), tipos.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void selectedStrings(List<String> strings) {
                //Toast.makeText(getActivity(), strings.toString(), Toast.LENGTH_LONG).show();
            }
        });

        if (ocultarSpiner == 1) {
            linear_filtro_calendario.setVisibility(View.GONE);
        }

        btn_filtro_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                txt_fecha_filtro.setText(new StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth).append("/").append(year).append(" "));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                dpd.show();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });

        btn_filtar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ocultarSpiner == 0) {
                    fechaFiltro = txt_fecha_filtro.getText().toString().replace("/", "-").replace(" ", "");
                }
                filtrarSitios();
                dialog.dismiss();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
