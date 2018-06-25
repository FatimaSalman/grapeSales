package com.example.fatima.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.Shop;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {

    private List<Offer> offerList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerNameTxt, addressTxt, offerNextPrice, offerPreviousPrice;
        ProgressBar progressBar;
        RoundedImageView offerImage;
        RatingBar rating;

        MyViewHolder(View view) {
            super(view);
            offerNameTxt = view.findViewById(R.id.offerNameTxt);
            rating = view.findViewById(R.id.rating);
            addressTxt = view.findViewById(R.id.addressTxt);
            offerNextPrice = view.findViewById(R.id.offerNextPrice);
            offerPreviousPrice = view.findViewById(R.id.offerPreviousPrice);
            offerPreviousPrice.setPaintFlags(offerPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            progressBar = view.findViewById(R.id.progressBar);
            offerImage = view.findViewById(R.id.offerImage);
        }
    }

    public OfferAdapter(Context context, List<Offer> offerList, OnItemClickListener listener) {
        this.offerList = offerList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Offer item = offerList.get(position);
        holder.offerNameTxt.setText(item.getOfferTitle());
        holder.addressTxt.setText(item.getOfferBio());
        holder.offerNextPrice.setText(item.getNextPrice());
        holder.offerPreviousPrice.setText(item.getPreviousPrice());
        holder.rating.setRating(Float.parseFloat(item.getRating()));
        Log.e("images", FontManager.IMAGE_URL + item.getOfferImage());
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(FontManager.IMAGE_URL + item.getOfferImage()).into(holder.offerImage, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
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
        return offerList.size();
    }
}