package net.electrosoftware.myapp2.activityes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.Imageutils;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.Lugar;

import java.io.File;
import java.util.Calendar;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AgregarLugar extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    ArrayAdapter adapterCategorias;
    MaterialSpinner spinner_lugar_categoria;
    EditText et_lugar_nombre, et_lugar_telefono, et_lugar_horario, et_lugar_descripcion;

    Button btn_lugar_cancelar, btn_lugar_continuar;

    Calendar calendar;
    Toolbar mToolbar;

    CircleImageView imv_nuevoLugar_foto;

    Bitmap bitmap = null;
    String file_name = "Sin imagen";
    Imageutils imageutils;

    public static Activity agregarLugar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_agregar_lugar);

        agregarLugar = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbarCrearLugar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Mis Lugares");
        mToolbar.setTitleTextColor(Color.WHITE);

        et_lugar_nombre = (EditText) findViewById(R.id.et_lugar_nombre);
        et_lugar_telefono = (EditText) findViewById(R.id.et_lugar_telefono);

        imageutils = new Imageutils(this);
        imv_nuevoLugar_foto = (CircleImageView) findViewById(R.id.imv_nuevoLugar_foto);
        imv_nuevoLugar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        calendar = Calendar.getInstance();

        adapterCategorias = ArrayAdapter.createFromResource(AgregarLugar.this, R.array.Categorias, android.R.layout.simple_spinner_item);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_lugar_categoria = (MaterialSpinner) findViewById(R.id.spinner_lugar_categoria);
        spinner_lugar_categoria.setAdapter(adapterCategorias);


        et_lugar_descripcion = (EditText) findViewById(R.id.et_lugar_descripcion);
        et_lugar_horario = (EditText) findViewById(R.id.et_lugar_horario);

        btn_lugar_cancelar = (Button) findViewById(R.id.btn_lugar_cancelar);
        btn_lugar_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_lugar_continuar = (Button) findViewById(R.id.btn_lugar_continuar);
        btn_lugar_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idCategoria = spinner_lugar_categoria.getSelectedItemPosition();
                if(idCategoria!=0){
                    String tipo = "";
                    switch (idCategoria) {
                        case 1:
                            tipo = "restaurante";
                            break;
                        case 2:
                            tipo = "rumba";
                            break;
                        case 3:
                            tipo = "cultura";
                            break;
                        case 4:
                            tipo = "musica";
                            break;
                        case 5:
                            tipo = "deporte";
                            break;
                        case 6:
                            tipo = "ropa";
                            break;
                        case 7:
                            tipo = "religion";
                            break;
                    }


                    Lugar lugar = new Lugar(
                            et_lugar_nombre.getText().toString(),
                            et_lugar_telefono.getText().toString(),
                            et_lugar_horario.getText().toString(),
                            tipo,
                            et_lugar_descripcion.getText().toString(),
                            file_name, "",0.0, 0.0, 0.0);
                    //lugar.setFoto(bitmap);
                    Comunicador.setObjeto1(lugar);
                    Comunicador.setObjeto2(bitmap);
                    //Intent intent = new Intent(AgregarLugar.this, AgregarSitioMapa.class);
                    startActivity(new Intent(AgregarLugar.this, AgregarSitioMapa.class));
                }else{
                    Toast.makeText(AgregarLugar.this, "Debe seleccionar la categoria", Toast.LENGTH_SHORT).show();
                }

            }
        });

        super.onCreate(savedInstanceState);
    }

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
        imv_nuevoLugar_foto.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);

    }
}
