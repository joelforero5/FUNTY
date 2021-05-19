package net.electrosoftware.myapp2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.activityes.EventoDetalle;
import net.electrosoftware.myapp2.activityes.LugarDetalle;
import net.electrosoftware.myapp2.activityes.MainActivity;
import net.electrosoftware.myapp2.clasesbases.Imageutils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class FragmentUsuario extends Fragment {
    View view;

    ImageView imv_usuario_foto;
    EditText et_usuario_email, et_usuario_nombreusuario, et_usuario_contrasena;
    Button btn_usuario_lugares, btn_usuario_eventos, btn_usuario_promociones, btn_usuario_actualizar;
    ImageButton btn_usuario_editar_foto;
    Imageutils imageutils;
    //For Image Attachment
    private Bitmap bitmap;
    private String file_name;

    // ACTUALIZAR
    TextView txt_dial_usuario_tittle;
    EditText et_dial_usuario_email, et_dial_usuario_nombreusuario, et_dial_usuario_contrasena, et_dial_usuario_confir_contrasena;
    Button btn_dial_usuario_cancelar, btn_dial_usuario_aceptar;

    public FragmentUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_usuario, null);

        imv_usuario_foto = (CircleImageView) view.findViewById(R.id.imv_usuario_foto);
        imv_usuario_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Foto de Perfil", Toast.LENGTH_SHORT).show();
                CharSequence options[] = new CharSequence[]{"Cámara", "Galería"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Agregar Imagen desde...");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                                break;
                            case 1:
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        et_usuario_email = (EditText) view.findViewById(R.id.et_usuario_email);
        et_usuario_nombreusuario = (EditText) view.findViewById(R.id.et_usuario_nombreusuario);
        et_usuario_contrasena = (EditText) view.findViewById(R.id.et_usuario_contrasena);

        btn_usuario_actualizar = (Button) view.findViewById(R.id.btn_usuario_actualizar);
        btn_usuario_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatos("Mis Datos");
            }
        });

        return view;
    }

    public void actualizarDatos(String titulo) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_actualizar_perfil);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_dial_usuario_tittle = (TextView) dialog.findViewById(R.id.txt_dial_usuario_tittle);

        et_dial_usuario_email = (EditText) dialog.findViewById(R.id.et_dial_usuario_email);
        et_dial_usuario_nombreusuario = (EditText) dialog.findViewById(R.id.et_dial_usuario_nombreusuario);
        et_dial_usuario_contrasena = (EditText) dialog.findViewById(R.id.et_dial_usuario_contrasena);
        et_dial_usuario_confir_contrasena = (EditText) dialog.findViewById(R.id.et_dial_usuario_confir_contrasena);

        btn_dial_usuario_cancelar = (Button) dialog.findViewById(R.id.btn_dial_usuario_cancelar);
        btn_dial_usuario_aceptar = (Button) dialog.findViewById(R.id.btn_dial_usuario_aceptar);

        txt_dial_usuario_tittle.setText(titulo);

        btn_dial_usuario_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
            }
        });

        btn_dial_usuario_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_dial_usuario_contrasena.getText().toString().equalsIgnoreCase(et_dial_usuario_confir_contrasena.getText().toString())) {
                    Toast.makeText(getActivity(), "Datos actualizados correctamente, inicia sesión de nuevo", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialog.cancel();
                } else {
                    et_dial_usuario_confir_contrasena.setError("Contraseñas no coinciden");
                    Toast.makeText(getActivity(), "Las contraseñas no coinciden, verifica esto para continuar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    /*Uri selectedImage = data.getData();
                    imv_usuario_foto.setImageURI(selectedImage);*/
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imv_usuario_foto.setBackground(null);
                    }
                    imv_usuario_foto.setImageBitmap(imageBitmap);
                    imv_usuario_foto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imv_usuario_foto.setImageURI(selectedImage);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        imv_usuario_foto.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);

    }
}
