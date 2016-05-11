package com.teambarq.barq;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Sanditi on 5/10/16.
 */
public class OrderAdapter extends ArrayAdapter<Device> {
    Context context;
    int layoutResourceId;
    ArrayList<Device> data = null;
    ServeActivity test;
    OrderHolder holder;
//    private final ViewBinderHelper binderHelper;
//    RideHistoryActivity test;

    public OrderAdapter(Context context, int layoutResourceId, ArrayList<Device> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        test = (ServeActivity)context;
//        binderHelper = new ViewBinderHelper();
//        test = (RideHistoryActivity)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new OrderHolder();
            holder.Location = (TextView)row.findViewById(R.id.Location);
//            holder.deleteView = row.findViewById(R.id.delete_layout);
//            holder.swipeLayout = (SwipeRevealLayout) row.findViewById(R.id.swipe_layout);
            row.setTag(holder);
        }
        else
        {
            holder = (OrderHolder)row.getTag();
        }
        final Device neworder = data.get(position);
        if (neworder!=null) {

            holder.Location.setText(neworder.Location);
            switch(neworder.Color){
                case "Blue":
                    row.setBackgroundColor(Color.parseColor("#7F0000FF"));
                    break;
                case "Red":
                    row.setBackgroundColor(Color.parseColor("#7FFF0000"));
                    break;
                case "Green":
                    row.setBackgroundColor(Color.parseColor("#7F00FF00"));
                    break;

            }

//            holder.deleteView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    test.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            remove(ride);
//                            notifyDataSetChanged();
//                            Log.e("ME202", "deleting");
//                        }
//                    });
//                }
//            });
        }
        return row;
    }

//    public void saveStates(Bundle outState) {
//        binderHelper.saveStates(outState);
//    }
//    public void restoreStates(Bundle inState) {
//        binderHelper.restoreStates(inState);
//    }


    static class OrderHolder
    {
        TextView Location;
//        View deleteView;
//        SwipeRevealLayout swipeLayout;
    }
}

