package com.creatively.grapeSalesApp.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.model.MenuItem;


import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<MenuItem> itemList;
    private Context context;
    public int selectPosition = 1;
    private OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView menuTitleTxt;
        View viewColor;

        MyViewHolder(View view) {
            super(view);
            menuTitleTxt = view.findViewById(R.id.menuTitleTxt);
            viewColor = view.findViewById(R.id.view);
        }
    }


    public ItemAdapter(Context context, List<MenuItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MenuItem item = itemList.get(position);
        holder.menuTitleTxt.setText(item.getName());

        if (selectPosition == position) {
            holder.viewColor.setVisibility(View.VISIBLE);
            holder.menuTitleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        } else {
            holder.viewColor.setVisibility(View.GONE);
            holder.menuTitleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}