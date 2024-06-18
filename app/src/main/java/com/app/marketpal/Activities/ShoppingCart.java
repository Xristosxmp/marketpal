package com.app.marketpal.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ShoppingCart extends AppCompatActivity {


    private SharedPreferences shopping_cart;
    private SharedPreferences shopping_cart_amount;
    private LinearLayout parent;
    private Map<String, Map<String, Double>> products = new HashMap<>();
    private Map<String, Double> supplierCosts = new HashMap<>();
    ImageView BEST_MARKET_IMAGE;
    private LinearLayout ln_parent;
    private TextView ShowAllProducts;
    private Map<String, List<String>> SUPP_PRODUCTS_COUNTER;
    private Map<String, List<String>> supplierProducts = new HashMap<>();
    private List<ProductClass> MY_MARKET_LIST;
    private List<ProductClass> SKLAVENITIS_LIST;
    private List<ProductClass> AB_LIST;
    private List<ProductClass> GALAXIAS_LIST;
    private List<ProductClass> MASOUTHS_LIST;
    private Adaptery MY_MARKET_ADAPTERY;
    private Adaptery SKLAVENITIS_ADAPTERY;
    private Adaptery AB_ADAPTERY;
    private Adaptery GALAXIAS_ADAPTERY;
    private Adaptery MASOUTHS_ADAPTERY;
    private RecyclerView MY_MARKET_RECYCLER;
    private RecyclerView SKLAVENITIS_RECYCLER;
    private RecyclerView AB_RECYCLER;
    private RecyclerView GALAXIAS_RECYCLER;
    private RecyclerView MASOUTHS_RECYCLER;
    double MY_MARKET_SUM = 0;
    double SKLAVENITIS_SUM = 0;
    double AB_SUM = 0;
    double GALAXIAS_SUM = 0;
    double MASOUTHS_SUM = 0;
    private Intent ProfileIntent;
    private Intent OptimalCartIntent;
    private Intent HomeIntent;
    private ArrayList<String> PRODUCT_LIST_IDS;

    @Override
    public void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_shoppingcart);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        shopping_cart_amount = getBaseContext().getSharedPreferences("shopping_cart_amount", Context.MODE_PRIVATE);
        PRODUCT_LIST_IDS = new ArrayList<>();

        InitAdsMain();

        ln_parent = new LinearLayout(getBaseContext());
        ln_parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ln_parent.setOrientation(LinearLayout.VERTICAL);


        ConfigureVariables();
        MyProductsInit();
        InitData();
        Navigation();

        findViewById(R.id.nav_to_optimal_cart).setOnClickListener(nav_to_optimal_cart_v -> {
                OptimalCartIntent = new Intent(getBaseContext() , optimal_cart.class);
                startActivity(OptimalCartIntent);
                overridePendingTransition(0,0);
        });
        ScrollView scrl = findViewById(R.id.cart_scroller);
        findViewById(R.id.scroll_to_prices).setOnClickListener(scroll_to_prices_v -> {scrl.smoothScrollTo(0 , (int)findViewById(R.id.prices_and_products_textview).getTop());});

        AdBottom();
        GoHome();
    }



    private void ConfigureVariables(){
        MY_MARKET_LIST = new ArrayList<>();
        SKLAVENITIS_LIST = new ArrayList<>();
        AB_LIST = new ArrayList<>();
        GALAXIAS_LIST = new ArrayList<>();
        MASOUTHS_LIST = new ArrayList<>();


        MY_MARKET_ADAPTERY = new Adaptery(this , MY_MARKET_LIST, ActivityType.MAIN_ACTIVITY);
        SKLAVENITIS_ADAPTERY = new Adaptery(this , SKLAVENITIS_LIST, ActivityType.MAIN_ACTIVITY);
        AB_ADAPTERY = new Adaptery(this , AB_LIST, ActivityType.MAIN_ACTIVITY);
        GALAXIAS_ADAPTERY = new Adaptery(this , GALAXIAS_LIST, ActivityType.MAIN_ACTIVITY);
        MASOUTHS_ADAPTERY = new Adaptery(this , MASOUTHS_LIST, ActivityType.MAIN_ACTIVITY);


        MY_MARKET_RECYCLER = findViewById(R.id.mymarket_recycler);
        SKLAVENITIS_RECYCLER = findViewById(R.id.sklaventis_recycler);
        AB_RECYCLER = findViewById(R.id.ab_recycler);
        GALAXIAS_RECYCLER = findViewById(R.id.galaxias_recycler);
        MASOUTHS_RECYCLER = findViewById(R.id.masouths_recycler);


        MY_MARKET_RECYCLER.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SKLAVENITIS_RECYCLER.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AB_RECYCLER.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        GALAXIAS_RECYCLER.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MASOUTHS_RECYCLER.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



        MY_MARKET_RECYCLER.setAdapter(MY_MARKET_ADAPTERY);
        SKLAVENITIS_RECYCLER.setAdapter(SKLAVENITIS_ADAPTERY);
        AB_RECYCLER.setAdapter(AB_ADAPTERY);
        GALAXIAS_RECYCLER.setAdapter(GALAXIAS_ADAPTERY);
        MASOUTHS_RECYCLER.setAdapter(MASOUTHS_ADAPTERY);


        MY_MARKET_RECYCLER.setItemAnimator(null);
        SKLAVENITIS_RECYCLER.setItemAnimator(null);
        AB_RECYCLER.setItemAnimator(null);
        GALAXIAS_RECYCLER.setItemAnimator(null);
        MASOUTHS_RECYCLER.setItemAnimator(null);


        MY_MARKET_SUM = 0;
        SKLAVENITIS_SUM = 0;
        AB_SUM = 0;
        GALAXIAS_SUM = 0;
        MASOUTHS_SUM = 0;
    }


    void AdBottom(){
        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(0, 16, 0, 16);
        LinearLayout ln = findViewById(R.id.shopping_cart_container);
        ln.addView(linearLayout);
    }

    private void Navigation(){
        LinearLayout ln = findViewById(R.id.shopping_cart_container);
        findViewById(R.id.clean_cart).setOnClickListener(clean_cart_v -> {
                Dialog dialog = new Dialog(ShoppingCart.this);
                dialog.setContentView(R.layout.clean_cart_layout);
                ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                InsetDrawable inset = new InsetDrawable(back, 30);
                dialog.getWindow().setBackgroundDrawable(inset);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(155));
                AppCompatButton btn_remove = dialog.findViewById(R.id.btn_delete);
                btn_remove.setOnClickListener(btn_remove_v -> {
                        shopping_cart.edit().clear().commit();
                        shopping_cart_amount.edit().clear().commit();
                        ln.removeAllViews();
                        InitData();
                        dialog.dismiss();
                        TextView amount__p = findViewById(R.id.products_amound_header); amount__p.setText("("  + shopping_cart.getAll().size() + ")");
                });
                AppCompatButton btn_cancle = dialog.findViewById(R.id.btn_cancel);
                btn_cancle.setOnClickListener(btn_cancle_v -> {dialog.dismiss();});

        });
        findViewById(R.id.go_to_profile).setOnClickListener(go_to_profile -> {
                    ProfileIntent = new Intent(getBaseContext() , Profile.class);
                    startActivity(ProfileIntent);
                    overridePendingTransition(0,0);
                    finish();
        });

    }


    void CreateProductsInsideMarkets(String supplier, LinearLayout ln){
        for (Map.Entry<String, Map<String, Double>> entry : products.entrySet()) {
            String product = entry.getKey();
            Map<String, Double> supplierPrices = entry.getValue();
            for (Map.Entry<String, Double> supplierEntry : supplierPrices.entrySet()) {
                String supplier_02 = supplierEntry.getKey();
                if(supplier.equals(supplier_02)){
                    ln.addView(CreateFullProduct(product , supplier));
                }
            }
        }

    }

    LinearLayout CreateFullProduct(String p_name , String supplier){
        LinearLayout ln = new LinearLayout(getBaseContext());
        ln = new LinearLayout(getBaseContext());
        ln.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.setPadding(0 , 5 , 0 , 0);


        ImageView product_img = new ImageView(getBaseContext());
        product_img.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(72), dpToPx(74)));

        String value = shopping_cart.getString(p_name, "");
        String[] lines = value.split("\n");

        Glide.with(this).load(lines[0].split(" " , 4)[2].trim())
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .error(R.drawable.product_not_found)
                .priority(Priority.HIGH)
                .into(product_img);
        ln.addView(product_img);

        String price_of_product_on_supplier = "0.0 Euro";
        for(int i=0; i<lines.length; i++){
            if(lines[i].split(" ")[1].equals(supplier)){
                price_of_product_on_supplier = lines[i].split(" ")[0];
            }
        }


        if (Build.VERSION.SDK_INT <= 24){
            AppCompatTextView product_name = new AppCompatTextView(getBaseContext());
            product_name.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    dpToPx(65)));
            product_name.setGravity(Gravity.CENTER);
            product_name.setText(p_name);
            product_name.setTextColor(Color.parseColor("#070606"));
            product_name.setAutoSizeTextTypeUniformWithConfiguration(9, 12, 1, TypedValue.COMPLEX_UNIT_SP);
            ln.addView(product_name);

            AppCompatTextView product_price_per_supplier = new AppCompatTextView(getBaseContext());
            product_price_per_supplier.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    dpToPx(65)));
            product_price_per_supplier.setGravity(Gravity.CENTER);
            product_price_per_supplier.setTextSize(dpToPx(9));
            product_price_per_supplier.setTypeface(Typeface.DEFAULT_BOLD);
            product_price_per_supplier.setText(price_of_product_on_supplier.replace(".", ",") + " €");
            product_price_per_supplier.setTextColor(Color.parseColor("#000000"));
            ln.addView(product_price_per_supplier);

        }
        else{
            TextView product_name = new TextView(getBaseContext());
            product_name.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(65)));
            product_name.setGravity(Gravity.CENTER);
            product_name.setAutoSizeTextTypeUniformWithConfiguration(
                    9, 12, 1, TypedValue.COMPLEX_UNIT_SP);
            product_name.setText(p_name);
            product_name.setTextColor(Color.parseColor("#070606"));
            ln.addView(product_name);

            TextView product_price_per_supplier = new TextView(getBaseContext());
            product_name.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(65)));
            product_price_per_supplier.setGravity(Gravity.CENTER);
            product_price_per_supplier.setTextSize(dpToPx(9));
            product_price_per_supplier.setTypeface(Typeface.DEFAULT_BOLD);
            product_price_per_supplier.setText(price_of_product_on_supplier.replace("." , ",") + " €");
            product_price_per_supplier.setTextColor(Color.parseColor("#000000"));
            ln.addView(product_price_per_supplier);

        }

        return ln;
    }

    void CreateImportantComp(String supplier){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        Set<String> allKeys = shopping_cart.getAll().keySet();

        LinearLayout ln_market = new LinearLayout(getBaseContext());
        layoutParams.setMargins(dpToPx(10) , 0 , 0 , dpToPx(5));
        ln_market.setOrientation(LinearLayout.HORIZONTAL);
        ln_market.setGravity(Gravity.CENTER);
        ln_market.setLayoutParams(layoutParams);

        ImageView MARKET_LOGO = new ImageView(getBaseContext());
        LinearLayout.LayoutParams img_view_params = new LinearLayout.LayoutParams(dpToPx(32) , dpToPx(32));
        MARKET_LOGO.setBackgroundResource(R.drawable.market_round_behind_background);
        MARKET_LOGO.setLayoutParams(img_view_params);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String supplier_02_t_data = "\nΠΑΡΕΧΕΙ "+(SUPP_PRODUCTS_COUNTER.get(supplier).size()+1)/2 + " ΣΤΑ " +  allKeys.size() + " ΠΡΟΙΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ";
        SpannableString supplier_02_t = new SpannableString(supplier_02_t_data);
        supplier_02_t.setSpan(new ForegroundColorSpan(Color.parseColor("#7f7f7f")), 0, supplier_02_t_data.length(), 0);
        supplier_02_t.setSpan(new AbsoluteSizeSpan(13, true), 0, supplier_02_t_data.length(), 0);
        TextView MARKET = new TextView(getBaseContext());
        MARKET.setLayoutParams(layoutParams);
        //MARKET.setTextColor(Color.parseColor("#4B4B4B"));
        MARKET.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        MARKET.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        MARKET.setPadding(dpToPx(4), 15, 0, 0);
        //MARKET.setText(supplier + " • ");
        MARKET.setTextSize(20);
        MARKET.setTypeface(null, Typeface.BOLD);
        ln_market.addView(MARKET_LOGO);
        ln_market.addView(MARKET);
        ln_parent.addView(ln_market);
        for (Map.Entry<String, Double> entry : supplierCosts.entrySet()) {
            String supplier_02 = entry.getKey();
            double cost = entry.getValue();
            if(supplier.equals(supplier_02)){
                supplier = supplier + " • ";
                SpannableString supplier_01 = new SpannableString(supplier);
                supplier_01.setSpan(new ForegroundColorSpan(Color.parseColor("#151515")), 0, supplier.length(), 0);
                supplier_01.setSpan(new AbsoluteSizeSpan(20, true), 0, supplier.length(), 0);
                SpannableString supplier_03 = new SpannableString(new DecimalFormat("#0.00").format(cost).replace("." , ",") + " €");
                supplier_03.setSpan(new ForegroundColorSpan(Color.parseColor("#1564a2")), 0, supplier_03.length(), 0);
                supplier_03.setSpan(new AbsoluteSizeSpan(20, true), 0, supplier_03.length(), 0);
                builder.append(supplier_01);
                builder.append(supplier_03);
                builder.append(supplier_02_t);
                MARKET.setText(builder, TextView.BufferType.SPANNABLE);;
            }
        }
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getBaseContext());
        horizontalScrollView.setId(View.generateViewId());
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(200));
        layoutParams.setMargins(0, 0, 0, 0);
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setOverScrollMode(HorizontalScrollView.OVER_SCROLL_NEVER);
        horizontalScrollView.setScrollbarFadingEnabled(false);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setScrollBarSize(4);
        horizontalScrollView.setBackgroundResource(R.drawable.rect);

        LinearLayout ln = new LinearLayout(this);
        ln.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setGravity(Gravity.CENTER);
        horizontalScrollView.addView(ln);
        CreateProductsInsideMarkets(supplier.split(" ")[0] , ln);

        if(Build.VERSION.SDK_INT >= 24) {
            Drawable thumbDrawable = getResources().getDrawable(R.drawable.round_scrollbar_horizontal);
            if (thumbDrawable != null) {
                horizontalScrollView.setHorizontalScrollbarThumbDrawable(thumbDrawable);
            }
            int trackColor = getResources().getColor(R.color.smoother_gray);
            horizontalScrollView.setHorizontalScrollbarTrackDrawable(new ColorDrawable(trackColor));
        }
        ln_parent.addView(horizontalScrollView);


    }

    void GoHome(){
        findViewById(R.id.CartToHome).setOnClickListener(v -> {
            if(getIntent().getStringExtra("activity") != null)
            switch (getIntent().getStringExtra("activity")){
                case "main": startActivity(new Intent(this , MainActivity.class)); break;
                case "profile": startActivity(new Intent(this , Profile.class)); break;
                case "offers": startActivity(new Intent(this , Offers_activity.class)); break;
                default: onBackPressed(); break;
            } else startActivity(new Intent(this , MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
    void MyProductsInit(){
        parent = findViewById(R.id.shopping_cart_container);
        String product_name = "";
        String product_best_price = "";
        String product_best_market = "";
        String product_img = "";
        String product_id = "";
        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        Set<String> allKeys = shopping_cart.getAll().keySet();
        TextView amount__p = findViewById(R.id.products_amound_header); amount__p.setText("("  + allKeys.size() + ")");

        double total_price_from_products = 0f;

        for (String key : allKeys) {

            String value = shopping_cart.getString(key, "");
            String[] lines = value.split("\n");
            product_name = key;
            product_best_price = lines[0].split(" " , 4)[0];
            product_best_market = lines[0].split(" " , 4)[1];
            product_img = lines[0].split(" " , 4)[2].trim();
            product_id  = lines[0].split(" " , 4)[3].trim();
            PRODUCT_LIST_IDS.add(product_id);

            for(int i=1; i<lines.length; i++)
                addProductPrice(products , key , lines[i].split(" ")[1] , Double.parseDouble(lines[i].split(" ")[0]));


            parent.addView(ln_create(key ,  product_img , product_best_price , product_best_market));
        }


        SUPP_PRODUCTS_COUNTER = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : products.entrySet()) {
            String product = entry.getKey();
            Map<String, Double> supplierPrices = entry.getValue();

            for (Map.Entry<String, Double> supplierEntry : supplierPrices.entrySet()) {
                String supplier = supplierEntry.getKey();
                double price = supplierEntry.getValue() * Integer.parseInt(shopping_cart_amount.getString(product , "1").toString());

                double totalCost;
                if (supplierCosts.containsKey(supplier)) {
                    totalCost = supplierCosts.get(supplier);
                } else {
                    totalCost = 0.0;
                }
                totalCost += price;
                supplierCosts.put(supplier, totalCost);
                // Save the product for the supplier
                if (supplierProducts.containsKey(supplier)) {
                    supplierProducts.get(supplier).add(product);
                    SUPP_PRODUCTS_COUNTER.get(supplier).add(product);
                } else {
                    List<String> productsList = new ArrayList<>();
                    productsList.add(product);
                    supplierProducts.put(supplier, productsList);
                    SUPP_PRODUCTS_COUNTER.put(supplier , productsList);

                }
            }
        }
        String bestSupplier = "";
        double lowestCost = Double.MAX_VALUE;
        int maxProductCount = 0;


        for (List<String> productList : supplierProducts.values()) {
            int productCount = productList.size();
            if (productCount > maxProductCount) {
                maxProductCount = productCount;
            }
        }

        //supplierProducts.get(supplier).size() <= KEY
        for (Map.Entry<String, Double> entry : supplierCosts.entrySet()) {
            String supplier = entry.getKey();
            double cost = entry.getValue();

            if (supplierProducts.containsKey(supplier) && supplierProducts.get(supplier).size() == maxProductCount) {
                if (cost < lowestCost) {
                    lowestCost = cost;
                    bestSupplier = supplier;
                }
            }
        }




        if(allKeys.size() == 0){
            findViewById(R.id.empty_shopping_cart).setVisibility(View.VISIBLE);
            return;
        }

    }
    private static void addProductPrice(Map<String, Map<String, Double>> products, String product, String supplier, double price) {
        if (Build.VERSION.SDK_INT <= 23) {
            ArrayMap<String, Double> supplierPrices = (ArrayMap<String, Double>) products.get(product);
            if (supplierPrices == null) {
                supplierPrices = new ArrayMap<>();
                products.put(product, supplierPrices);
            }
            supplierPrices.put(supplier, price);
        } else {
            Map<String, Double> supplierPrices = products.getOrDefault(product, new HashMap<>());
            supplierPrices.put(supplier, price);
            products.put(product, supplierPrices);
        }
    }
    LinearLayout ln_create(String p_name , String p_img , String p_price , String p_market){
        LinearLayout ln = new LinearLayout(getBaseContext());
        ln.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setGravity(Gravity.LEFT);
        ln.setPadding(16 , 16 ,16 ,16);
        //ln.setBackgroundResource(R.drawable.shadow_border_bottom);

        ImageView img = new ImageView(getBaseContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(72), ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        img.setLayoutParams(params);
        img.setAdjustViewBounds(true);
        ImageLoader(p_img , img);
        ln.addView(img);

        LinearLayout data_holder = new LinearLayout(getBaseContext());
        LinearLayout.LayoutParams parm = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        parm.setMargins(dpToPx(5),dpToPx(10),dpToPx(20),dpToPx(10));
        data_holder.setLayoutParams(parm);
        data_holder.setOrientation(LinearLayout.VERTICAL);
        data_holder.setPadding(0,0,0,dpToPx(15));
        data_holder.setBackgroundResource(R.drawable.shadow_border_bottom);
        data_holder.setGravity(Gravity.LEFT);

        LinearLayout price_and_logo = new LinearLayout(getBaseContext());
        price_and_logo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT));
        price_and_logo.setOrientation(LinearLayout.HORIZONTAL);
        price_and_logo.setGravity(Gravity.LEFT);



        LinearLayout.LayoutParams p_price_txt_params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);

        TextView p_price_txt = new TextView(getBaseContext());
        p_price_txt.setLayoutParams(p_price_txt_params);
        p_price_txt.setText(p_price.replace(".", ",") + " €");
        p_price_txt.setTextSize(20);
        p_price_txt.setTypeface(null, Typeface.BOLD);
        p_price_txt.setTextColor(Color.parseColor("#000000"));
        //p_price_txt.setPadding(0 , 0 ,dpToPx(10) , 0);
        price_and_logo.addView(p_price_txt);
        data_holder.addView(price_and_logo);


        TextView p_name_txt = new TextView(getBaseContext());
        LinearLayout.LayoutParams p_name_txt_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        p_name_txt_params.setMargins(0 , dpToPx(5) , 0 , 0);
        p_name_txt.setLayoutParams(p_name_txt_params);
        p_name_txt.setText(p_name);
        p_name_txt.setTextColor(Color.parseColor("#070606"));
        data_holder.addView(p_name_txt);


        SetModifierForAmount(price_and_logo , p_name);

        ln.addView(data_holder);

        return ln;
    }
    void ImageLoader(String img_url , ImageView v){
        Glide.with(getBaseContext()).load(img_url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontAnimate().placeholder(R.drawable.product_placeholder).into(v);
    }
    void SetModifierForAmount(LinearLayout price_and_logo , String p_name){

        LinearLayout ModifyLayout = new LinearLayout(getBaseContext());
        LinearLayout.LayoutParams ModifyLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , dpToPx(25));
        ModifyLayoutParams.setMargins(dpToPx(5) , 0 , 0 , 0);
        ModifyLayout.setLayoutParams(ModifyLayoutParams);
        ModifyLayout.setBackgroundResource(R.drawable.round_border_for_modify_product_cart);
        ModifyLayout.setGravity(Gravity.RIGHT);
        ModifyLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btn_decrease = new ImageButton(ModifyLayout.getContext());
        ImageButton btn_increase = new ImageButton(ModifyLayout.getContext());
        TextView product_amount_text = new TextView(ModifyLayout.getContext());
        product_amount_text.setText(getBaseContext().getSharedPreferences("shopping_cart_amount", Context.MODE_PRIVATE).getString(p_name , "1"));
        product_amount_text.setGravity(Gravity.CENTER);
        product_amount_text.setTypeface(Typeface.DEFAULT_BOLD);
        product_amount_text.setTextColor(Color.parseColor("#000000"));


        LinearLayout.LayoutParams btn_decrease_params = new LinearLayout.LayoutParams(dpToPx(15) , ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams btn_increase_params = new LinearLayout.LayoutParams(dpToPx(15) , ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams product_amount_text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //product_amount_text_params.setMargins(20 , 0 , 20 , 0);


        btn_decrease_params.setMargins(dpToPx(9) , 0 , dpToPx(5) , 0);
        btn_increase_params.setMargins(dpToPx(5) , 0 , dpToPx(9) , 0);
        product_amount_text.setPadding(20 , 0 , 20 , 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btn_decrease.setBackgroundResource(outValue.resourceId);

            outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btn_increase.setBackgroundResource(outValue.resourceId);
        }


        btn_decrease.setLayoutParams(btn_decrease_params);
        product_amount_text.setLayoutParams(product_amount_text_params);
        btn_increase.setLayoutParams(btn_increase_params);

        product_amount_text.setBackgroundResource(R.drawable.right_border);
        btn_decrease.setBackgroundResource(0);
        btn_increase.setBackgroundResource(0);


        Glide.with(this)
                    .load(Integer.parseInt(product_amount_text.getText().toString()) == 1
                            ? R.drawable.bin_shopping_cart
                            : R.drawable.decrease_icon24)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .error(R.drawable.product_not_found)
                    .priority(Priority.HIGH)
                    .into(btn_decrease);
        Glide.with(this).load(R.drawable.increase_icon24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .priority(Priority.HIGH)
                .into(btn_increase);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btn_decrease.setBackgroundResource(outValue.resourceId);
            outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            btn_increase.setBackgroundResource(outValue.resourceId);
        }

        btn_decrease.setOnClickListener(v -> {

                int result = Integer.parseInt(product_amount_text.getText().toString()) - 1;
                if(result == 1)
                    Glide.with(this).load(R.drawable.bin_shopping_cart)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .into(btn_decrease);
                else if(result == 2)
                    Glide.with(this).load(R.drawable.decrease_icon24)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .into(btn_decrease);


                if(result <= 0){

                    Dialog dialog = new Dialog(ShoppingCart.this);
                    dialog.setContentView(R.layout.delete_layout);
                    ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                    InsetDrawable inset = new InsetDrawable(back, 30);
                    dialog.getWindow().setBackgroundDrawable(inset);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(155));

                    AppCompatButton btn_remove = dialog.findViewById(R.id.btn_delete);
                    btn_remove.setOnClickListener(btn_remove_v -> {
                            parent = findViewById(R.id.shopping_cart_container);
                            SharedPreferences srd = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shopping_cart.edit();
                            editor.remove(p_name);
                            editor.apply();
                            parent.removeView((LinearLayout)price_and_logo.getParent().getParent());
                            ln_parent.removeAllViews();
                            String product_name = "";
                            String product_best_price = "";
                            String product_best_market = "";
                            String product_img = "";
                            String product_id = "";
                            products.clear();
                            supplierCosts.clear();
                            SUPP_PRODUCTS_COUNTER.clear();
                            supplierProducts.clear();
                            PRODUCT_LIST_IDS.clear();
                            shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
                            Set<String> allKeys = shopping_cart.getAll().keySet();
                            TextView amount__p = findViewById(R.id.products_amound_header); amount__p.setText("("  + allKeys.size() + ")");
                            double total_price_from_products = 0f;
                            int index = 0;
                            for (String key : allKeys) {
                                index++;
                                String value = shopping_cart.getString(key, "");
                                String[] lines = value.split("\n");
                                product_name = key;
                                product_best_price = lines[0].split(" " , 4)[0];
                                product_best_market = lines[0].split(" " , 4)[1];
                                product_img = lines[0].split(" " , 4)[2].trim();
                                product_id  = lines[0].split(" " , 4)[3].trim();
                                PRODUCT_LIST_IDS.add(product_id);
                                for(int i=1; i<lines.length; i++)
                                    addProductPrice(products , key , lines[i].split(" ")[1] , Double.parseDouble(lines[i].split(" ")[0]));
                            }

                            for (Map.Entry<String, Map<String, Double>> entry : products.entrySet()) {
                                String product = entry.getKey();
                                Map<String, Double> supplierPrices = entry.getValue();
                                for (Map.Entry<String, Double> supplierEntry : supplierPrices.entrySet()) {
                                    String supplier = supplierEntry.getKey();
                                    double price = supplierEntry.getValue() * Integer.parseInt(shopping_cart_amount.getString(product , "1").toString());
                                    double totalCost;
                                    if (supplierCosts.containsKey(supplier)) totalCost = supplierCosts.get(supplier);
                                    else totalCost = 0.0;
                                    totalCost += price;
                                    supplierCosts.put(supplier, totalCost);
                                    if (supplierProducts.containsKey(supplier)) {
                                        supplierProducts.get(supplier).add(product);
                                        SUPP_PRODUCTS_COUNTER.get(supplier).add(product);
                                    } else {
                                        List<String> productsList = new ArrayList<>();
                                        productsList.add(product);
                                        supplierProducts.put(supplier, productsList);
                                        SUPP_PRODUCTS_COUNTER.put(supplier , productsList);
                                    }
                                }
                            }
                            String bestSupplier = "";
                            double lowestCost = Double.MAX_VALUE;
                            int maxProductCount = 0;
                            for (List<String> productList : supplierProducts.values()) {
                                int productCount = productList.size();
                                if (productCount > maxProductCount) maxProductCount = productCount;
                            }

                            for (Map.Entry<String, Double> entry : supplierCosts.entrySet()) {
                                String supplier = entry.getKey();
                                double cost = entry.getValue();
                                if (supplierProducts.containsKey(supplier) && supplierProducts.get(supplier).size() == maxProductCount) {
                                    if (cost < lowestCost) {
                                        lowestCost = cost;
                                        bestSupplier = supplier;
                                    }
                                }
                            }
                            InitData();
                            dialog.dismiss();
                    });

                    AppCompatButton btn_cancle = dialog.findViewById(R.id.btn_cancel);
                    btn_cancle.setOnClickListener(btn_cancle_click -> {dialog.dismiss();});
                    return;
                }
                String data = String.valueOf(result);
                product_amount_text.setText(data);
                SharedPreferences.Editor editor = shopping_cart_amount.edit();
                editor.putString(p_name , data);
                editor.apply();
                InitData();
        });
        btn_increase.setOnClickListener(btn_increase_v -> {
                int result = Integer.parseInt(product_amount_text.getText().toString()) + 1;
                if(result == 1)
                    Glide.with(this).load(R.drawable.bin_shopping_cart)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .into(btn_decrease);
                else if(result == 2)
                    Glide.with(this).load(R.drawable.decrease_icon24)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .into(btn_decrease);

            if(result == 100) return;
                String data = String.valueOf(result);
                product_amount_text.setText(data);
                SharedPreferences.Editor editor = shopping_cart_amount.edit();
                editor.putString(p_name , data);
                editor.apply();
                InitData();
        });

        ModifyLayout.addView(btn_decrease);
        ModifyLayout.addView(product_amount_text);
        ModifyLayout.addView(btn_increase);

        price_and_logo.addView(ModifyLayout);
    }
    private void InitAdsMain(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {
                AdView mAdView = findViewById(R.id.cart_ads);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });



    }
    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}
    private void InitData(){

        MY_MARKET_LIST.clear(); MY_MARKET_SUM = 0;
        SKLAVENITIS_LIST.clear(); SKLAVENITIS_SUM = 0;
        AB_LIST.clear(); AB_SUM = 0;
        GALAXIAS_LIST.clear(); GALAXIAS_SUM = 0;
        MASOUTHS_LIST.clear(); MASOUTHS_SUM = 0;


        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');

        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        Set<String> data = shopping_cart.getAll().keySet();

        for (String key : data) {
            String value = shopping_cart.getString(key, "");

            String[] lines = value.split("\n");
            String product_best_price = lines[0].split(" " , 4)[0];
            String product_best_market = lines[0].split(" " , 4)[1];
            String product_img = lines[0].split(" " , 4)[2].trim();


            for(int i=1; i<lines.length; i++){

                ProductClass model = new ProductClass();
                model.setName(key);
                model.setUrl(product_img);
                model.setMarket("NULL");
                model.setOrigianlName(key);

                String PRICE = lines[i].split(" ")[0];
                Double PRICE_TYPECAST_DOUBLE = Double.parseDouble(lines[i].split(" ")[0]) * Integer.parseInt(shopping_cart_amount.getString(key , "1").toString());


                switch (lines[i].split(" ")[1]) {
                    case "MYMARKET":
                        model.setPrice(PRICE + " €");
                        MY_MARKET_SUM += PRICE_TYPECAST_DOUBLE;
                        MY_MARKET_LIST.add(model);
                        MY_MARKET_ADAPTERY.notifyDataSetChanged();
                        break;
                    case "ΣΚΛΑΒΕΝΙΤΗΣ":
                        model.setPrice(PRICE + " €");
                        SKLAVENITIS_SUM += PRICE_TYPECAST_DOUBLE;
                        SKLAVENITIS_LIST.add(model);
                        SKLAVENITIS_ADAPTERY.notifyDataSetChanged();
                        break;
                    case "ΑΒ":
                        model.setPrice(PRICE + " €");
                        AB_SUM += PRICE_TYPECAST_DOUBLE;
                        AB_LIST.add(model);
                        AB_ADAPTERY.notifyDataSetChanged();
                        break;
                    case "ΓΑΛΑΞΙΑΣ":
                        model.setPrice(PRICE + " €");
                        GALAXIAS_SUM += PRICE_TYPECAST_DOUBLE;
                        GALAXIAS_LIST.add(model);
                        GALAXIAS_ADAPTERY.notifyDataSetChanged();
                        break;
                    case "ΜΑΣΟΥΤΗΣ":
                        model.setPrice(PRICE + " €");
                        MASOUTHS_SUM += PRICE_TYPECAST_DOUBLE;
                        MASOUTHS_LIST.add(model);
                        MASOUTHS_ADAPTERY.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

            }
        }

        TextView detail;
        detail = findViewById(R.id.MYMARKET_DETAILS);
        detail.setText("MYMARKET • " + new DecimalFormat("#0.00", symbols).format(MY_MARKET_SUM) +" €\nΠΑΡΕΧΕΙ " +  MY_MARKET_LIST.size()  + " ΣΤΑ " + data.size() + " ΠΡΟΪΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ");

        detail = findViewById(R.id.SKLAVENTIS_DETAILS);
        detail.setText("ΣΚΛΑΒΕΝΙΤΗΣ • " + new DecimalFormat("#0.00", symbols).format(SKLAVENITIS_SUM) +" €\nΠΑΡΕΧΕΙ " +  SKLAVENITIS_LIST.size()  + " ΣΤΑ " + data.size() + " ΠΡΟΪΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ");

        detail = findViewById(R.id.AB_DETAILS);
        detail.setText("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ • " + new DecimalFormat("#0.00", symbols).format(AB_SUM) +" €\nΠΑΡΕΧΕΙ " +  AB_LIST.size()  + " ΣΤΑ " + data.size() + " ΠΡΟΪΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ");

        detail = findViewById(R.id.GALAXIAS_DETAILS);
        detail.setText("ΓΑΛΑΞΙΑΣ • " + new DecimalFormat("#0.00", symbols).format(GALAXIAS_SUM) +" €\nΠΑΡΕΧΕΙ " +  GALAXIAS_LIST.size()  + " ΣΤΑ " + data.size() + " ΠΡΟΪΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ");

        detail = findViewById(R.id.MASOUTHS_DETAILS);
        detail.setText("ΜΑΣΟΥΤΗΣ • " + new DecimalFormat("#0.00", symbols).format(MASOUTHS_SUM) +" €\nΠΑΡΕΧΕΙ " +  MASOUTHS_LIST.size()  + " ΣΤΑ " + data.size() + " ΠΡΟΪΟΝΤΑ ΤΟΥ ΚΑΛΑΘΙΟΥ");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            MY_MARKET_LIST.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
            SKLAVENITIS_LIST.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
            AB_LIST.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
            GALAXIAS_LIST.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
            MASOUTHS_LIST.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
        }
        else {
            Collections.sort(MY_MARKET_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass product1, ProductClass product2) {
                    double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(SKLAVENITIS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass product1, ProductClass product2) {
                    double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(AB_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass product1, ProductClass product2) {
                    double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(GALAXIAS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass product1, ProductClass product2) {
                    double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(MASOUTHS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass product1, ProductClass product2) {
                    double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
        }


        if(MY_MARKET_LIST.size() == 0){
            findViewById(R.id.MYMARKET_DETAILS_CONTAINER).setVisibility(View.GONE);
            MY_MARKET_RECYCLER.setVisibility(View.GONE);
        }
        if(SKLAVENITIS_LIST.size() == 0){
            findViewById(R.id.SKLAVENITIS_DETAILS_CONTAINER).setVisibility(View.GONE);
            SKLAVENITIS_RECYCLER.setVisibility(View.GONE);
        }
        if(AB_LIST.size() == 0){
            findViewById(R.id.AB_DETAILS_CONTAINER).setVisibility(View.GONE);
            AB_RECYCLER.setVisibility(View.GONE);
        }
        if(GALAXIAS_LIST.size() == 0){
            findViewById(R.id.GALAXIAS_DETAILS_CONTAINER).setVisibility(View.GONE);
            GALAXIAS_RECYCLER.setVisibility(View.GONE);
        }
        if(MASOUTHS_LIST.size() == 0){
            findViewById(R.id.MASOUTHS_DETAILS_CONTAINER).setVisibility(View.GONE);
            MASOUTHS_RECYCLER.setVisibility(View.GONE);
        }


        if (MY_MARKET_LIST.isEmpty() && SKLAVENITIS_LIST.isEmpty() && AB_LIST.isEmpty() && GALAXIAS_LIST.isEmpty() && MASOUTHS_LIST.isEmpty()) {
            findViewById(R.id.empty_shopping_cart).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty_shopping_cart).setVisibility(View.GONE);
        }


    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
