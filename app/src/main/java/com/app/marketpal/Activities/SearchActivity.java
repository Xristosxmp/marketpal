package com.app.marketpal.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import com.romainpiel.shimmer.Shimmer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchActivity extends AppCompatActivity {


    private Intent HomeIntent;
    private Intent OffersIntent;
    private Intent CartIntent;
    private Intent ProfileIntent;


    private RecyclerView rv_search;
    private List<ProductClass> search_list;
    private Adaptery adp;

    private int FilterChoice;
    private String SearchHistory;

    private SearchView search;
    private FrameLayout search_pressed;

    private String terms_freq[] = {
            "pampers",
            "ariel",
            "fairy",
            "nivea",
            "nescafe",
            "lenor",
            "babylino",
            "skip",
            "dettol",
            "νουνου"
    };

    private SharedPreferences supermarkets;


    @Override
    @SuppressWarnings("InvalidSetHasFixedSize")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        search_list = new ArrayList<>();
        adp = new Adaptery(this , search_list, ActivityType.SEARCH_ACTIVITY);
        adp.setHasStableIds(true);
        rv_search = findViewById(R.id.search_recycler);
        rv_search.setItemAnimator(null);
        rv_search.setLayoutManager(new GridLayoutManager(this,3));
        rv_search.setAdapter(adp);
        rv_search.setHasFixedSize(true);
        rv_search.setItemViewCacheSize(10);
        adp.setOnClickListener((position, model) -> {
            Intent intent = new Intent(SearchActivity.this, ProductView.class);
            intent.putExtra("PRODUCT_OBJ", model);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        search = findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    SearchHistory = query;
                    String URL = "";
                    switch (FilterChoice) {
                        case 0:
                            URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + query;
                            break;
                        case 1:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        case 2:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_COUPONS&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        case 3:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_50_PER&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        default:
                            URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + query;
                            break;
                    }

                    new AsyncSearch(URL, rv_search).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    search.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        search_pressed = findViewById(R.id.search_pressed);
        search_pressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = search.getQuery().toString();
                if (!query.isEmpty()) {
                    SearchHistory = query;
                    String URL = "";
                    switch (FilterChoice) {
                        case 0:
                            URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + query;
                            break;
                        case 1:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        case 2:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_COUPONS&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        case 3:
                            URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_50_PER&page=1&per_page=30&in_stock=true&term=" + query;
                            break;
                        default:
                            URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + query;
                            break;
                    }

                    new AsyncSearch(URL, rv_search).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }}
        });

        LinearLayout freq_ln = findViewById(R.id.terms_freq_container);
        for(int i=0; i<terms_freq.length; i++)
            CreateTextViewTerm(freq_ln , terms_freq[i] , i);


        CloseActivity();
        SearchFilters();
    }

    private void CreateTextViewTerm(LinearLayout p , String s , int i){
        TextView t = new TextView(this);
        t.setText(s);
        t.setTypeface(null, Typeface.BOLD);
        t.setTextColor(Color.parseColor("#696969"));
        t.setTextSize(14);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
         if(i < terms_freq.length - 1)
         pp.setMargins(dpToPx(15) , 0 , 0 , 0);
         else
         pp.setMargins(dpToPx(15) , 0 , dpToPx(15) , 0);
        t.setLayoutParams(pp);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + s;
//                new AsyncSearch(URL, rv_search)
//                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                search.setQuery(s , true);
            }
        });
        p.addView(t);
    }

    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}

    public void cbx(int id , CheckBox c1 , CheckBox c2 , CheckBox c3 , CheckBox c4) {

            if(id == R.id.checkBoxPreselected) {
                FilterChoice = 0;
                c1.setChecked(true); c2.setChecked(false);
                c3.setChecked(false); c4.setChecked(false);
            }
            else if(id == R.id.checkBoxOffer) {
                FilterChoice = 1;
                c1.setChecked(false); c2.setChecked(true);
                c3.setChecked(false); c4.setChecked(false);
            }
            else if(id == R.id.checkBoxCashback) {
                FilterChoice = 2;
                c1.setChecked(false);  c2.setChecked(false);
                c3.setChecked(true);   c4.setChecked(false);
            }
            else {
                FilterChoice = 3;
                c1.setChecked(false); c2.setChecked(false);
                c3.setChecked(false); c4.setChecked(true);
            }
    }

    private void SearchFilters(){
        FilterChoice = 0;
        findViewById(R.id.search_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SearchActivity.this);

                dialog.setContentView(R.layout.search_filter_layout);
                ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);

                InsetDrawable inset = new InsetDrawable(back, 30);
                dialog.getWindow().setBackgroundDrawable(inset);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(350));

                ImageView exit = dialog.findViewById(R.id.search_filter_exit);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                CheckBox c1 = dialog.findViewById(R.id.checkBoxPreselected);
                CheckBox c2 = dialog.findViewById(R.id.checkBoxOffer);
                CheckBox c3 = dialog.findViewById(R.id.checkBoxCashback);
                CheckBox c4 = dialog.findViewById(R.id.checkBoxGift);
                c1.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {cbx(R.id.checkBoxPreselected,c1,c2,c3,c4);}});
                c2.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {cbx(R.id.checkBoxOffer,c1,c2,c3,c4);}});
                c3.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {cbx(R.id.checkBoxCashback,c1,c2,c3,c4);}});
                c4.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {cbx(R.id.checkBoxGift,c1,c2,c3,c4);}});

                switch (FilterChoice){
                    case 0:  c1.setChecked(true); break;
                    case 1:  c2.setChecked(true); break;
                    case 2:  c3.setChecked(true); break;
                    case 3:  c4.setChecked(true); break;
                    default: c1.setChecked(true); break;
                }

                AppCompatButton apply = dialog.findViewById(R.id.btn_apply);
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(SearchHistory == null){
                            dialog.dismiss();
                            return;
                        }
                        if(SearchHistory.equals("") || SearchHistory.isEmpty()){
                            dialog.dismiss();
                            return;
                        }

                        String URL;
                        switch (FilterChoice){
                            case 0: URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + SearchHistory; break;
                            case 1: URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&page=1&per_page=30&in_stock=true&term=" + SearchHistory; break;
                            case 2: URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_COUPONS&page=1&per_page=30&in_stock=true&term=" + SearchHistory; break;
                            case 3: URL = "https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_50_PER&page=1&per_page=30&in_stock=true&term=" + SearchHistory; break;
                            default: URL = "https://v8api.pockee.com/api/v8/public/products?per_page=100&in_stock=true&term=" + SearchHistory; break;
                        }

                        new AsyncSearch(URL, rv_search)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        dialog.dismiss();
                    }
                });

            }
        });

    }


    private class AsyncSearch extends AsyncTask<String,String, Void>{

        private RecyclerView rv_01;
        private String API;
        private Shimmer shimmer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
            shimmer = new Shimmer();
            shimmer.setRepeatCount(20)
                    .setDuration(2500)
                    .setStartDelay(250)
                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);


            shimmer.start(findViewById(R.id.loading_search));
            search_list.clear();
        }

        AsyncSearch(String API , RecyclerView rv_01){
            this.API = API;
            this.rv_01 = rv_01;
        }


        @Override
        protected Void doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data.length() == 0) return null;
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        for (int i = 0; i < data.length(); i++) {
                            ProductClass model = new ProductClass();
                            JSONObject product = data.getJSONObject(i);
                            double final_price = Double.MAX_VALUE;
                            JSONArray assortments = product.getJSONArray("assortments");
                            String[][] assortmentsData = new String[assortments.length()][2];

                            if (product.has("coupons") && product.getJSONArray("coupons").length() > 0) {
                                JSONObject coupon = product.getJSONArray("coupons").getJSONObject(0);
                                if (coupon.getDouble("value") > 0) model.setCoupon_value(decimalFormat.format(coupon.getDouble("value")));
                                else model.setCoupon_value("null");
                                if (coupon.getDouble("value_discount") > 0) model.setValue_discount(decimalFormat.format(coupon.getDouble("value_discount")));
                                else model.setValue_discount("null");
                            }

                            if (!product.isNull("image_versions")) model.setUrl(product.getJSONObject("image_versions").getString("original"));
                            else model.setUrl("https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png");


                            for (int j = 0; j < assortments.length(); j++) {
                                JSONObject item = assortments.getJSONObject(j);
                                JSONObject retailer = item.getJSONObject("retailer");
                                JSONObject productPivot = item.getJSONObject("product_pivot");
                                String name = retailer.getString("name");
                                if (!supermarkets.contains(name)) continue;

                                double current_price = productPivot.isNull("final_price")
                                        ? productPivot.getDouble("start_price")
                                        : productPivot.getDouble("final_price");
                                if (current_price < final_price) {
                                    model.setMarket(name.trim());
                                    final_price = current_price;
                                    model.setPrice(current_price + " €");
                                }
                                assortmentsData[j][0] = name;
                                assortmentsData[j][1] = String.valueOf(current_price);
                            }
                            if (final_price == Double.MAX_VALUE) continue;

                            model.setName(product.getString("name"));
                            model.setID(product.getString("id"));
                            model.setASSORTEMTNS_DATA(assortmentsData);
                            model.setDesc(product.getString("description"));
                            model.setOrigianlName(product.getString("name"));
                            model.setBrand_id( product.getString("brand_id"));
                            search_list.add(model);
                        }
                    }
                }
                response.close();
            } catch (Exception e) {
                Log.e("searchact" , e.getLocalizedMessage());
            }
            return null;
        }


        @Override
        @SuppressWarnings("set")
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            findViewById(R.id.loading_container).setVisibility(View.GONE); shimmer.cancel();
            try {
                if(!search_list.isEmpty()){
                    findViewById(R.id.no_products).setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) search_list.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                    else Collections.sort(search_list, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass p1, ProductClass p2) {
                                double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });

                    adp.notifyDataSetChanged();
                }else findViewById(R.id.no_products).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("searchact" , e.getLocalizedMessage());
            }

        }
    }

    private void CloseActivity(){
        findViewById(R.id.close_act).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
