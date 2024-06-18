package com.app.marketpal.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

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

public class Offers_activity extends AppCompatActivity {

    private List<ProductClass> list_mymarket;
    private List<ProductClass> list_sklavenitis;
    private List<ProductClass> list_ab;
    private List<ProductClass> list_masouths;
    private List<ProductClass> list_galaxias;
    private Adaptery adp;
    private Adaptery adp2;
    private Adaptery adp3;
    private Adaptery adp4;
    private Adaptery adp5;
    private SharedPreferences supermarkets;
    private ShimmerFrameLayout API_DATA_SHIMMER[];
    private RecyclerView API_DATA_RV[];
    private Intent CartIntent;
    private DrawerLayout drawer;
    private NavigationView d;
    private Intent SearchIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        d = findViewById(R.id.NavigationViewLeft);
        d.setItemIconTintList(null);
        drawer = findViewById(R.id.application_menu_offers);

        list_mymarket = new ArrayList<>();
        list_sklavenitis = new ArrayList<>();
        list_ab = new ArrayList<>();
        list_masouths = new ArrayList<>();
        list_galaxias = new ArrayList<>();

        API_DATA_SHIMMER = new ShimmerFrameLayout[]{findViewById(R.id.offers_1_shimmer), findViewById(R.id.offers_2_shimmer), findViewById(R.id.offers_3_shimmer), findViewById(R.id.offers_4_shimmer), findViewById(R.id.offers_5_shimmer)};
        API_DATA_RV = new RecyclerView[]{findViewById(R.id.offers_recycler_1),findViewById(R.id.offers_recycler_2),findViewById(R.id.offers_recycler_3),findViewById(R.id.offers_recycler_4),findViewById(R.id.offers_recycler_5)};

        adp = new Adaptery(getBaseContext(), list_mymarket, ActivityType.MAIN_ACTIVITY);
        adp2 = new Adaptery(getBaseContext(), list_sklavenitis, ActivityType.MAIN_ACTIVITY);
        adp3 = new Adaptery(getBaseContext(), list_ab, ActivityType.MAIN_ACTIVITY);
        adp4 = new Adaptery(getBaseContext(), list_masouths, ActivityType.MAIN_ACTIVITY);
        adp5 = new Adaptery(getBaseContext(), list_galaxias, ActivityType.MAIN_ACTIVITY);

        adp.setOnClickListener((position, model) -> startActivity(position,  model, list_mymarket));
        adp2.setOnClickListener((position, model) -> startActivity(position, model, list_sklavenitis));
        adp3.setOnClickListener((position, model) -> startActivity(position, model, list_ab));
        adp4.setOnClickListener((position, model) -> startActivity(position, model, list_masouths));
        adp5.setOnClickListener((position, model) -> startActivity(position, model, list_galaxias));


        API_DATA_RV[0].setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        API_DATA_RV[0].setAdapter(adp);

        API_DATA_RV[1].setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        API_DATA_RV[1].setAdapter(adp2);

        API_DATA_RV[2].setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        API_DATA_RV[2].setAdapter(adp3);

        API_DATA_RV[3].setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        API_DATA_RV[3].setAdapter(adp4);

        API_DATA_RV[4].setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        API_DATA_RV[4].setAdapter(adp5);

        new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&type=TYPE_RECOMMENDED&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        InitAdsMain();
        NavigateCart();

        String ss[] = {
                "MYMARKET",
                "ΣΚΛΑΒΕΝΙΤΗΣ",
                "ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ",
                "ΜΑΣΟΥΤΗΣ",
                "ΓΑΛΑΞΙΑΣ"
        };
        LinearLayout s[] = {
                findViewById(R.id.MYMARKET_CAT_OFFERS),
                findViewById(R.id.SKLAVENITHS_CAT_OFFERS),
                findViewById(R.id.AB_CAT_OFFERS),
                findViewById(R.id.MASOUTHS_CAT_OFFERS),
                findViewById(R.id.GALAXIAS_CAT_OFFERS)

        };
        for(int i=0; i<ss.length; i++)
            if(!supermarkets.contains(ss[i])) s[i].setVisibility(View.GONE);

        LinearLayout cat = findViewById(R.id.category_container);
        for(int i=0; i<cat.getChildCount();i++){
            if(cat.getChildAt(i) instanceof TextView){
                TextView t = (TextView) cat.getChildAt(i);
                String tag = t.getTag().toString();
                t.setOnClickListener(v -> {
                        if(tag.equals("all"))
                            new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        else
                            new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=" + tag + "&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                });
            }
        }

        findViewById(R.id.filter_offer).setOnClickListener(v -> {drawer.openDrawer((int) Gravity.LEFT);});
        findViewById(R.id.close_act).setOnClickListener(v -> { finish(); });
        findViewById(R.id.sr_v1).setOnClickListener(v -> {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);
        });
        findViewById(R.id.sr_v2).setOnClickListener(v ->{
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);
        });

        LayoutInflater ii = LayoutInflater.from(this);
        View cc = ii.inflate(R.layout.navigation_custom_ln, null);
        TextView v;
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Τρόφιμα"); d.getMenu().findItem(R.id.foods).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Χυμοί,Σνακς & Κάβα"); d.getMenu().findItem(R.id.juices).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Γαλακτοκομικά & Τυριά"); d.getMenu().findItem(R.id.dairy).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Προσωπική Φροντίδα"); d.getMenu().findItem(R.id.personal_care).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Οικιακή Φροντίδα"); d.getMenu().findItem(R.id.house_care).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Παιδικά & Βρεφικά"); d.getMenu().findItem(R.id.kids).setActionView(cc);
        cc = ii.inflate(R.layout.navigation_custom_ln, null); v = cc.findViewById(R.id.cat_txt); v.setText("Κατικοίδια"); d.getMenu().findItem(R.id.pets).setActionView(cc);
        d.getMenu().findItem(R.id.nav_to_rec).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&type=TYPE_RECOMMENDED&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_grocery).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=41&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_galata_rafiou).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=365&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_freska_galata).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=187&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_turia).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=383&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_giaourtia).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=188&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_kremes_galaktos).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=189&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_pagwta).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=437&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_mpures).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=32&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_xumous).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=31&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_snacks).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=43&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_cafe).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=44&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_veges_fruits).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=607&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_meat).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=42&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;

        });
        d.getMenu().findItem(R.id.nav_to_fishes).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=781&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_bread).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=37&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_pasta).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=38&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_dough).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=53&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_women).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=174&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_men).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=175&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_self_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=407&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_hair_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=176&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_mouth_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=177&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_dressing_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=178&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_general_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=305&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_kitchen_bathe).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=171&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_house_clean).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=172&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_clothes).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=173&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_equipment).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=300&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_baby_care).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=179&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_baby_food).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=180&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_diapers).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=181&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_baby_clean).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=182&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_dog).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=184&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_cat).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=185&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
        d.getMenu().findItem(R.id.nav_to_accessories).setOnMenuItemClickListener(t -> {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=186&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView productAmount = findViewById(R.id.product_amount);


        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) {productAmount.setVisibility(View.GONE);}
        else {productAmount.setVisibility(View.VISIBLE);productAmount.setText(String.valueOf(size));}
    }

    private void InitAdsMain(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView mAdView = findViewById(R.id.adViewOffers);
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
        });


    }



    private void NavigateCart(){
        findViewById(R.id.cart_container).setOnClickListener(v -> {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.putExtra("activity" , "offers");
                startActivity(CartIntent);
                overridePendingTransition(0, 0);
        });
    }

    private void S(LinearLayout p , boolean c){

        if (c) {
                for(int i=0; i<p.getChildCount(); i++){
                    View child = p.getChildAt(i);
                    if(child instanceof LinearLayout){
                        LinearLayout ln_child = (LinearLayout) child;
                        ln_child.setVisibility(View.VISIBLE);
                    }

                    if(child instanceof RecyclerView){
                        RecyclerView rv_child = (RecyclerView) child;
                        rv_child.setVisibility(View.GONE);
                    }
                }

        }else{
            for(int i=0; i<p.getChildCount(); i++){
                View child = p.getChildAt(i);
                if(child instanceof LinearLayout){
                    LinearLayout ln_child = (LinearLayout) child;
                    ln_child.setVisibility(View.GONE);
                }

                if(child instanceof RecyclerView){
                    RecyclerView rv_child = (RecyclerView) child;
                    rv_child.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    private class AsyncProducts extends AsyncTask<String,String,Void>{

        private String API;

        LinearLayout s1 = findViewById(R.id.MYMARKET_CAT_OFFERS);
        LinearLayout s2 = findViewById(R.id.SKLAVENITHS_CAT_OFFERS);
        LinearLayout s3 = findViewById(R.id.AB_CAT_OFFERS);
        LinearLayout s4 = findViewById(R.id.MASOUTHS_CAT_OFFERS);
        LinearLayout s5 = findViewById(R.id.GALAXIAS_CAT_OFFERS);

        AsyncProducts(String API){
            this.API = API;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            API_DATA_SHIMMER[0].startShimmer();
            API_DATA_SHIMMER[1].startShimmer();
            API_DATA_SHIMMER[2].startShimmer();
            API_DATA_SHIMMER[3].startShimmer();
            API_DATA_SHIMMER[4].startShimmer();
            API_DATA_SHIMMER[0].setVisibility(View.VISIBLE);
            API_DATA_SHIMMER[1].setVisibility(View.VISIBLE);
            API_DATA_SHIMMER[2].setVisibility(View.VISIBLE);
            API_DATA_SHIMMER[3].setVisibility(View.VISIBLE);
            API_DATA_SHIMMER[4].setVisibility(View.VISIBLE);

            list_mymarket.clear();
            list_sklavenitis.clear();
            list_ab.clear();
            list_masouths.clear();
            list_galaxias.clear();
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
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


                try {
                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                    JSONArray data = new JSONObject(json).getJSONArray("data");
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

                        if(model.getMarket().equals("MYMARKET")) list_mymarket.add(model);
                        if(model.getMarket().equals("ΣΚΛΑΒΕΝΙΤΗΣ")) list_sklavenitis.add(model);
                        if(model.getMarket().equals("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ")) list_ab.add(model);
                        if(model.getMarket().equals("ΜΑΣΟΥΤΗΣ")) list_masouths.add(model);
                        if(model.getMarket().equals("ΓΑΛΑΞΙΑΣ")) list_galaxias.add(model);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        list_mymarket.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                        list_sklavenitis.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                        list_ab.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                        list_masouths.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                        list_galaxias.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                    }else {
                        Collections.sort(list_mymarket, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass product1, ProductClass product2) {
                                double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                        Collections.sort(list_sklavenitis, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass product1, ProductClass product2) {
                                double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                        Collections.sort(list_ab, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass product1, ProductClass product2) {
                                double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                        Collections.sort(list_masouths, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass product1, ProductClass product2) {
                                double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                        Collections.sort(list_galaxias, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass product1, ProductClass product2) {
                                double price1 = Double.parseDouble(product1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(product2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                    }
                }catch (Exception e){e.printStackTrace();}

            } catch (Exception e) {e.printStackTrace();}
            return null;
        }


        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            API_DATA_SHIMMER[0].stopShimmer();
            API_DATA_SHIMMER[1].stopShimmer();
            API_DATA_SHIMMER[2].stopShimmer();
            API_DATA_SHIMMER[3].stopShimmer();
            API_DATA_SHIMMER[4].stopShimmer();
            API_DATA_SHIMMER[0].setVisibility(View.GONE);
            API_DATA_SHIMMER[1].setVisibility(View.GONE);
            API_DATA_SHIMMER[2].setVisibility(View.GONE);
            API_DATA_SHIMMER[3].setVisibility(View.GONE);
            API_DATA_SHIMMER[4].setVisibility(View.GONE);

            if(list_mymarket.isEmpty())    S(s1 , true); else S(s1 , false);
            if(list_sklavenitis.isEmpty()) S(s2 , true); else S(s2 , false);
            if(list_ab.isEmpty())          S(s3 , true); else S(s3 , false);
            if(list_masouths.isEmpty())    S(s4 , true); else S(s4 , false);
            if(list_galaxias.isEmpty())    S(s5 , true); else S(s5 , false);

            adp.notifyDataSetChanged();
            adp2.notifyDataSetChanged();
            adp3.notifyDataSetChanged();
            adp4.notifyDataSetChanged();
            adp5.notifyDataSetChanged();
        }
    }

    private void startActivity(int position, ProductClass model , List<ProductClass> list){
        Intent intent = new Intent(Offers_activity.this, ProductView.class);
        intent.putExtra("PRODUCT_OBJ", model);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}
}
