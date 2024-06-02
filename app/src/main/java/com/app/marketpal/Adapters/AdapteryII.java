package com.app.marketpal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.marketpal.Models.CategoryClass;
import com.app.marketpal.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

public class AdapteryII extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CategoryClass> mData;
    private LayoutInflater inflater;
    private boolean adXl = true;

    public AdapteryII(Context mContext, List<CategoryClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CategoryClass.DEFAULT_TYPE:
                view = inflater.inflate(R.layout.category_recycler, parent, false);
                return new DefaultTypeView(view);
            case CategoryClass.HEADER_TYPE:
                view = inflater.inflate(R.layout.category_header, parent, false);
                return new HeaderTypeView(view);
            case CategoryClass.NO_TITLE_TYPE:
                view = inflater.inflate(R.layout.category_recycler2, parent, false);
                return new NoTitleTypeView(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryClass object = mData.get(position);
        if (object == null) return;

        switch (object.type) {
            case CategoryClass.DEFAULT_TYPE:
                bindDefaultType((DefaultTypeView) holder, object);
                break;
            case CategoryClass.HEADER_TYPE:
                bindHeaderType((HeaderTypeView) holder);
                break;
            case CategoryClass.NO_TITLE_TYPE:
                bindNoTitleType((NoTitleTypeView) holder, object);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private void bindDefaultType(DefaultTypeView holder, CategoryClass object) {
        holder.category_title.setText(object.getCategory_title());
        holder.category_desc.setText(object.getCategory_desc());
        holder.category_brand.setText(object.getCategory_brand());

        holder.category_holder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.category_holder.setAdapter(object.getCategory_holder());
        holder.category_holder.setItemAnimator(null);
        holder.category_holder.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        holder.no_products.setVisibility(object.getCategory_holder() == null ? View.VISIBLE : View.GONE);
    }

    private void bindHeaderType(HeaderTypeView holder) {
        if (adXl) {
            MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    holder.ad.loadAd(adRequest);
                    holder.ad.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            holder.ad.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            holder.ad.setVisibility(View.GONE);
                        }
                    });
                }
            });
            adXl = false;
        }
    }

    private void bindNoTitleType(NoTitleTypeView holder, CategoryClass object) {
        holder.category_brand.setText(object.getCategory_brand());

        holder.category_holder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.category_holder.setAdapter(object.getCategory_holder());
        holder.category_holder.setItemAnimator(null);
        holder.category_holder.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        holder.no_products.setVisibility(object.getCategory_holder() == null ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<CategoryClass> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.mData, newData));
        this.mData.clear();
        this.mData.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class DefaultTypeView extends RecyclerView.ViewHolder {
        TextView category_title;
        TextView category_desc;
        TextView category_brand;
        RecyclerView category_holder;
        LinearLayout no_products;

        public DefaultTypeView(View itemView) {
            super(itemView);
            category_title = itemView.findViewById(R.id.category_recycler_title);
            category_desc = itemView.findViewById(R.id.category_recycler_desc);
            category_brand = itemView.findViewById(R.id.category_recycler_brand);
            category_holder = itemView.findViewById(R.id.category_recycler_holder);
            no_products = itemView.findViewById(R.id.category_recycler_no_products);
        }
    }

    public static class HeaderTypeView extends RecyclerView.ViewHolder {
        AdView ad;

        public HeaderTypeView(View itemView) {
            super(itemView);
            ad = itemView.findViewById(R.id.categoty_ad);
        }
    }

    public static class NoTitleTypeView extends RecyclerView.ViewHolder {
        TextView category_brand;
        RecyclerView category_holder;
        LinearLayout no_products;

        public NoTitleTypeView(View itemView) {
            super(itemView);
            category_brand = itemView.findViewById(R.id.category_recycler_brand);
            category_holder = itemView.findViewById(R.id.category_recycler_holder);
            no_products = itemView.findViewById(R.id.category_recycler_no_products);
        }
    }

    static class DiffCallback extends DiffUtil.Callback {

        private final List<CategoryClass> oldList;
        private final List<CategoryClass> newList;

        public DiffCallback(List<CategoryClass> oldList, List<CategoryClass> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Assuming CategoryClass has a unique ID field
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

}