package com.creatively.grapeSalesApp.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ShopSearchAdapter extends RecyclerView.Adapter<ShopSearchAdapter.MyViewHolder> {

    private List<Shop> shopList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerQuanityTxt, addressTxt, shopNameTxt, seenTxt;
        ProgressBar progressBar;
        RoundedImageView shopImage;
        RelativeLayout layout;
        ImageView ic_remove, ic_edit;
        RatingBar rating;

        MyViewHolder(View view) {
            super(view);
            offerQuanityTxt = view.findViewById(R.id.offerQuanityTxt);
            layout = view.findViewById(R.id.layout);
            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_grape_border));
            addressTxt = view.findViewById(R.id.addressTxt);
            shopNameTxt = view.findViewById(R.id.shopNameTxt);
            progressBar = view.findViewById(R.id.progressBar);
            shopImage = view.findViewById(R.id.shopImage);
            ic_remove = view.findViewById(R.id.ic_remove);
            ic_remove.setVisibility(View.GONE);
            ic_edit = view.findViewById(R.id.ic_edit);
            ic_edit.setVisibility(View.GONE);
            rating = view.findViewById(R.id.rating);
            seenTxt = view.findViewById(R.id.seenTxt);
        }
    }


    public ShopSearchAdapter(Context context, List<Shop> shopList, OnItemClickListener listener) {
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
        holder.rating.setRating(Float.parseFloat(item.getRating()));
        holder.offerQuanityTxt.setText(item.getOfferCount());
        holder.seenTxt.setText(item.getSeen());
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(FontManager.IMAGE_URL + item.getShopImage())
                .into(holder.shopImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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