package net.electrosoftware.myapp2.clasesbases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.activityes.GaleriaDetalle;
import net.electrosoftware.myapp2.firebaseClases.Comunicador;

import java.util.ArrayList;

public class RecyclerGaleriaAdapter extends CustomRecyclerViewAdapter {
    private Activity activity;
    private ArrayList<String> images;
    private int screenWidth;

    public RecyclerGaleriaAdapter(Activity activity, ArrayList<String> images) {
        this.activity = activity;
        this.images = images;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public CustomRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.item_grid_galeria, parent, false);
        Holder dataObjectHolder = new Holder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final CustomRecycleViewHolder holder, final int position) {
        final Holder myHolder = (Holder) holder;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(images.get(position), opts);
        opts.inJustDecodeBounds = false;
        int height;
        if (position == 1 || position == (images.size() - 1)) {
            height = 150;
        } else {
            height = 300;
        }

        Picasso.with(activity)
                .load(images.get(position))
                .error(R.drawable.ic_empty)
                //.placeholder(R.drawable.ic_launcher)
                .resize(screenWidth / 2, height)
                .centerCrop()
                .into((myHolder.imv_item_galeria_imagen));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class Holder extends CustomRecycleViewHolder {
        private ImageView imv_item_galeria_imagen;

        public Holder(View itemView) {
            super(itemView);
            imv_item_galeria_imagen = (ImageView) itemView.findViewById(R.id.imv_item_galeria_imagen);

            imv_item_galeria_imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity.getApplicationContext(), "Click!", Toast.LENGTH_SHORT).show();
                    Comunicador.setFotoGaleriaItem(imv_item_galeria_imagen.getDrawable());
                    activity.startActivity(new Intent(activity.getApplicationContext(), GaleriaDetalle.class));
                }
            });
        }
    }
}