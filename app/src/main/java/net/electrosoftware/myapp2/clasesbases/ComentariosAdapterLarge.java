package net.electrosoftware.myapp2.clasesbases;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.electrosoftware.myapp2.R;

import java.util.List;

/**
 * Created by Jonathan on 13/05/2017.
 */

public class ComentariosAdapterLarge extends RecyclerView.Adapter<ComentariosAdapterLarge.ComentariosViewHolder> {

    public static class ComentariosViewHolder extends RecyclerView.ViewHolder {

        TextView txt_nombre_usuario_comentario_large, txt_texto_comentario_large, txt_fecha_comentario_large, txt_calificacion_comentario_large;
        ImageView imv_foto_perfil_comentario_large;
        CardView cv_comentario_large;

        ComentariosViewHolder(View itemView) {
            super(itemView);
            txt_nombre_usuario_comentario_large = (TextView) itemView.findViewById(R.id.txt_nombre_usuario_comentario_large);
            txt_texto_comentario_large = (TextView) itemView.findViewById(R.id.txt_texto_comentario_large);
            txt_fecha_comentario_large = (TextView) itemView.findViewById(R.id.txt_fecha_comentario_large);
            txt_calificacion_comentario_large = (TextView) itemView.findViewById(R.id.txt_calificacion_comentario_large);
            imv_foto_perfil_comentario_large = (ImageView) itemView.findViewById(R.id.imv_foto_perfil_comentario_large);
            cv_comentario_large = (CardView) itemView.findViewById(R.id.cv_comentario_large);
        }
    }

    List<ComentariosData> comentariosDataList;

    public ComentariosAdapterLarge(List<ComentariosData> comentariosList) {
        this.comentariosDataList = comentariosList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ComentariosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comentarios_large, viewGroup, false);
        ComentariosViewHolder pvh = new ComentariosViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ComentariosViewHolder comentariosViewHolder, final int i) {
        comentariosViewHolder.txt_nombre_usuario_comentario_large.setText(comentariosDataList.get(i).getNombreUsuario());
        comentariosViewHolder.txt_texto_comentario_large.setText(comentariosDataList.get(i).comentarioTexto);
        comentariosViewHolder.txt_fecha_comentario_large.setText(comentariosDataList.get(i).fechaComentario);
        comentariosViewHolder.txt_calificacion_comentario_large.setText(comentariosDataList.get(i).getCalificacionComentario());
        comentariosViewHolder.imv_foto_perfil_comentario_large.setImageBitmap(comentariosDataList.get(i).getFotoUsuario());

        comentariosViewHolder.cv_comentario_large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Foto del usuario " + comentariosDataList.get(i).getNombreUsuario(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comentariosDataList.size();
    }
}

