package net.electrosoftware.myapp2.activityes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;

public class GaleriaDetalle extends AppCompatActivity {
    ImageView imv_imagen_detalle;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_detalle);

        Bitmap icon = BitmapFactory.decodeResource(GaleriaDetalle.this.getResources(),
                R.drawable.kamran);

        imv_imagen_detalle = (ImageView) findViewById(R.id.imv_imagen_detalle);
        //Bitmap bitmap = getIntent().getParcelableExtra("image");
        imv_imagen_detalle.setImageDrawable(Comunicador.getFotoGaleriaItem());//ImageBitmap(icon);
    }
}
