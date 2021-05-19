package net.electrosoftware.myapp2.activityes;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.fragments.FragmentMapa;
import net.electrosoftware.myapp2.fragments.FragmentListaSitios;
import net.electrosoftware.myapp2.fragments.FragmentMisFavoritos;
import net.electrosoftware.myapp2.fragments.FragmentUsuario;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    TextView txt_encabezado_nombre_usuario_Consumidor, txt_encabezado_correo_Consumidor;
    CircleImageView imv_encabezado_foto_perfil_Consumidor;
   // final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //final FirebaseStorage storage = FirebaseStorage.getInstance();

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_MAP = "MAPA";
    private static final String TAG_PROFILE = "PERFIL";
    private static final String TAG_EVENT = "EVENTOS";
    private static final String TAG_FAVORITE = "FAVORITOS";
    public static String CURRENT_TAG = TAG_MAP;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().replace(R.id.frag_main, new FragmentMapa()).commit();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        txt_encabezado_nombre_usuario_Consumidor = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.txt_encabezado_nombre_usuario_Consumidor);
        txt_encabezado_correo_Consumidor = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_encabezado_correo_Consumidor);
        imv_encabezado_foto_perfil_Consumidor = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.imv_encabezado_foto_perfil_Consumidor);

        mHandler = new Handler();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAP;
            loadHomeFragment();
        }

    }

    /*
     * Returns respected fragment that user
     * selected from navigation menu
     */

    private void loadHomeFragment() {
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_main, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // mapa
                FragmentMapa mapFragment = new FragmentMapa();
                return mapFragment;

            case 1:
                // perfil
                FragmentUsuario usuarioFragment = new FragmentUsuario();
                return usuarioFragment;
            case 2:
                // eventos
                Comunicador.setTipoSitio("evento");
                FragmentListaSitios eventosFragment = new FragmentListaSitios();
                return eventosFragment;
            case 3:
                // favoritos
                FragmentMisFavoritos favoritosFragment = new FragmentMisFavoritos();
                return favoritosFragment;
            default:
                return new FragmentMapa();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_mapa:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAP;
                        break;
                    case R.id.nav_perfil:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_eventos:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_EVENT;
                        break;
                    case R.id.nav_favoritos:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_FAVORITE;
                        break;
                    case R.id.nav_cerrar:
                        // cerrar sesión
                        new AlertDialog.Builder(MainActivity.this).setTitle("Cerrar Sesión")
                                .setMessage("¿Está seguro de cerrar sesión?")
                                .setIcon(R.mipmap.ic_launcher)
                                .setCancelable(true)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            txt_encabezado_nombre_usuario_Consumidor.setText(Comunicador.getUsuario().getNombre());
            txt_encabezado_correo_Consumidor.setText(user.getEmail());
            String Foto = Comunicador.getUsuario().getRutaFoto();
            if (!Foto.equalsIgnoreCase("Sin imagen")) {
                StorageReference fotoRef = storage.getReference().child("foto usuarios/" + Foto);
                fotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imv_encabezado_foto_perfil_Consumidor.setImageBitmap(b);

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

            /*
            userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usuario = dataSnapshot.getValue(User.class);
                    txt_encabezado_nombre_usuario_Consumidor.setText(usuario.getNombre());
                    txt_encabezado_correo_Consumidor.setText(user.getEmail());
                    String Foto = usuario.getRutaFoto();
                    if(!Foto.equalsIgnoreCase("Sin imagen")){
                        StorageReference fotoRef = storage.getReference().child("foto usuarios/"+Foto);
                        fotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imv_encabezado_foto_perfil_Consumidor.setImageBitmap(b);

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
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            */


       /* } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }*/


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAP;
                loadHomeFragment();
                return;
            }
        }

        //super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Toast.makeText(MainActivity.this, "No salir de la app", Toast.LENGTH_SHORT).show();
        return super.onKeyDown(keyCode, event);
    }
}
