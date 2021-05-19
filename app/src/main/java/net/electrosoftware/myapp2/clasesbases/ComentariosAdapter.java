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

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentariosViewHolder> {

    public static class ComentariosViewHolder extends RecyclerView.ViewHolder {

        TextView txt_nombre_usuario_comentario;
        TextView txt_texto_comentario;
        TextView txt_fecha_comentario;
        ImageView imv_foto_perfil_comentario;
        CardView cv_comentario;

        ComentariosViewHolder(View itemView) {
            super(itemView);
            txt_nombre_usuario_comentario = (TextView) itemView.findViewById(R.id.txt_nombre_usuario_comentario);
            txt_texto_comentario = (TextView) itemView.findViewById(R.id.txt_texto_comentario);
            txt_fecha_comentario = (TextView) itemView.findViewById(R.id.txt_fecha_comentario);
            imv_foto_perfil_comentario = (ImageView) itemView.findViewById(R.id.imv_foto_perfil_comentario);
            cv_comentario = (CardView)itemView.findViewById(R.id.cv_comentario);
        }
    }

    List<ComentariosData> comentariosDataList;

    public ComentariosAdapter(List<ComentariosData> comentariosList) {
        this.comentariosDataList = comentariosList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ComentariosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comentarios, viewGroup, false);
        ComentariosViewHolder pvh = new ComentariosViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ComentariosViewHolder personViewHolder, final int i) {
        personViewHolder.txt_nombre_usuario_comentario.setText(comentariosDataList.get(i).getNombreUsuario());
        int comentarioLength = comentariosDataList.get(i).comentarioTexto.length();
        if (comentarioLength >= 30) {
            String comentarioTrim = comentariosDataList.get(i).comentarioTexto.substring(0, 30) + "...";
            personViewHolder.txt_texto_comentario.setText(comentarioTrim);
        } else {
            personViewHolder.txt_texto_comentario.setText(comentariosDataList.get(i).comentarioTexto);
        }
        personViewHolder.txt_fecha_comentario.setText(comentariosDataList.get(i).fechaComentario);
        personViewHolder.imv_foto_perfil_comentario.setImageBitmap(comentariosDataList.get(i).getFotoUsuario());

        personViewHolder.cv_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Foto del usuario " + comentariosDataList.get(i).getNombreUsuario(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int num = 1;
        if (num * 3 > comentariosDataList.size()) {
            return comentariosDataList.size();
        } else {
            return num * 3;
        }
    }
}

