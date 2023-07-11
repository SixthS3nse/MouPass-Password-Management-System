package com.example.moupass10.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.moupass10.R;

import java.util.List;

public class DataItemAdapter extends RecyclerView.Adapter<com.example.moupass10.ui.dashboard.DataItemAdapter.ViewHolder> {

    private List<DataItem> dataItems;
    private LayoutInflater mInflater;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(DataItem dataItem);
    }

    private OnItemClickListener listener;

    // data is passed into the constructor
    public DataItemAdapter(Context context, List<DataItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.dataItems = data;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list, parent, false);
        return new com.example.moupass10.ui.dashboard.DataItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataItem item = dataItems.get(position);
        String title = dataItems.get(position).getTitle();
        String website = dataItems.get(position).getWebsite();
        holder.txtTitle.setText(title);
        holder.txtWebsite.setText(website);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DashboardDetails.class);
                intent.putExtra("dataItem", item);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtWebsite;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle1);
            txtWebsite = itemView.findViewById(R.id.txtWebsite1);
        }
    }
}
