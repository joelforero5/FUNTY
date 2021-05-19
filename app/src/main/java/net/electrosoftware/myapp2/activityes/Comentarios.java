package net.electrosoftware.myapp2.activityes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.ComentariosAdapterLarge;
import net.electrosoftware.myapp2.clasesbases.ComentariosData;

import java.util.ArrayList;
import java.util.List;

public class Comentarios extends AppCompatActivity {
    RecyclerView rv_comentarios_large;
    List<ComentariosData> dataModels;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        mToolbar = (Toolbar) findViewById(R.id.toolbarComent);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("Mis Eventos");

        rv_comentarios_large = (RecyclerView) findViewById(R.id.rv_comentarios_large);
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(Comentarios.this);
        rv_comentarios_large.setLayoutManager(linearlayoutmanager);
        rv_comentarios_large.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
    }

    private void initializeData() {
        dataModels = new ArrayList<>();

        Bitmap icon = BitmapFactory.decodeResource(Comentarios.this.getResources(),
                R.drawable.usuario);

        dataModels.add(new ComentariosData(icon, "Joletta Spurdens", "11/09/2016", "Integer non velit", "1.5"));
        dataModels.add(new ComentariosData(icon, "Inessa Cutts", "04/19/2017", "Quisque ut erat", "2.7"));
        dataModels.add(new ComentariosData(icon, "Fairlie Yuryichev", "07/06/2016", "Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo", "2.4"));
        dataModels.add(new ComentariosData(icon, "Clem Ealam", "12/10/2016", "Nulla mollis molestie lorem", "4.3"));
        dataModels.add(new ComentariosData(icon, "Barbie Walklot", "01/06/2017", "Nullam molestie nibh in lectus", "1.4"));
        dataModels.add(new ComentariosData(icon, "Lief Gofford", "06/25/2016", "Fusce posuere felis sed lacus", "2.1"));
        dataModels.add(new ComentariosData(icon, "Noah Spurdens", "01/24/2017", "Mauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis", "3.2"));
        dataModels.add(new ComentariosData(icon, "Collete Dupoy", "11/05/2016", "Aliquam sit amet diam in magna bibendum imperdiet", "1.4"));
        dataModels.add(new ComentariosData(icon, "Charin Pluvier", "12/18/2016", "Morbi ut odio", "2.6"));
    }

    private void initializeAdapter() {
        ComentariosAdapterLarge adapter = new ComentariosAdapterLarge(dataModels);
        rv_comentarios_large.setAdapter(adapter);
    }
}
