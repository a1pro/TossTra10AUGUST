package com.app.tosstra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tosstra.R;
import com.app.tosstra.models.NotificationModel;


public class RVnotificationAdapter extends RecyclerView.Adapter<RVnotificationAdapter.notificationAdapterHolder> {
    Context context;
    NotificationModel data;
    public RVnotificationAdapter(Context context, NotificationModel data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public notificationAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new notificationAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notificationAdapterHolder holder, int position) {
        holder.tvName.setText(data.getData().get(position).getMessage());
        holder.tvDate.setText(data.getData().get(position).getNotificationDate());
        holder.tvTime.setText(data.getData().get(position).getNotificationTime());
    }

    @Override
    public int getItemCount() {
        return data.getData().size();
    }

    public class notificationAdapterHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvDate;
        public notificationAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
