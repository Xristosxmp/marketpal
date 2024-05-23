package com.app.marketpal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

public class AdapteryII extends RecyclerView.Adapter{


    private Context mContext;
    private List<CategoryClass> mData;


    public AdapteryII(Context mContext , List<CategoryClass> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case CategoryClass.DEFAULT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler , parent , false);
                return new DefaultTypeView(view);
            case CategoryClass.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_header , parent , false);
                return new HeaderTypeView(view);
            case CategoryClass.NO_TITLE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler2 , parent , false);
                return new NoTitleTypeView(view);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean adXl = true;
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        CategoryClass object = mData.get(position);
        if(object != null){
            switch (object.type){
                case CategoryClass.DEFAULT_TYPE:
                    ((DefaultTypeView) holder).category_title.setText(mData.get(position).getCategory_title());
                    ((DefaultTypeView) holder).category_desc.setText(mData.get(position).getCategory_desc());
                    ((DefaultTypeView) holder).category_brand.setText(mData.get(position).getCategory_brand());
                    ((DefaultTypeView) holder).category_holder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    ((DefaultTypeView) holder).category_holder.setAdapter(mData.get(position).getCategory_holder());
                    ((DefaultTypeView) holder).category_holder.setItemAnimator(null);
                    ((DefaultTypeView) holder).category_holder.setRecycledViewPool(new RecyclerView.RecycledViewPool());


                    if(mData.get(position).getStatus()){
                        ((DefaultTypeView) holder).sm.stopShimmer();((DefaultTypeView) holder).sm.setVisibility(View.GONE);
                        if(mData.get(position).getCategory_holder() == null){((DefaultTypeView) holder).no_products.setVisibility(View.VISIBLE);}
                        else {((DefaultTypeView) holder).no_products.setVisibility(View.GONE);}
                    }else{((DefaultTypeView) holder).sm.startShimmer(); ((DefaultTypeView) holder).sm.setVisibility(View.VISIBLE);}
                break;
                case CategoryClass.HEADER_TYPE:

                    if(adXl) {
                        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(InitializationStatus initializationStatus) {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                ((HeaderTypeView) holder).ad.loadAd(adRequest);
                                ((HeaderTypeView) holder).ad.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                        ((HeaderTypeView) holder).ad.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        ((HeaderTypeView) holder).ad.setVisibility(View.GONE);
                                    }
                                });

                            }
                        });
                        adXl = false;
                    }
                break;
                case CategoryClass.NO_TITLE_TYPE:
                    ((NoTitleTypeView) holder).category_brand.setText(mData.get(position).getCategory_brand());
                    ((NoTitleTypeView) holder).category_holder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    ((NoTitleTypeView) holder).category_holder.setAdapter(mData.get(position).getCategory_holder());
                    ((NoTitleTypeView) holder).category_holder.setItemAnimator(null);
                    ((NoTitleTypeView) holder).category_holder.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    if(mData.get(position).getStatus()){
                        ((NoTitleTypeView) holder).sm.stopShimmer();((NoTitleTypeView) holder).sm.setVisibility(View.GONE);
                        if(mData.get(position).getCategory_holder() == null){((NoTitleTypeView) holder).no_products.setVisibility(View.VISIBLE);}
                        else{((NoTitleTypeView) holder).no_products.setVisibility(View.GONE);}
                    }else{((NoTitleTypeView) holder).sm.startShimmer(); ((NoTitleTypeView) holder).sm.setVisibility(View.VISIBLE); }

                    break;
                default:
                    return;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).type) {
            case 0: return CategoryClass.DEFAULT_TYPE;
            case 1: return CategoryClass.HEADER_TYPE;
            case 2: return CategoryClass.NO_TITLE_TYPE;
            default: return -1;
        }
    }

    @Override
    public int getItemCount() {return mData.size();}

    public static class DefaultTypeView extends RecyclerView.ViewHolder{
        TextView category_title;
        TextView category_desc;
        TextView category_brand;
        RecyclerView category_holder;
        ImageView arrow;
        LinearLayout ln;
        ShimmerFrameLayout sm;
        LinearLayout no_products;

        public DefaultTypeView(View itemView) {
            super(itemView);
            sm = itemView.findViewById(R.id.category_recycler_shimmer);
            category_title = itemView.findViewById(R.id.category_recycler_title);
            category_desc = itemView.findViewById(R.id.category_recycler_desc);
            category_brand = itemView.findViewById(R.id.category_recycler_brand);
            category_holder = itemView.findViewById(R.id.category_recycler_holder);
            no_products = itemView.findViewById(R.id.category_recycler_no_products);

        }
    }
    public static class HeaderTypeView extends RecyclerView.ViewHolder{
        AdView ad;
        public HeaderTypeView(View itemView) {
            super(itemView);
            ad = itemView.findViewById(R.id.categoty_ad);
        }
    }
    public static class NoTitleTypeView extends RecyclerView.ViewHolder{
        TextView category_brand;
        RecyclerView category_holder;
        ShimmerFrameLayout sm;
        LinearLayout no_products;
        public NoTitleTypeView(View itemView) {
            super(itemView);
            sm = itemView.findViewById(R.id.category_recycler_shimmer);
            category_brand = itemView.findViewById(R.id.category_recycler_brand);
            category_holder = itemView.findViewById(R.id.category_recycler_holder);
            no_products = itemView.findViewById(R.id.category_recycler_no_products);

        }
    }


}
