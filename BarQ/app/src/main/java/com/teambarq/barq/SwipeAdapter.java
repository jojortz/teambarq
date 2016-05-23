package com.teambarq.barq;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemServed(int position);
        void onItemDeleteAction(int position);
    }

    private List<Device> mDevices;
    private OnItemClickListener mOnItemClickListener;

    public SwipeAdapter(List<Device> devices) {
        super();
        this.mDevices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device1 = getItem(position);
        holder.Location.setText(device1.Location);

        switch(device1.Color) {
            case "Blue":
                holder.Location.setBackgroundResource(R.color.bluegreen);
                break;
            case "Red":
                holder.Location.setBackgroundResource(R.color.redorange);
                break;
            case "Yellow":
                holder.Location.setBackgroundResource(R.color.softyellow);
                break;
        }
        holder.coordinatorLayout.setOnServeListener(new OnItemServe(position));
        holder.delete.setOnClickListener(new OnActionListener(position));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.coordinatorLayout.sync();
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void deleteItem(int position) {
        mDevices.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void addItem(Device newdevice) {
        mDevices.add(newdevice);
//        notifyItemInserted(getItemCount()-1);
        notifyDataSetChanged();
    }

    public Device getItem(int position) {
        return mDevices.get(position);
    }

    public class OnItemServe implements CoordinatorLayout.OnServeListener {

        private int position;

        public OnItemServe(int position) {
            this.position = position;
        }

        @Override
        public void onDismissed() {
                mOnItemClickListener.onItemServed(position);
        }
    }

    private class OnActionListener implements View.OnClickListener {

        private int position;

        public OnActionListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemDeleteAction(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //TODO: change font for location
        public CoordinatorLayout coordinatorLayout;
        @Bind(R.id.Location)
        public TextView Location;
        @Bind(R.id.delete)
        public ImageButton delete;

        public ViewHolder(View view) {
            super(view);
            coordinatorLayout = (CoordinatorLayout) view;
            ButterKnife.bind(this, view);
        }
    }
}
