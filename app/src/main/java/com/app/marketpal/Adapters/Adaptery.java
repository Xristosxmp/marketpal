package com.app.marketpal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class Adaptery extends RecyclerView.Adapter<Adaptery.MyViewHolder> {


    private Context mContext;
    private List<ProductClass> mData;
    private Adaptery.OnClickListener onClickListener;
    private ActivityType activityType;


    public Adaptery(Context mContext , List<ProductClass> mData, ActivityType activityType){
        this.mContext = mContext;
        this.mData = mData;
        this.activityType = activityType;
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(activityType == ActivityType.MAIN_ACTIVITY)  v = inflater.inflate(R.layout.product_item , parent , false);
        else v = inflater.inflate(R.layout.product_item_search , parent , false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String product = mData.get(position).getUrl();
        holder.txt_01.setText(mData.get(position).getName());
        holder.txt_02.setText(mData.get(position).getPrice().replace("." , ","));

        Glide.with(mContext).load(product)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .dontAnimate()
                .error(R.drawable.product_not_found)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.product_placeholder)
                .into(holder.img_02);

        switch (mData.get(position).getMarket()) {
            case "MYMARKET": holder.img_01.setImageResource(R.drawable.mymarket);  break;
            case "ΜΑΣΟΥΤΗΣ": holder.img_01.setImageResource(R.drawable.masouths); break;
            case "E-FRESH.GR": holder.img_01.setImageResource(R.drawable.efresh);  break;
            case "ΓΑΛΑΞΙΑΣ":  holder.img_01.setImageResource(R.drawable.galaxias); break;
            case "MARKET IN": holder.img_01.setImageResource(R.drawable.market_in); break;
            case "ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ": holder.img_01.setImageResource(R.drawable.ab); break;
            case "ΣΚΛΑΒΕΝΙΤΗΣ": holder.img_01.setImageResource(R.drawable.sklavenitis_logo); break;
            case "NULL": holder.img_01.setVisibility(View.GONE); break;
            default:  break;
        }


        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, mData.get(position));
            }
        });

    }
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onClick(int position, ProductClass model);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_01;
        TextView txt_01;
        ImageView img_02;
        TextView txt_02;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             img_01 = itemView.findViewById(R.id.market_product_txt);
             txt_01 = itemView.findViewById(R.id.product_name_txt);
             img_02 = itemView.findViewById(R.id.prouduct_txt);
             txt_02 = itemView.findViewById(R.id.product_price_txt);
        }

    }

}
