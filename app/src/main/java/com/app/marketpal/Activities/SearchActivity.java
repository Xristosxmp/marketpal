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
import android.os.Bundle;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        search_list = new ArrayList<>();
        adp = new Adaptery(this , search_list, ActivityType.SEARCH_ACTIVITY);
        rv_search = findViewById(R.id.search_recycler);
        rv_search.setItemAnimator(null);
        rv_search.setLayoutManager(new GridLayoutManager(this,3));
        rv_search.setAdapter(adp);
        rv_search.setHasFixedSize(true);
        rv_search.setItemViewCacheSize(20);


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


    private class AsyncSearch extends AsyncTask<String,String, JSONObject>{

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
        protected JSONObject doInBackground(String... strings) {
            JSONObject JSON_OBJECT = new JSONObject();


            try {
                JSON_OBJECT = new JSONObject();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                ResponseBody responseBody = response.body();
                String json = responseBody != null ? responseBody.string() : "";
                JSON_OBJECT = new JSONObject(json);
            } catch (Exception e) {JSON_OBJECT = null;}


            return JSON_OBJECT;
        }

        @Override
        protected void onPostExecute(JSONObject JSON_OBJECT) {
            super.onPostExecute(JSON_OBJECT);
            if(JSON_OBJECT == null) return;
            try {
                JSONArray data = JSON_OBJECT.getJSONArray("data");

                findViewById(R.id.loading_container).setVisibility(View.GONE); shimmer.cancel();

                if(data.length() <= 0)  findViewById(R.id.no_products).setVisibility(View.VISIBLE);
                else findViewById(R.id.no_products).setVisibility(View.GONE);
                for(int i=0; i<data.length(); i++){
                    JSONObject product = data.getJSONObject(i);
                    String product_id = product.getString("id");
                    String product_name = product.getString("name");
                    String product_desc = product.getString("description");
                    String product_brand = product.getString("brand_id");
                    String coupon_value = "null";
                    String coupon_value_discount = "null";
                    String product_img = null;
                    JSONArray ASSORTMENTS =  product.getJSONArray("assortments");
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";

                    if(!product.isNull("image_versions"))
                        product_img = product.getJSONObject("image_versions").getString("original");
                    else product_img = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";



                    if(product.getJSONArray("coupons").length() > 0){
                        JSONArray J = product.getJSONArray("coupons");
                        JSONObject P = J.getJSONObject(0);
                        double v1 = P.getDouble("value");
                        double v2 = P.getDouble("value_discount");
                        if (v1 > 0) coupon_value = new DecimalFormat("#0.00").format(v1);
                        if (v2 > 0) coupon_value_discount = new DecimalFormat("#0.00").format(v2);
                    }
                    for(int j=0; j<ASSORTMENTS.length(); j++){
                        JSONObject item = ASSORTMENTS.getJSONObject(j);
                        JSONObject retailer = item.getJSONObject("retailer");
                        JSONObject productPivot = item.getJSONObject("product_pivot");
                        String name = retailer.getString("name");
                        if(!supermarkets.contains(name)) continue;

                        double finalPrice;
                        if (productPivot.isNull("final_price")) {finalPrice = productPivot.getDouble("start_price");}
                        else {finalPrice = productPivot.getDouble("final_price");}
                        if (finalPrice < FINAL_PRICE) {FINAL_NAME = name; FINAL_PRICE = finalPrice;}
                        ASSORTEMTNS_DATA[j][0] = name;
                        ASSORTEMTNS_DATA[j][1] = String.valueOf(finalPrice);
                    }
                    if(FINAL_PRICE == 55555) continue;


                    ProductClass model = new ProductClass();
                    model.setName(product_name);
                    model.setUrl(product_img);
                    model.setMarket(FINAL_NAME.trim());  model.setPrice(String.valueOf(FINAL_PRICE) + " €");
                    model.setID(product_id);
                    model.setASSORTEMTNS_DATA(ASSORTEMTNS_DATA);
                    model.setDesc(product_desc);
                    model.setOrigianlName(product_name);
                    model.setBrand_id(product_brand);
                    model.setValue_discount(coupon_value_discount);
                    model.setCoupon_value(coupon_value);
                    search_list.add(model);

                }
                adp.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        Intent intent = new Intent(SearchActivity.this, ProductView.class);
                        intent.putExtra("original_name" , search_list.get(position).getOrigianlName());
                        intent.putExtra("id" , search_list.get(position).getID());
                        intent.putExtra("name" , search_list.get(position).getName());
                        intent.putExtra("img" , search_list.get(position).getUrl());
                        //intent.putExtra("desc" , dairy_01_list.get(position).get());
                        intent.putExtra("assortments" , search_list.get(position).getASSORTEMTNS_DATA());
                        intent.putExtra("desc" , search_list.get(position).getDesc());
                        intent.putExtra("brand_id" , search_list.get(position).getBrand_id());
                        intent.putExtra("coupon_value" , search_list.get(position).getCoupon_value());
                        intent.putExtra("coupon_value_discount" , search_list.get(position).getValue_discount());


                        startActivity(intent);
                    }
                });
                adp.notifyDataSetChanged();
            }catch (Exception e){e.printStackTrace();}

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


}
