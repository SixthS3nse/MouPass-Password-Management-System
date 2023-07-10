package com.example.moupass10.ui.dashboard;

import android.content.Context;
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

    // data is passed into the constructor
    public DataItemAdapter(Context context, List<DataItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.dataItems = data;
        this.context = context;
    }

    @Override
    public com.example.moupass10.ui.dashboard.DataItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list, parent, false);
        return new com.example.moupass10.ui.dashboard.DataItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.example.moupass10.ui.dashboard.DataItemAdapter.ViewHolder holder, int position) {
        String title = dataItems.get(position).getTitle();
        String website = dataItems.get(position).getWebsite();
        holder.txtTitle.setText(title);
        holder.txtWebsite.setText(website);
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
