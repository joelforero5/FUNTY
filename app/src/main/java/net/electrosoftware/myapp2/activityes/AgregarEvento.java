package net.electrosoftware.myapp2.activityes;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.Imageutils;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;
import net.electrosoftware.myapp2.firebaseClases.Evento;

import java.io.File;
import java.util.Calendar;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AgregarEvento extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    ArrayAdapter adapterCategorias, adapterPatrocinador;
    MaterialSpinner spinner_evento_categoria, spinner_evento_patrocinador;
    EditText et_evento_nombre, et_evento_telefono, et_evento_fechainicio, et_evento_fechafin, et_evento_horainicio, et_evento_horafin, et_evento_descripcion;
    ImageButton btn_evento_fechainicio, btn_evento_fechafin, btn_evento_horainicio, btn_evento_horafin;
    Button btn_evento_cancelar, btn_evento_continuar;

    Calendar calendar;
    Toolbar mToolbar;

    CircleImageView imv_nuevoEvento_foto;

    Bitmap bitmap = null;
    String file_name = "Sin imagen";
    Imageutils imageutils;
    LinearLayout linear_evento_patrocinador;
    public static Activity agregarEvento;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_agregar_evento);

        agregarEvento = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbarCrearEvento);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Mis Eventos");
        mToolbar.setTitleTextColor(Color.WHITE);

        et_evento_nombre = (EditText) findViewById(R.id.et_evento_nombre);
        et_evento_telefono = (EditText) findViewById(R.id.et_evento_telefono);

        imageutils = new Imageutils(this);
        imv_nuevoEvento_foto = (CircleImageView) findViewById(R.id.imv_nuevoEvento_foto);
        imv_nuevoEvento_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        calendar = Calendar.getInstance();

        adapterCategorias = ArrayAdapter.createFromResource(AgregarEvento.this, R.array.Categorias, android.R.layout.simple_spinner_item);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterPatrocinador = ArrayAdapter.createFromResource(AgregarEvento.this, R.array.Patrocinadores, android.R.layout.simple_spinner_item);
        adapterPatrocinador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_evento_categoria = (MaterialSpinner) findViewById(R.id.spinner_evento_categoria);
        spinner_evento_categoria.setAdapter(adapterCategorias);

        spinner_evento_patrocinador = (MaterialSpinner) findViewById(R.id.spinner_evento_patrocinador);
        spinner_evento_patrocinador.setAdapter(adapterPatrocinador);

        linear_evento_patrocinador = (LinearLayout) findViewById(R.id.linear_evento_patrocinador);

        if (Comunicador.getTipoPerfil().equalsIgnoreCase("Empresario")) {
            linear_evento_patrocinador.setVisibility(View.VISIBLE);
        } else if (Comunicador.getTipoPerfil().equalsIgnoreCase("Consumidor")) {
            linear_evento_patrocinador.setVisibility(View.GONE);
        }

        et_evento_fechainicio = (EditText) findViewById(R.id.et_evento_fechainicio);
        et_evento_descripcion = (EditText) findViewById(R.id.et_evento_descripcion);
        btn_evento_fechainicio = (ImageButton) findViewById(R.id.btn_evento_fechainicio);
        btn_evento_fechainicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(AgregarEvento.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                et_evento_fechainicio.setText(new StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth).append("/").append(year).append(" "));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dpd.show();
            }
        });

        et_evento_fechafin = (EditText) findViewById(R.id.et_evento_fechafin);
        btn_evento_fechafin = (ImageButton) findViewById(R.id.btn_evento_fechafin);
        btn_evento_fechafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(AgregarEvento.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                et_evento_fechafin.setText(new StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth).append("/").append(year).append(" "));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dpd.show();
            }
        });

        et_evento_horainicio = (EditText) findViewById(R.id.et_evento_horainicio);
        btn_evento_horainicio = (ImageButton) findViewById(R.id.btn_evento_horainicio);
        btn_evento_horainicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AgregarEvento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String AM_PM = "";
                                String hourString = "";

                                if (hourOfDay < 12) {
                                    AM_PM = "am";
                                    hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                } else {
                                    AM_PM = "pm";
                                    hourString = (hourOfDay - 12) < 10 ? "0" + (hourOfDay - 12) : "" + (hourOfDay - 12);
                                }
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;

                                if (hourString.equalsIgnoreCase("00")) {
                                    hourString = "12";
                                }

                                String time = hourString + ":" + minuteString + " " + AM_PM;

                                et_evento_horainicio.setText(time);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        et_evento_horafin = (EditText) findViewById(R.id.et_evento_horafin);
        btn_evento_horafin = (ImageButton) findViewById(R.id.btn_evento_horafin);
        btn_evento_horafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AgregarEvento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String AM_PM = "";
                                String hourString = "";

                                if (hourOfDay < 12) {
                                    AM_PM = "am";
                                    hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                                } else {
                                    AM_PM = "pm";
                                    hourString = (hourOfDay - 12) < 10 ? "0" + (hourOfDay - 12) : "" + (hourOfDay - 12);
                                }
                                String minuteString = minute < 10 ? "0" + minute : "" + minute;

                                if (hourString.equalsIgnoreCase("00")) {
                                    hourString = "12";

                                }
                                String time = hourString + ":" + minuteString + " " + AM_PM;

                                et_evento_horafin.setText(time);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        btn_evento_cancelar = (Button) findViewById(R.id.btn_evento_cancelar);
        btn_evento_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_evento_continuar = (Button) findViewById(R.id.btn_evento_continuar);
        btn_evento_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idCategoria = spinner_evento_categoria.getSelectedItemPosition();
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

                    String patrosinador = "Patrocina: ";
                    if(Comunicador.getUsuario().getPerfil().equalsIgnoreCase("Consumidor")){
                        patrosinador = patrosinador + Comunicador.getUsuario().getNombre();
                    }else{
                        patrosinador = patrosinador + spinner_evento_patrocinador.getSelectedItem().toString();
                    }
                    Evento evento = new Evento(
                            et_evento_nombre.getText().toString(),
                            et_evento_telefono.getText().toString(),
                            et_evento_fechainicio.getText().toString().replace("/", "-").replace(" " , ""),
                            et_evento_fechafin.getText().toString().replace("/", "-").replace(" " , ""),
                            et_evento_horainicio.getText().toString(),
                            et_evento_horainicio.getText().toString(),
                            patrosinador,
                            tipo,
                            et_evento_descripcion.getText().toString(),
                            file_name,
                            "", 0, 0.0, 0.0);
                    //evento.setFoto(bitmap);
                    Comunicador.setObjeto1(evento);
                    Comunicador.setObjeto2(bitmap);
                    //Intent intent = new Intent(AgregarEvento.this, AgregarSitioMapa.class);
                    startActivity(new Intent(AgregarEvento.this, AgregarSitioMapa.class));
                }else{
                    Toast.makeText(AgregarEvento.this, "Debe seleccionar la categoria", Toast.LENGTH_SHORT).show();
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
        imv_nuevoEvento_foto.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);

    }
}
