package com.creatively.grapeSalesApp.grapeapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.Order;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView shopNameTxt, offerNameTxt, dateTxt, statusTxt;
        ProgressBar progressBar;
        RoundedImageView offerImage;

        MyViewHolder(View view) {
            super(view);
            shopNameTxt = view.findViewById(R.id.shopNameTxt);
            offerNameTxt = view.findViewById(R.id.offerNameTxt);
            dateTxt = view.findViewById(R.id.dateTxt);
            progressBar = view.findViewById(R.id.progressBar);
            offerImage = view.findViewById(R.id.offerImage);
            statusTxt = view.findViewById(R.id.statusTxt);
        }
    }

    public OrderAdapter(Context context, List<Order> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Order item = orderList.get(position);
        holder.offerNameTxt.setText(item.getOffer_name());
        holder.shopNameTxt.setText(item.getShop_name());
        holder.dateTxt.setText(item.getCreated_at());
        if (TextUtils.equals(item.getStatus(), "0")) {
            holder.statusTxt.setText("قيد الانتظار");
        } else if (TextUtils.equals(item.getStatus(), "1")) {
            holder.statusTxt.setText("قيد التجهيز");
        } else if (TextUtils.equals(item.getStatus(), "2")) {
            holder.statusTxt.setText("تم الغاء الطلب");
        } else if (TextUtils.equals(item.getStatus(), "3")) {
            holder.statusTxt.setText("تم رفض الطلب");
        } else if (TextUtils.equals(item.getStatus(), "4")) {
            holder.statusTxt.setText("قيد التوصيل");
        } else if (TextUtils.equals(item.getStatus(), "5")) {
            holder.statusTxt.setText("اكتمال الطلب");
        }
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(FontManager.IMAGE_URL + item.getImage()).into(holder.offerImage, new Callback() {
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
        return orderList.size();
    }
}