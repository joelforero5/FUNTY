package net.electrosoftware.myapp2.activityes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.AppUtils;
import net.electrosoftware.myapp2.clasesbases.FetchAddressIntentService;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.Evento;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.Lugar;
import net.electrosoftware.myapp2.firebaseClases.Punto;
import net.electrosoftware.myapp2.firebaseClases.itemListaSitio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AgregarSitioMapa extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;
    private LatLng mCenterLatLong;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * The formatted location address.
     */

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    TextView txt_sitiomap_buscar;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    Button btn_sitiomap_cancelar, btn_sitiomap_guardar;

    public Bitmap bitmap;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_sitio_mapa);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        txt_sitiomap_buscar = (TextView) findViewById(R.id.txt_sitiomap_buscar);
        txt_sitiomap_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("La localización no está habilitada");
                dialog.setPositiveButton("Abrir configuración de localización", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancelar", null);
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Localización no admitida en este dispositivo", Toast.LENGTH_SHORT).show();
        }

        btn_sitiomap_cancelar = (Button) findViewById(R.id.btn_sitiomap_cancelar);
        btn_sitiomap_guardar = (Button) findViewById(R.id.btn_sitiomap_guardar);

        btn_sitiomap_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_sitiomap_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference SitiosRef = database.getReference(FirebaseReferences.SITIO_REFERENCE);
                LatLng pos = mMap.getCameraPosition().target;
                Punto p = new Punto(pos.latitude, pos.longitude);

                String rutaFoto = "";


                if (Comunicador.getObjeto1() instanceof Lugar) {
                    Lugar lugar = (Lugar) Comunicador.getObjeto1();
                    lugar.setLat(pos.latitude);
                    lugar.setLng(pos.longitude);
                    try {
                        lugar.setDireccion(LagLngToDir(pos.latitude, pos.longitude));
                    } catch (IOException e) {
                        e.printStackTrace();
                        lugar.setDireccion("");
                    }
                    String key = lugar.writeNewEvento(SitiosRef);
                    if (Comunicador.getTipoSitio() != null) {
                        final DatabaseReference ListaUserEventoRef = database.getReference(FirebaseReferences.LISTA_REFERENCE)
                                .child(user.getUid())
                                //.child(FirebaseReferences.EVENTO_REFERENCE)
                                .child(Comunicador.getTipoSitio())
                                .child(key);
                        itemListaSitio iten = new itemListaSitio(lugar.getNombre(), lugar.getTipo(), lugar.getRutaFoto());
                        iten.writeItemListaSitio(ListaUserEventoRef);

                        final DatabaseReference FiltroEventosTipoRef = database.getReference(FirebaseReferences.FILTRO_REFERENCE)
                                //.child(FirebaseReferences.EVENTO_REFERENCE)
                                .child(Comunicador.getTipoSitio())
                                .child(lugar.getTipo());
                        p.writePunto(FiltroEventosTipoRef, key);
                    }

                    guardarFoto(lugar.getRutaFoto(), Comunicador.getTipoSitio());
                    Toast.makeText(AgregarSitioMapa.this, "El Lugar se a registrado", Toast.LENGTH_SHORT).show();
                    AgregarLugar.agregarLugar.finish();


                } else if(Comunicador.getObjeto1() instanceof Evento) {
                    Evento evento = (Evento) Comunicador.getObjeto1();
                    evento.setLat(pos.latitude);
                    evento.setLng(pos.longitude);
                    try {
                        evento.setDireccion(LagLngToDir(pos.latitude, pos.longitude));
                    } catch (IOException e) {
                        e.printStackTrace();
                        evento.setDireccion("");
                    }
                    String key = evento.writeNewEvento(SitiosRef);
                    if (Comunicador.getTipoSitio() != null) {
                        final DatabaseReference ListaUserEventoRef = database.getReference(FirebaseReferences.LISTA_REFERENCE)
                                .child(user.getUid())
                                //.child(FirebaseReferences.EVENTO_REFERENCE)
                                .child(Comunicador.getTipoSitio())
                                .child(key);
                        itemListaSitio iten = new itemListaSitio(evento.getNombre(), evento.getTipo(), evento.getRutaFoto());
                        iten.writeItemListaSitio(ListaUserEventoRef);

                        final DatabaseReference FiltroEventosTipoRef = database.getReference(FirebaseReferences.FILTRO_REFERENCE)
                                //.child(FirebaseReferences.EVENTO_REFERENCE)
                                .child(Comunicador.getTipoSitio())
                                .child(evento.getFechaIni())
                                .child(evento.getTipo());
                        p.writePunto(FiltroEventosTipoRef, key);
                    }

                    guardarFoto(evento.getRutaFoto(), Comunicador.getTipoSitio());

                    Toast.makeText(AgregarSitioMapa.this, "El Evento se a registrado", Toast.LENGTH_SHORT).show();
                    AgregarEvento.agregarEvento.finish();
                }

                if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Consumidor")) {
                    startActivity(new Intent(AgregarSitioMapa.this, MainActivity.class));
                } else if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Empresario")) {
                    startActivity(new Intent(AgregarSitioMapa.this, Empresarios.class));
                }else{
                    Toast.makeText(AgregarSitioMapa.this, "No encontramos la información de su cuenta, si el problema continua comuniquece con nosotros", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AgregarSitioMapa.this, Login.class));
                }

                finish();
            }
        });



    }

    private void guardarFoto(String rutaFoto, String tipoSitio){
        bitmap = (Bitmap) Comunicador.getObjeto2();
        if (bitmap != null) {
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("foto sitios").child(tipoSitio).child(rutaFoto);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;
                mMap.clear();
                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
        Log.d(TAG, "Reaching map" + mMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;
            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).build();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            startIntentService(location);
        } else {
            Toast.makeText(getApplicationContext(), "Disuclpa, no se pudo crear mapa", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);
            ///displayAddressOutput();
            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }
        }
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        /*/  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
            //txt_sitiomap_buscar.setText(mAreaOutput+ "");
            //mLocationAddress.setText(mAddressOutput);
            //txt_sitiomap_buscar.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services no está disponible: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                LatLng latLong;
                latLong = place.getLatLng();
                //txt_sitiomap_buscar.setText(place.getName() + "");
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).build();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    public String LagLngToDir(double latitude, double longitude) throws IOException {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        /*String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();*/  // Only if available else return NULL

            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
}

