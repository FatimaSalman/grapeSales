package com.creatively.grapeSalesApp.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.List;


public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.MyViewHolder> {

    private List<Offer> offerList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerNameTxt, addressTxt, offerNextPrice, offerPreviousPrice;
        ProgressBar progressBar;
        RoundedImageView offerImage;

        MyViewHolder(View view) {
            super(view);
            offerNameTxt = view.findViewById(R.id.offerNameTxt);
            addressTxt = view.findViewById(R.id.addressTxt);
            offerNextPrice = view.findViewById(R.id.offerNextPrice);
            offerPreviousPrice = view.findViewById(R.id.offerPreviousPrice);
            offerPreviousPrice.setPaintFlags(offerPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            progressBar = view.findViewById(R.id.progressBar);
            offerImage = view.findViewById(R.id.offerImage);
        }
    }


    public OfferListAdapter(Context context, List<Offer> offerList, OnItemClickListener listener) {
        this.offerList = offerList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item_list_row, parent, false);

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