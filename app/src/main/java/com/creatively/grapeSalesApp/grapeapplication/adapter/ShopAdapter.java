package com.creatively.grapeSalesApp.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.MenuItem;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {

    private List<Shop> shopList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerQuanityTxt, addressTxt, shopNameTxt;
        ProgressBar progressBar;
        RoundedImageView shopImage;
        ImageView ic_remove, ic_edit;
        RelativeLayout layout;

        MyViewHolder(View view) {
            super(view);
            offerQuanityTxt = view.findViewById(R.id.offerQuanityTxt);
            addressTxt = view.findViewById(R.id.addressTxt);
            shopNameTxt = view.findViewById(R.id.shopNameTxt);
            progressBar = view.findViewById(R.id.progressBar);
            shopImage = view.findViewById(R.id.shopImage);
            ic_remove = view.findViewById(R.id.ic_remove);
            ic_edit = view.findViewById(R.id.ic_edit);
            layout = view.findViewById(R.id.layout);
        }
    }


    public ShopAdapter(Context context, List<Shop> shopList, OnItemClickListener listener) {
        this.shopList = shopList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Shop item = shopList.get(position);
        holder.shopNameTxt.setText(item.getName());
        holder.addressTxt.setText(item.getAddress());
        holder.offerQuanityTxt.setText(item.getOfferCount());
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(FontManager.IMAGE_URL + item.getShopImage())
                .into(holder.shopImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ic_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }
}