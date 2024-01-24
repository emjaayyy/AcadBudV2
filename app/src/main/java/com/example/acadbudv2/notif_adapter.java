package com.example.acadbudv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class notif_adapter extends RecyclerView.Adapter<notif_adapter.ViewHolder> {
    private List<notif_item> notifications;
    private Context context;

    public notif_adapter(Context context, List<notif_item> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notif_item notification = notifications.get(position);
        holder.titleTextView.setText(notification.getTitle());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notif_tv_1);
        }
    }

}