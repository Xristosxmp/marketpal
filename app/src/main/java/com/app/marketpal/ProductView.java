package com.app.marketpal;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ProductView extends AppCompatActivity {

    private ImageView product_image;
    private AppCompatTextView product_name;
    private TextView product_desc;
    private TextView product_show_more_desc;
    private LinearLayout product_prices;
    private Button add_to_cart;
    private TextView productAmount;


    private SharedPreferences shopping_cart;
    private SharedPreferences Shopping_cart_amount;
    private SharedPreferences favorites;
    private SharedPreferences supermarkets;
    private SharedPreferences recently_viewed;


    private String product_name_txt;
    private String image_url;
    private String product_id;
    private String product_original_name;
    private String brand_id;

    private LinearLayout coupon_container;
    private TextView coupon_value;
    private TextView coupon_value_discount;

    private RecentlyViewerDatabase recently_view_db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setBorderOnScroll();
        InitAdsMain();

        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        recently_viewed = getBaseContext().getSharedPreferences("recently_viewed", Context.MODE_PRIVATE);
        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        Shopping_cart_amount = getBaseContext().getSharedPreferences("shopping_cart_amount", Context.MODE_PRIVATE);
        favorites = getBaseContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);


        product_name_txt = getIntent().getStringExtra("name");
        image_url = getIntent().getStringExtra("img");
        product_id = getIntent().getStringExtra("id");
        product_original_name = getIntent().getStringExtra("original_name");
        brand_id = getIntent().getStringExtra("brand_id");


        product_image = findViewById(R.id.Product_Image);
        product_desc = findViewById(R.id.product_desc);
        product_desc.setText(getIntent().getStringExtra("desc"));
        product_show_more_desc =  findViewById(R.id.show_more_desc);
        product_name = findViewById(R.id.Product_Name);
        product_name.setText(product_name_txt);
        product_prices = findViewById(R.id.product_all_prices);
        add_to_cart = findViewById(R.id.add_to_cart);
        productAmount = findViewById(R.id.product_amount);

        // Database
        recently_view_db = new RecentlyViewerDatabase(this);
        SQLiteDatabase db = recently_view_db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecentlyViewerDatabase.COLUMN_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put(RecentlyViewerDatabase.COLUMN_PRODUCT_ID, product_id);
        db.insertWithOnConflict(RecentlyViewerDatabase.TABLE_RECENTLY_VIEWED, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();


        // Coupons
        coupon_container = findViewById(R.id.coupon_container);
        coupon_value = findViewById(R.id.coupon_value);
        coupon_value_discount = findViewById(R.id.coupon_value_discount);

        String c1 = getIntent().getStringExtra("coupon_value");
        String c2 = getIntent().getStringExtra("coupon_value_discount");
        if(c1 != null && c2 != null)
        if(!c1.equals("null") && !c2.equals("null")) {
            coupon_value.setText(c1.replace("." , ",") + "€");
            coupon_value_discount.setText(c2.replace("." , ",") + "€");
        }else  coupon_container.setVisibility(View.GONE);


        // Prices
        StringBuilder _cart_data_ = new StringBuilder("");
        String[][] assortments = (String[][]) getIntent().getSerializableExtra("assortments");
        String lowest_price = "5555";
        String lowest_supplier = null;
        int ii = 0;
        for(int i=0; i<assortments.length; i++)
        if(assortments[i][0] == null) continue;
        else{
            if(Double.parseDouble(assortments[i][1]) < Double.parseDouble(lowest_price)){
                lowest_price = assortments[i][1];
                lowest_supplier = assortments[i][0];
            }
        }
        _cart_data_.append(lowest_price + " " + lowest_supplier.replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "") + " " + image_url + " " + product_id + "\n");
        for(int i=0; i<assortments.length; i++)
            if(assortments[i][0] == null) continue;
        else {
                LinearLayout ln = new LinearLayout(getBaseContext());
                ln.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (ii == 0) layoutParams.setMargins(dpToPx(10), 0, 0, 0);
                else layoutParams.setMargins(dpToPx(18), 0, 0, 0);
                ln.setLayoutParams(layoutParams);
                ln.setGravity(Gravity.CENTER);
                ii++;


                ImageView image = new ImageView(ln.getContext());
                if (assortments[i][0].equals("MYMARKET"))
                    image.setImageResource(R.drawable.mymarket);
                if (assortments[i][0].equals("ΜΑΣΟΥΤΗΣ"))
                    image.setImageResource(R.drawable.masouths);
                if (assortments[i][0].equals("E-FRESH.GR"))
                    image.setImageResource(R.drawable.efresh);
                if (assortments[i][0].equals("ΓΑΛΑΞΙΑΣ"))
                    image.setImageResource(R.drawable.galaxias);
                if (assortments[i][0].equals("MARKET IN"))
                    image.setImageResource(R.drawable.market_in);
                if (assortments[i][0].equals("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ"))
                    image.setImageResource(R.drawable.ab);
                if (assortments[i][0].equals("ΣΚΛΑΒΕΝΙΤΗΣ"))
                    image.setImageResource(R.drawable.sklavenitis_logo);
                image.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48)));

                TextView text = new TextView(ln.getContext());
                text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                text.setGravity(Gravity.CENTER);
                //text.setTextSize(dpToPx(4));
                text.setTextColor(Color.parseColor("#000000"));
                text.setTypeface(Typeface.DEFAULT_BOLD);
                text.setText(assortments[i][1].replace(".", ",") + " €");

                if (i == assortments.length - 1) _cart_data_.append(assortments[i][1] + " " + assortments[i][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ", "").trim());
                else  _cart_data_.append(assortments[i][1] + " " + assortments[i][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim() + "\n");

                ln.addView(image); ln.addView(text);
                product_prices.addView(ln);
        }



        // Καλαθι
        if (shopping_cart.contains(product_name_txt)){
            add_to_cart.setText("ΑΦΑΙΡΕΣΗ ΑΠΟ ΤΟ ΚΑΛΑΘΙ");
            add_to_cart.setBackgroundColor(Color.parseColor("#ff6666"));
        }else{
            add_to_cart.setText("ΠΡΟΣΘΗΚΗ ΣΤΟ ΚΑΛΑΘΙ");
            add_to_cart.setBackgroundColor(Color.parseColor("#559d76"));
        }
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(add_to_cart.getText().equals("ΠΡΟΣΘΗΚΗ ΣΤΟ ΚΑΛΑΘΙ")){
                    shopping_cart.edit().putString(product_original_name , _cart_data_.toString().trim()).apply();

                    add_to_cart.setText("ΑΦΑΙΡΕΣΗ ΑΠΟ ΤΟ ΚΑΛΑΘΙ");
                    add_to_cart.setBackgroundColor(Color.parseColor("#ff6666"));
                    Shopping_cart_amount.edit().putString(product_name_txt , "1").apply();
                }else{
                    add_to_cart.setText("ΠΡΟΣΘΗΚΗ ΣΤΟ ΚΑΛΑΘΙ");
                    shopping_cart.edit().remove(product_original_name).apply();

                    Shopping_cart_amount.edit().remove(product_original_name).apply();
                    add_to_cart.setBackgroundColor(Color.parseColor("#559d76"));
                }
                int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
                if(size == 0) {
                    productAmount.setVisibility(View.GONE);
                }
                else {
                    productAmount.setVisibility(View.VISIBLE);
                    productAmount.setText(String.valueOf(size));

                }
            }
        });
        FrameLayout cart = findViewById(R.id.cart_container);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ShoppingCart.class);
                startActivity(intent);
                overridePendingTransition(0 , 0);
            }
        });
        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) {
            productAmount.setVisibility(View.GONE);

        }
        else {

            productAmount.setVisibility(View.VISIBLE);
            productAmount.setText(String.valueOf(size));

        }

        // Αγαπημενο
        //favorites_btn
        ImageView fv = findViewById(R.id.favorites_btn);
        if (favorites.contains(product_original_name)) {
            fv.setImageResource(R.drawable.favorites_red);
        } else {
            fv.setImageResource(R.drawable.favorites);
        }
        fv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorites.contains(product_original_name)) {
                    favorites.edit().remove(product_original_name).apply();
                    fv.setImageResource(R.drawable.favorites);

                } else {
                    favorites.edit().putString(product_original_name, product_id).apply();
                    fv.setImageResource(R.drawable.favorites_red);

                }
            }
        });


        product_desc.post(new Runnable() {
            @Override
            public void run() {
                if(product_desc.getText().equals(product_name_txt)){
                    product_desc.setVisibility(View.GONE);
                    product_show_more_desc.setVisibility(View.GONE);
                    return;
                }
                if(product_desc.getLineCount() >= 6)
                    product_show_more_desc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(product_desc.getMaxLines() == 6){
                                product_desc.setMaxLines(Integer.MAX_VALUE);
                                product_show_more_desc.setText("...Λιγότερα");
                            }else{
                                product_desc.setMaxLines(6);
                                product_show_more_desc.setText("Περισσότερα...");
                            }
                        }
                    });
                else product_show_more_desc.setVisibility(View.GONE);

            }
        });
        Glide.with(getBaseContext()).load(image_url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontAnimate().into(product_image);
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new SetRecommented("https://v8api.pockee.com/api/v8/public/products?brand_ids=" + brand_id + "&page=1&per_page=10&in_stock=true" , findViewById(R.id.recommended_shimmer) , findViewById(R.id.product_recommended_recycler) , new ArrayList<>()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shopping_cart.contains(product_name_txt)){
            add_to_cart.setText("ΑΦΑΙΡΕΣΗ ΑΠΟ ΤΟ ΚΑΛΑΘΙ");
            add_to_cart.setBackgroundColor(Color.parseColor("#ff6666"));
        }else{
            add_to_cart.setText("ΠΡΟΣΘΗΚΗ ΣΤΟ ΚΑΛΑΘΙ");
            add_to_cart.setBackgroundColor(Color.parseColor("#559d76"));
        }
        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) {
            productAmount.setVisibility(View.GONE);
        }
        else {
            productAmount.setVisibility(View.VISIBLE);
            productAmount.setText(String.valueOf(size));

        }


    }

    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}

    private void setBorderOnScroll() {
        ScrollView scrl = findViewById(R.id.product_scroller);
        LinearLayout ln = findViewById(R.id.TopNAV);
        scrl.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrl.getScrollY() > 0)ln.setBackgroundResource(R.drawable.shadow_border_bottom);
                else ln.setBackground(null);
            }
        });
    }

    private void InitAdsMain(){
        MobileAds.initialize(ProductView.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView_product);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mAdView.setVisibility(View.GONE);
            }
        });
    }

    private class SetRecommented extends AsyncTask<String,String, JSONObject>{

        private List<ProductClass> product_list;
        private RecyclerView RecommentedRecycler;
        private ShimmerFrameLayout ShimmerContainer;
        private String API;


        SetRecommented(String API , ShimmerFrameLayout ShimmerContainer , RecyclerView RecommentedRecycler , List<ProductClass> product_list){
            this.API = API;
            this.ShimmerContainer = ShimmerContainer;
            this.RecommentedRecycler = RecommentedRecycler;
            this.product_list = product_list;
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
            if(JSON_OBJECT == null){
                ShimmerContainer.stopShimmer();
                ShimmerContainer.setVisibility(View.GONE);
            }


            try {
                JSONArray data = JSON_OBJECT.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject product = data.getJSONObject(i);
                    String product_id = product.getString("id");
                    String product_name = product.getString("name");
                    String product_desc = product.getString("description");
                    String product_brand = product.getString("brand_id");
                    if(product_name.equals(product_name_txt)) continue;
                    JSONArray ASSORTMENTS =  product.getJSONArray("assortments");
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    String product_img = null;
                    String coupon_value = "null";
                    String coupon_value_discount = "null";


                    if(product.getJSONArray("coupons").length() > 0){
                        JSONArray J = product.getJSONArray("coupons");
                        JSONObject P = J.getJSONObject(0);
                        double v1 = P.getDouble("value");
                        double v2 = P.getDouble("value_discount");
                        if (v1 > 0) coupon_value = new DecimalFormat("#0.00").format(v1);
                        if (v2 > 0) coupon_value_discount = new DecimalFormat("#0.00").format(v2);
                    }
                    if(!product.isNull("image_versions"))
                        product_img = product.getJSONObject("image_versions").getString("original");
                    else product_img = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";
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
                    model.setMarket(FINAL_NAME.trim());
                    model.setID(product_id);
                    model.setASSORTEMTNS_DATA(ASSORTEMTNS_DATA);
                    model.setDesc(product_desc);
                    model.setOrigianlName(product_name);
                    model.setPrice(String.valueOf(FINAL_PRICE) + " €");
                    model.setBrand_id(product_brand);
                    model.setValue_discount(coupon_value_discount);
                    model.setCoupon_value(coupon_value);
                    product_list.add(model);
                }
                Collections.sort(product_list, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });
                Adaptery adp = new Adaptery(getBaseContext(), product_list);
                if(ShimmerContainer!=null)  RecommentedRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                RecommentedRecycler.setAdapter(adp);
                adp.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        Intent intent = new Intent(getBaseContext(), ProductView.class);
                        intent.putExtra("original_name" , product_list.get(position).getOrigianlName());
                        intent.putExtra("id" , product_list.get(position).getID());
                        intent.putExtra("name" , product_list.get(position).getName());
                        intent.putExtra("img" , product_list.get(position).getUrl());
                        //intent.putExtra("desc" , dairy_01_list.get(position).get());
                        intent.putExtra("assortments" , product_list.get(position).getASSORTEMTNS_DATA());
                        intent.putExtra("desc" , product_list.get(position).getDesc());
                        intent.putExtra("brand_id" , product_list.get(position).getBrand_id());
                        intent.putExtra("coupon_value" , product_list.get(position).getCoupon_value());
                        intent.putExtra("coupon_value_discount" , product_list.get(position).getValue_discount());
                        startActivity(intent);
                    }
                });


                /*
                rv_01.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    boolean isLoading = false;
                    static int page = 2;
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        int visibleThreshold = 5; // Adjust this value as needed

                        if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + visibleThreshold)) {
                            isLoading = true;
                            new AsyncProductUpdate(API.replaceAll("&page=1", "&page=" + page), adp)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            page++;
                            isLoading = false;
                        }
                    }
                });


                 */


            }catch (Exception e){e.printStackTrace();}
            if(ShimmerContainer!=null) {
                ShimmerContainer.stopShimmer();
                ShimmerContainer.setVisibility(View.GONE);
            }

        }



    }


}
