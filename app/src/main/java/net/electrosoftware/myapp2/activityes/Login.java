package net.electrosoftware.myapp2.activityes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.Global;
import net.electrosoftware.myapp2.clasesbases.User;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.FirebaseReferences;
import net.electrosoftware.myapp2.firebaseClases.Usuario;

import java.io.ByteArrayOutputStream;

public class Login extends AppCompatActivity {
    TextView txt_login_app_name;
    Button btn_login_registrarse, btn_login_ingresar;
    EditText et_correo_usuario, et_login_contrasena;
    TextView txt_login_recordar_contrasena;

    // DIALOGO RECORDAR PASS
    TextView txt_dial_titulo_recordar;
    EditText et_dial_recordar_correo;
    Button btn_dial_recordar_cancelar, btn_dial_recordar_aceptar;

    byte[] data=null;
    ProgressDialog PDInicioSesion = null;
    Usuario usuario;
    //int correoLength, contrasenaLength;
    String correoText, contrasenaText;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final DatabaseReference userRef = database.getReference(FirebaseReferences.USUARIOS_REFERENCE);

    ValueEventListener cargarUsuarioEvent = null;

    //boolean flagTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_correo_usuario = (EditText) findViewById(R.id.et_correo_usuario);
        et_login_contrasena = (EditText) findViewById(R.id.et_login_contrasena);
        txt_login_recordar_contrasena = (TextView) findViewById(R.id.txt_login_recordar_contrasena);

        btn_login_ingresar = (Button) findViewById(R.id.btn_login_ingresar);
        btn_login_registrarse = (Button) findViewById(R.id.btn_login_registrarse);
        txt_login_app_name = (TextView) findViewById(R.id.txt_login_app_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        txt_login_app_name.setTypeface(custom_font);
        et_login_contrasena.setTypeface(custom_font);
        btn_login_registrarse.setTypeface(custom_font);
        btn_login_ingresar.setTypeface(custom_font);
        et_correo_usuario.setTypeface(custom_font);

        //correoLength = et_correo_usuario.getText().toString().length();
        //contrasenaLength = et_login_contrasena.getText().toString().length();

        correoText = et_correo_usuario.getText().toString();
        contrasenaText = et_login_contrasena.getText().toString();

        btn_login_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });

        btn_login_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Logueo().execute();
                validarUsuario();

            }
        });
        txt_login_recordar_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordarPass("RECORDAR CONTRASEÑA");
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        if (PDInicioSesion != null){
            Login.this.PDInicioSesion.dismiss();
            userRef.removeEventListener(cargarUsuarioEvent);
        }


    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }*/

    public void recordarPass(String texto) {
        final Dialog dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_recordar_pass);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_dial_titulo_recordar = (TextView) dialog.findViewById(R.id.txt_dial_titulo_recordar);
        et_dial_recordar_correo = (EditText) dialog.findViewById(R.id.et_dial_recordar_correo);

        btn_dial_recordar_cancelar = (Button) dialog.findViewById(R.id.btn_dial_recordar_cancelar);
        btn_dial_recordar_aceptar = (Button) dialog.findViewById(R.id.btn_dial_recordar_aceptar);

        txt_dial_titulo_recordar.setText(texto);

        btn_dial_recordar_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });

        btn_dial_recordar_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (et_dial_recordar_correo.getText().toString().length() > 0) {

                    auth.sendPasswordResetEmail(et_dial_recordar_correo.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Se ha enviado una solicitud de reinicio de contraseña a tu correo, revísalo", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    dialog.dismiss();
                    dialog.cancel();
                } else {
                    Toast.makeText(Login.this, "Es necesario el correo", Toast.LENGTH_LONG).show();
                }


            }
        });

        dialog.show();
    }

    private void validarUsuario(){

        correoText = et_correo_usuario.getText().toString();
        contrasenaText = et_login_contrasena.getText().toString();
        Boolean validador = false;
        String perfil = "";
        User user = null;
        if (correoText.length() > 0 && contrasenaText.length() > 0) {
            Global lista_usuarios = new Global();
            if (lista_usuarios.getListaUsers().size()!=0) {
                for (int i = 0; i < lista_usuarios.getListaUsers().size(); i++) {
                    if (correoText.equals(lista_usuarios.getListaUsers().get(i).getCorreo_usuario())&&contrasenaText.equals(lista_usuarios.getListaUsers().get(i).getContraseña())){
                        validador=true;
                        perfil = lista_usuarios.getListaUsers().get(i).getPerfil();
                        user=new User(Global.getListaUsers().get(i).getCorreo_usuario(),Global.getListaUsers().get(i).getNombrePerfil_usuario(),Global.getListaUsers().get(i).getPerfil(),Global.getListaUsers().get(i).getContraseña());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Global.getListaUsers().get(i).getFoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        data = baos.toByteArray();

                    }


                }

                if (validador){

                    if (perfil.equals("Consumidor")){
                        Intent intent = new Intent(Login.this,MainActivity.class);
                        intent.putExtra("usuario",user);
                        intent.putExtra("foto",data);
                        startActivity(intent);

                        finish();
                    }else if (perfil.equals("Empresario")){
                        Intent intent = new Intent(Login.this,Empresarios.class);
                        intent.putExtra("usuario",user);
                        intent.putExtra("foto",data);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    Toast.makeText(Login.this,"User ò Contraseña incorrectos",Toast.LENGTH_SHORT).show();
                }
            }
        }


        /*PDInicioSesion = ProgressDialog.show(Login.this, "Iniciando Sesión", "Iniciando Sesión, espera un momento...", true, true);
        PDInicioSesion.setCancelable(false);

        correoText = et_correo_usuario.getText().toString();
        contrasenaText = et_login_contrasena.getText().toString();

        if (correoText.length() > 0 && contrasenaText.length() > 0) {
            mAuth.signInWithEmailAndPassword(correoText, contrasenaText)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {

                                user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    cargarUsuarioEvent = userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            user = dataSnapshot.getValue(User.class);
                                            Comunicador.setUsuario(user);
                                            //flagTask = true;
                                            Login.this.PDInicioSesion.dismiss();
                                            if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Consumidor")) {
                                                Toast.makeText(Login.this, "Bienvenido " + user.nombre, Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Login.this, MainActivity.class));
                                            } else if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Empresario")) {
                                                Toast.makeText(Login.this, "Bienvenido " + user.nombre, Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Login.this, Empresarios.class));
                                            }else{
                                                Toast.makeText(Login.this, "No encontramos la información de su cuenta, si el problema continua comuniquece con nosotros", Toast.LENGTH_LONG).show();
                                            }
                                            //Login.this.PDInicioSesion.dismiss();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Login.this.PDInicioSesion.dismiss();
                                            Toast.makeText(Login.this, "Fallo al intentar validar los datos. " + error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Login.this.PDInicioSesion.dismiss();
                                    Toast.makeText(Login.this, "No se ha establecido conexión con el servicio, por favor revisa tu conexión a internet", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Login.this.PDInicioSesion.dismiss();
                                Toast.makeText(Login.this, "Correo o contraseña incorrecta", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            //startActivity(new Intent(Login.this, MainActivity.class));
        } else {
            Login.this.PDInicioSesion.dismiss();
            Toast.makeText(Login.this, "Correo o contraseña vacios", Toast.LENGTH_LONG).show();
        }*/


    }

    private class Logueo extends AsyncTask<String, Float, Integer> {

        protected void onPreExecute() {
            //flagTask = false;
            PDInicioSesion = ProgressDialog.show(Login.this, "Iniciando Sesión", "Iniciando Sesión, espera un momento...", true, true);
            PDInicioSesion.setCancelable(false);
        }

        protected Integer doInBackground(String... parametros) {

            //FirebaseAuth.AuthStateListener mAuthListener;
            correoText = et_correo_usuario.getText().toString();
            contrasenaText = et_login_contrasena.getText().toString();

            if (correoText.length() > 0 && contrasenaText.length() > 0) {
                mAuth.signInWithEmailAndPassword(correoText, contrasenaText)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (task.isSuccessful()) {

                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {


                                        cargarUsuarioEvent = userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                usuario = dataSnapshot.getValue(Usuario.class);
                                                Comunicador.setUsuario(usuario);
                                                //flagTask = true;
                                                if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Consumidor")) {
                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                } else if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Empresario")) {
                                                    startActivity(new Intent(Login.this, Empresarios.class));
                                                }else{
                                                    Toast.makeText(Login.this, "No encontramos la información de su cuenta, si el problema continua comuniquece con nosotros", Toast.LENGTH_LONG).show();
                                                }
                                                //Login.this.PDInicioSesion.dismiss();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                //Log.w(TAG, "Failed to read value.", error.toException());
                                            }
                                        });
                                    } else {
                                        Login.this.PDInicioSesion.dismiss();
                                        Toast.makeText(Login.this, "No se ha establecido conexión con el servicio, por favor revisa tu conexión a internet", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Login.this.PDInicioSesion.dismiss();
                                    Toast.makeText(Login.this, "Correo o contraseña incorrecta", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                //startActivity(new Intent(Login.this, MainActivity.class));
            } else {
                Login.this.PDInicioSesion.dismiss();
                Toast.makeText(Login.this, "Correo o contraseña incorrecta", Toast.LENGTH_LONG).show();
            }
            return 0;
        }

        protected void onPostExecute(Integer bytes) {

            /*Login.this.PDInicioSesion.dismiss();
            if (flagTask) {

                if (Comunicador.getUsuario() != null) {
                    if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Consumidor")) {
                        startActivity(new Intent(Login.this, MainActivity.class));
                    } else if (Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Empresario")) {
                        startActivity(new Intent(Login.this, Empresarios.class));
                    }else{
                        Toast.makeText(Login.this, "No encontramos la información de su cuenta, si el problema continua comuniquece con nosotros", Toast.LENGTH_LONG).show();
                    }
                }
            }*/



        }

    }
}
