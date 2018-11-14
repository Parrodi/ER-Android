package com.example.carlo.androidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carlo.androidapp.R;
import com.example.carlo.androidapp.actividades.MapsActivity;
import com.example.carlo.androidapp.actividades.TourDescriptionActivity;
import com.example.carlo.androidapp.modelos.Tour;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Tour> tours;

    public ViewPagerAdapter(Context context, List<Tour> tours) {
        this.tours = tours;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tours.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position){

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.nombreDelTour);

        final Tour recorrido = tours.get(position);

        textView.setText(recorrido.getName());

        URL url = recorrido.getImage();

        Picasso.with(context).load(url.toString()).into(imageView);
        ViewPager vp =(ViewPager) container;
        vp.addView(view, 0);

        Button boton = (Button) view.findViewById(R.id.comprarTour);
        boton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, TourDescriptionActivity.class);
                i.putExtra("tour_name", recorrido.getName());
                i.putExtra("tour_description", recorrido.getDescription());
                i.putExtra("tour_image", recorrido.getImage().toString());

                context.startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
