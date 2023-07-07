package com.example.moupass10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<DataModel> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public DataAdapter(Context context, ArrayList<DataModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mData.get(position).getTitle();
        String website = mData.get(position).getWebsite();
        holder.myTitleTextView.setText(title);
        holder.myWebsiteTextView.setText(website);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTitleTextView;
        TextView myWebsiteTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTitleTextView = itemView.findViewById(R.id.txtTitle);
            myWebsiteTextView = itemView.findViewById(R.id.txtWebsite);
        }
    }
}

