package com.app.marketpal;

/*

    RECENTLY VIEWED ADAPTERY

 */

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

public class AdapteryIII extends RecyclerView.Adapter<AdapteryIII.MyViewHolder>{

    private Context mContext;
    private List<ProductClass> mData;
    private Adaptery.OnClickListener onClickListener;

    public AdapteryIII(Context mContext , List<ProductClass> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.recently_viewed_product_item , parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String product = mData.get(position).getUrl();
        holder.txt.setText(mData.get(position).getName());


        Glide.with(mContext).load(product)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .error(R.drawable.product_not_found)
                .priority(Priority.HIGH)
                .into(holder.img);

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

    @Override
    public int getItemCount() {return mData.size();}

    public void setOnClickListener(Adaptery.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onClick(int position, ProductClass model);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView txt;
        public MyViewHolder(@NonNull View i) {
            super(i);
            img = i.findViewById(R.id.product_img_recently_viewed);
            txt = i.findViewById(R.id.product_name_recently_viewed);
        }

    }

}
