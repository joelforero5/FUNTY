package net.electrosoftware.myapp2.activityes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import net.electrosoftware.myapp2.R;
import net.electrosoftware.myapp2.clasesbases.CustomRecyclerViewAdapter;
import net.electrosoftware.myapp2.clasesbases.RecyclerGaleriaAdapter;

import java.util.ArrayList;

public class Galeria extends AppCompatActivity {

    private final String foto_urls[] = {
            "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg",
            "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg",
            "https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s1024/Another%252520Rockaway%252520Sunset.jpg",
            "https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg",
            "https://lh6.googleusercontent.com/-8HO-4vIFnlw/URquZnsFgtI/AAAAAAAAAbs/WT8jViTF7vw/s1024/Antelope%252520Hallway.jpg",
            "https://lh4.googleusercontent.com/-WIuWgVcU3Qw/URqubRVcj4I/AAAAAAAAAbs/YvbwgGjwdIQ/s1024/Antelope%252520Walls.jpg",
            "https://lh6.googleusercontent.com/-UBmLbPELvoQ/URqucCdv0kI/AAAAAAAAAbs/IdNhr2VQoQs/s1024/Apre%2525CC%252580s%252520la%252520Pluie.jpg",
            "https://lh3.googleusercontent.com/-s-AFpvgSeew/URquc6dF-JI/AAAAAAAAAbs/Mt3xNGRUd68/s1024/Backlit%252520Cloud.jpg",
            "https://lh5.googleusercontent.com/-bvmif9a9YOQ/URquea3heHI/AAAAAAAAAbs/rcr6wyeQtAo/s1024/Bee%252520and%252520Flower.jpg",
            "https://lh5.googleusercontent.com/-n7mdm7I7FGs/URqueT_BT-I/AAAAAAAAAbs/9MYmXlmpSAo/s1024/Bonzai%252520Rock%252520Sunset.jpg"
    };

    private RecyclerView card_recycler_view;
    private ArrayList<String> images = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    CustomRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        card_recycler_view = (RecyclerView) findViewById(R.id.card_recycler_view);

        initializeImages();

        card_recycler_view.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        card_recycler_view.setLayoutManager(mLayoutManager);
        adapter = new RecyclerGaleriaAdapter(this, images);

        card_recycler_view.setAdapter(adapter);
    }

    private void initializeImages() {
        images = new ArrayList<>();
        for (int i = 0; i < foto_urls.length; i++) {
            images.add(foto_urls[i]);
        }
    }
}
