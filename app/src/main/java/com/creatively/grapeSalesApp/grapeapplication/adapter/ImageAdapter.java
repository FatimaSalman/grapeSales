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

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.OnItemClickListener;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.creatively.grapeSalesApp.grapeapplication.model.Images;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private List<Images> itemList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage, dateLayout;
        ProgressBar progressbar;

        MyViewHolder(View view) {
            super(view);
            userImage = view.findViewById(R.id.imageView);
            dateLayout = view.findViewById(R.id.dateLayout);
            progressbar = view.findViewById(R.id.progressbar);
        }
    }


    public ImageAdapter(Context context, List<Images> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Images item = itemList.get(position);
        holder.progressbar.setVisibility(View.VISIBLE);

        if (item.getImages() != null)
            if (!item.getImages().isEmpty()) {
                Picasso.get().load(FontManager.IMAGE_URL + item.getImages())
                        .into(holder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressbar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.progressbar.setVisibility(View.GONE);
                            }
                        });
            }

        if (item.getUri() != null)
            Picasso.get().load(item.getUri())
                    .into(holder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.progressbar.setVisibility(View.GONE);
                        }
                    });

        holder.dateLayout.setOnClickListener(new View.OnClickListener() {
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