package com.app.marketpal.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.RecentlyViewerDatabase;
import com.app.marketpal.enumActivities.ActivityType;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    @SuppressLint("SuspiciousIndentation")
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


        ProductClass PRODUCT_OBJECT = (ProductClass) getIntent().getSerializableExtra("PRODUCT_OBJ");

        product_name_txt = PRODUCT_OBJECT.getName();
        image_url = PRODUCT_OBJECT.getUrl();
        product_id = PRODUCT_OBJECT.getID();
        product_original_name = PRODUCT_OBJECT.getOrigianlName();
        brand_id = PRODUCT_OBJECT.getBrand_id();


        product_image = findViewById(R.id.Product_Image);
        product_desc = findViewById(R.id.product_desc);
        product_desc.setText(PRODUCT_OBJECT.getDesc());
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

        String c1 = PRODUCT_OBJECT.getCoupon_value();
        String c2 = PRODUCT_OBJECT.getValue_discount();
        if(c1 != null && c2 != null)
        if(!c1.equals("null") && !c2.equals("null")) {
            System.out.println(c1 + " ---HERE " + c2);
            coupon_value.setText(c1.replace("." , ",") + "€");
            coupon_value_discount.setText(c2.replace("." , ",") + "€");
        }else  coupon_container.setVisibility(View.GONE);
        else coupon_container.setVisibility(View.GONE);

        // Prices
        StringBuilder _cart_data_ = new StringBuilder("");
        String[][] assortments = (String[][]) PRODUCT_OBJECT.getASSORTEMTNS_DATA();
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
                switch (assortments[i][0]) {
                    case "MYMARKET": image.setImageResource(R.drawable.mymarket); break;
                    case "ΜΑΣΟΥΤΗΣ": image.setImageResource(R.drawable.masouths); break;
                    case "ΓΑΛΑΞΙΑΣ":  image.setImageResource(R.drawable.galaxias); break;
                    case "MARKET IN": image.setImageResource(R.drawable.market_in); break;
                    case "ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ": image.setImageResource(R.drawable.ab); break;
                    case "ΣΚΛΑΒΕΝΙΤΗΣ": image.setImageResource(R.drawable.sklavenitis_logo); break;
                    default: break;
                }

                image.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48)));

                TextView text = new TextView(ln.getContext());
                text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                text.setGravity(Gravity.CENTER);
                //text.setTextSize(dpToPx(4));
                text.setTextColor(Color.parseColor("#000000"));
                text.setTypeface(Typeface.DEFAULT_BOLD);
                String priceString = assortments[i][1].replace(".", ",");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
                double price = 0;
                try {
                    price = decimalFormat.parse(priceString).doubleValue();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String formattedPrice = decimalFormat.format(price);
                text.setText(formattedPrice + " €");

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
        add_to_cart.setOnClickListener(v -> {
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
        });

        findViewById(R.id.cart_container).setOnClickListener(v -> {
                Intent intent = new Intent(getBaseContext(), ShoppingCart.class);
                startActivity(intent);
                overridePendingTransition(0 , 0);
        });
        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) productAmount.setVisibility(View.GONE);
        else {

            productAmount.setVisibility(View.VISIBLE);
            productAmount.setText(String.valueOf(size));

        }

        // Αγαπημενο
        //favorites_btn
        ImageView fv = findViewById(R.id.favorites_btn);
        if (favorites.contains(product_original_name)) fv.setImageResource(R.drawable.favorites_red);
        else fv.setImageResource(R.drawable.favorites);
        fv.setOnClickListener(v -> {
                if (favorites.contains(product_original_name)) {
                    favorites.edit().remove(product_original_name).apply();
                    fv.setImageResource(R.drawable.favorites);
                } else {
                    favorites.edit().putString(product_original_name, product_id).apply();
                    fv.setImageResource(R.drawable.favorites_red);
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
                    product_show_more_desc.setOnClickListener(v -> {
                            if(product_desc.getMaxLines() == 6){
                                product_desc.setMaxLines(Integer.MAX_VALUE);
                                product_show_more_desc.setText("...Λιγότερα");
                            }else{
                                product_desc.setMaxLines(6);
                                product_show_more_desc.setText("Περισσότερα...");
                            }
                    });
                else product_show_more_desc.setVisibility(View.GONE);
            }
        });
        Glide.with(getBaseContext()).load(image_url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontAnimate().into(product_image);
        findViewById(R.id.backButton).setOnClickListener(v -> { finish(); });
        new SetRecommented("https://v8api.pockee.com/api/v8/public/products?brand_ids=" + brand_id + "&page=1&per_page=10&in_stock=true" , findViewById(R.id.recommended_shimmer) , findViewById(R.id.product_recommended_recycler) , new ArrayList<>()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
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

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            try {
                JSONArray data = JSON_OBJECT.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject product = data.getJSONObject(i);
                    String productImg;
                    String couponValue = "null";
                    String couponValueDiscount = "null";
                    double finalPrice = Double.MAX_VALUE;
                    String finalName = "";
                    JSONArray assortments = product.getJSONArray("assortments");
                    String[][] assortmentsData = new String[assortments.length()][2];

                    if (product.has("coupons") && product.getJSONArray("coupons").length() > 0) {
                        JSONObject coupon = product.getJSONArray("coupons").getJSONObject(0);
                        double value = coupon.getDouble("value");
                        double valueDiscount = coupon.getDouble("value_discount");
                        if (value > 0) couponValue = decimalFormat.format(value);
                        if (valueDiscount > 0) couponValueDiscount = decimalFormat.format(valueDiscount);
                    }

                    if (!product.isNull("image_versions")) {
                        productImg = product.getJSONObject("image_versions").getString("original");
                    } else {
                        productImg = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";
                    }

                    for (int j = 0; j < assortments.length(); j++) {
                        JSONObject item = assortments.getJSONObject(j);
                        JSONObject retailer = item.getJSONObject("retailer");
                        JSONObject productPivot = item.getJSONObject("product_pivot");
                        String name = retailer.getString("name");
                        if (!supermarkets.contains(name)) continue;

                        double currentFinalPrice = productPivot.isNull("final_price")
                                ? productPivot.getDouble("start_price")
                                : productPivot.getDouble("final_price");
                        if (currentFinalPrice < finalPrice) {
                            finalName = name;
                            finalPrice = currentFinalPrice;
                        }
                        assortmentsData[j][0] = name;
                        assortmentsData[j][1] = String.valueOf(currentFinalPrice);
                    }
                    if (finalPrice == Double.MAX_VALUE) continue;

                    ProductClass model = new ProductClass();
                    model.setName(product.getString("name"));
                    model.setUrl(productImg);
                    model.setMarket(finalName.trim());
                    model.setID(product.getString("id"));
                    model.setASSORTEMTNS_DATA(assortmentsData);
                    model.setDesc(product.getString("description"));
                    model.setOrigianlName(product.getString("name"));
                    model.setPrice(finalPrice + " €");
                    model.setBrand_id( product.getString("brand_id"));
                    model.setValue_discount(couponValueDiscount);
                    model.setCoupon_value(couponValue);
                    product_list.add(model);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    product_list.sort(Comparator.comparingDouble(product ->
                            Double.parseDouble(product.getPrice().replace(" €", ""))
                    ));
                } else {
                    Collections.sort(product_list, new Comparator<ProductClass>() {
                        @Override
                        public int compare(ProductClass p1, ProductClass p2) {
                            double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                            double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                            return Double.compare(price1, price2);
                        }
                    });
                }
                Adaptery adp = new Adaptery(getBaseContext(), product_list, ActivityType.MAIN_ACTIVITY);
                if(ShimmerContainer!=null)  RecommentedRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                RecommentedRecycler.setAdapter(adp);
                adp.setOnClickListener((position, model) -> {
                    Intent intent = new Intent(getBaseContext(), ProductView.class);
                    intent.putExtra("PRODUCT_OBJ", model);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                });

            }catch (Exception e){e.printStackTrace();}
            if(ShimmerContainer!=null) {
                ShimmerContainer.stopShimmer();
                ShimmerContainer.setVisibility(View.GONE);
            }

        }



    }


}
