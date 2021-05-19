package net.electrosoftware.myapp2.activityes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.Global;
import net.electrosoftware.myapp2.clasesbases.Imageutils;
import net.electrosoftware.myapp2.clasesbases.User;

import java.io.File;

import fr.ganfra.materialspinner.MaterialSpinner;


public class Registro extends AppCompatActivity implements Imageutils.ImageAttachmentListener {
    EditText et_registro_contrasena, et_registro_contrasena_confir, /*et_registro_telefono,*/
            et_registro_email, et_registro_usuario;
    TextView txt_registro_app_name;
    Button btn_registro_registrarse;
    ImageButton btn_registro_foto;
    String perfil = "no";
    MaterialSpinner spinner_registro_Perfil;

    ArrayAdapter adapterPerfiles;

    CircleImageView imv_registro_foto;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //For Image Attachment
    Bitmap bitmap,imagen = null;
    String file_name = "Sin imagen";
    Imageutils imageutils;

    Global lista_usuarios = new Global();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et_registro_usuario = (EditText) findViewById(R.id.et_registro_usuario);
        et_registro_contrasena = (EditText) findViewById(R.id.et_registro_contrasena);
        et_registro_contrasena_confir = (EditText) findViewById(R.id.et_registro_contrasena_confir);
        et_registro_email = (EditText) findViewById(R.id.et_registro_email);
        //et_registro_telefono = (EditText) findViewById(R.id.et_registro_telefono);
        btn_registro_registrarse = (Button) findViewById(R.id.btn_registro_registrarse);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        et_registro_email.setTypeface(custom_font);
        //et_registro_telefono.setTypeface(custom_font);
        et_registro_contrasena.setTypeface(custom_font);
        et_registro_contrasena_confir.setTypeface(custom_font);
        et_registro_usuario.setTypeface(custom_font);
        btn_registro_registrarse.setTypeface(custom_font);

        adapterPerfiles = ArrayAdapter.createFromResource(Registro.this, R.array.Perfiles, android.R.layout.simple_spinner_item);
        spinner_registro_Perfil = (MaterialSpinner) findViewById(R.id.spinner_registro_Perfil);
        spinner_registro_Perfil.setAdapter(adapterPerfiles);

        spinner_registro_Perfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    perfil = spinner_registro_Perfil.getSelectedItem().toString();
                } else {
                    perfil = "no";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                perfil = "no";
            }
        });

        btn_registro_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!perfil.equalsIgnoreCase("no")) {
                    if (et_registro_usuario.getText().toString().length() > 0) {
                        if (et_registro_email.getText().toString().length() > 0) {
                            if (et_registro_contrasena.getText().toString().equalsIgnoreCase(et_registro_contrasena_confir.getText().toString())) {
                                if (bitmap != null) {
                                    User user = new User(et_registro_email.getText().toString(), et_registro_usuario.getText().toString(), perfil, et_registro_contrasena.getText().toString(),imagen);

                                    Global.getListaUsers().add(user);
                                    String nombre = Global.getListaUsers().get(0).getNombrePerfil_usuario();
                                }

                            }
                        }
                    }
                }






                //startActivity(new Intent(Registro.this, Empresarios.class));

                /*if (!perfil.equalsIgnoreCase("no")) {
                    if (et_registro_usuario.getText().toString().length() > 0) {
                        if (et_registro_email.getText().toString().length() > 0) {
                            if (et_registro_contrasena.getText().toString().equalsIgnoreCase(et_registro_contrasena_confir.getText().toString())) {
                                mAuth.createUserWithEmailAndPassword(et_registro_email.getText().toString(),
                                        et_registro_contrasena.getText().toString())
                                        .addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    if (bitmap != null) {
                                                        final FirebaseStorage storage = FirebaseStorage.getInstance();
                                                        StorageReference storageRef = storage.getReference("foto usuarios").child(file_name);


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

                                                    FirebaseUser userFire = mAuth.getCurrentUser();
                                                    final DatabaseReference UsuariosRef = database.getReference(FirebaseReferences.USUARIOS_REFERENCE);
                                                    User user = new User(perfil, et_registro_usuario.getText().toString(), file_name);
                                                    user.writeNewUser(UsuariosRef, userFire.getUid());
                                                    new AlertDialog.Builder(Registro.this)
                                                            //.setTitle("Confirmación")
                                                            .setMessage("Usted se a registrado con éxito")
                                                            .setIcon(R.mipmap.ic_launcher)
                                                            .setCancelable(true)
                                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    startActivity(new Intent(Registro.this, Login.class));
                                                                }
                                                            })
                                                            .show();


                                                    //finish();
                                                    //userFire.getUid();

                                                    //updateUI(user);
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                    String error = task.getException().toString();
                                                    if (error.indexOf("WEAK_PASSWORD") != -1) {
                                                        error = "La contraseña es muy débil";
                                                    } else if (error.indexOf("FirebaseAuthInvalidCredentialsException") != -1) {
                                                        error = "El correo no es válido";
                                                    } else if (error.indexOf("FirebaseAuthUserCollisionException") != -1) {
                                                        error = "Ya existe una cuenta asosciada al correo";
                                                    }
                                                    Toast.makeText(Registro.this, error, Toast.LENGTH_LONG).show();
                                                    //updateUI(null);
                                                }

                                                // ...
                                            }
                                        });


                            } else {
                                et_registro_contrasena_confir.setError("Las contraseñas do coinciden");
                                //Toast.makeText(Registro.this, "Las contraseñas no coinciden")
                            }
                        } else {
                            et_registro_email.setError("El correo es necesario");
                        }
                    } else {
                        et_registro_usuario.setError("Falta su nombre");
                    }
                } else {
                    Toast.makeText(Registro.this, "Debe seleccionar el perfil", Toast.LENGTH_LONG).show();
                }*/


                Toast.makeText(Registro.this,"User Creado correctamente",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        imageutils = new Imageutils(this);
        imv_registro_foto = (CircleImageView) findViewById(R.id.imv_registro_foto);
        imv_registro_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        startActivity(new Intent(Registro.this, Login.class));
        finish();
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        bitmap = file;
        file_name = filename;
        imv_registro_foto.setImageBitmap(file);
        imagen=file;
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);

    }
}
