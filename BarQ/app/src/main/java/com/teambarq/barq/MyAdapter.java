package com.teambarq.barq;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jojortz on 4/6/2016.
 */
public class MyAdapter extends ArrayAdapter<Bartender>{
    Context thisContext;
    private static LayoutInflater inflater;

    public MyAdapter (Context context, ArrayList<Bartender> bartenderList){
        super(context, 0, bartenderList);
        thisContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder{
        public TextView name;
        public ImageView image;
    }

    @Override
    public View getView(int pos, View cV, ViewGroup par) {
        View row = cV;
        //Get the data item for this position
        Bartender bartender= getItem(pos);

//Check if view exists and only created if not
        if (row == null) {
            //inflate code
            row = inflater.inflate(R.layout.bartender_grid,null);
            ViewHolder holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.bartender_imageView);
            holder.name = (TextView) row.findViewById(R.id.bartender_textView);
            row.setTag(holder);
        }
        ViewHolder h = (ViewHolder) row.getTag();
        Picasso.with(thisContext).load(bartender.profilePic).into(h.image);
        h.name.setText(bartender.name);

        return row;

    }
}
