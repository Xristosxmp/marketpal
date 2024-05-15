package com.app.marketpal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class Adaptery extends RecyclerView.Adapter<Adaptery.MyViewHolder> {


    private Context mContext;
    private List<ProductClass> mData;
    private Adaptery.OnClickListener onClickListener;


    public Adaptery(Context mContext , List<ProductClass> mData){
        this.mContext = mContext;
        this.mData = mData;

    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.product_item , parent , false);

        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String product = mData.get(position).getUrl();
        holder.txt_01.setText(mData.get(position).getName());
        holder.txt_02.setText(mData.get(position).getPrice().replace("." , ","));

        Glide.with(mContext).load(product)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .error(R.drawable.product_not_found)
                .priority(Priority.HIGH)
                .into(holder.img_02);


        if (mData.get(position).getMarket().equals("MYMARKET"))  holder.img_01.setImageResource(R.drawable.mymarket);
        if (mData.get(position).getMarket().equals("ΜΑΣΟΥΤΗΣ"))  holder.img_01.setImageResource(R.drawable.masouths);
        if (mData.get(position).getMarket().equals("E-FRESH.GR")) holder.img_01.setImageResource(R.drawable.efresh);
        if (mData.get(position).getMarket().equals("ΓΑΛΑΞΙΑΣ"))    holder.img_01.setImageResource(R.drawable.galaxias);
        if (mData.get(position).getMarket().equals("MARKET IN")) holder.img_01.setImageResource(R.drawable.market_in);
        if (mData.get(position).getMarket().equals("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ"))   holder.img_01.setImageResource(R.drawable.ab);
        if (mData.get(position).getMarket().equals("ΣΚΛΑΒΕΝΙΤΗΣ"))  holder.img_01.setImageResource(R.drawable.sklavenitis_logo);
        if (mData.get(position).getMarket().equals("NULL"))  holder.img_01.setVisibility(View.GONE);


        ProductClass product_ = mData.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, product_);
                }
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
