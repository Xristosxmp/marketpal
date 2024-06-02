package com.app.marketpal.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
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


    Adaptery adp;
    Adaptery adp2;
    Adaptery adp3;
    Adaptery adp4;
    Adaptery adp5;

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


        ScrollToCat();
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
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tag.equals("all"))
                            new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        else
                            new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=" + tag + "&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });
            }
        }

        findViewById(R.id.filter_offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer((int) Gravity.LEFT);
            }
        });
        findViewById(R.id.close_act).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.sr_v1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);

            }
        });
        findViewById(R.id.sr_v2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);

            }
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
        d.getMenu().findItem(R.id.nav_to_rec).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&type=TYPE_RECOMMENDED&page=1&per_page=90&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_grocery).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=41&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_galata_rafiou).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=365&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_freska_galata).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=187&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_turia).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=383&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_giaourtia).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=188&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_kremes_galaktos).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=189&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_pagwta).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=437&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_mpures).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=32&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_xumous).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=31&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_snacks).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=43&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_cafe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=44&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_veges_fruits).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=607&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_meat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=42&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_fishes).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=781&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_bread).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=37&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_pasta).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=38&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_dough).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=53&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_women).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=174&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_men).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=175&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_self_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=407&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_hair_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=176&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_mouth_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=177&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_dressing_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=178&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_general_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=305&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_kitchen_bathe).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=171&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_house_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=172&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_clothes).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=173&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_equipment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=300&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_baby_care).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=179&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_baby_food).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=180&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_diapers).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=181&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_baby_clean).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=182&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_dog).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=184&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_cat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=185&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
        });
        d.getMenu().findItem(R.id.nav_to_accessories).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                new AsyncProducts("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_OFFERS_ONLY&category_id=186&page=1&per_page=128&in_stock=true").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                drawer.closeDrawer((int) Gravity.LEFT);
                return false;
            }
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


    private void ScrollToCat(){
        ScrollView scrl = findViewById(R.id.offers_scroller);
        findViewById(R.id.mymarket_scroll_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrl.smoothScrollTo(0 , (int )findViewById(R.id.MYMARKET_CAT_OFFERS).getTop());
            }
        });
        findViewById(R.id.sklaveniths_scroll_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrl.smoothScrollTo(0 , (int) findViewById(R.id.SKLAVENITHS_CAT_OFFERS).getTop());
            }
        });
        findViewById(R.id.ab_scroll_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrl.smoothScrollTo(0 , (int) findViewById(R.id.AB_CAT_OFFERS).getTop());
            }
        });
        findViewById(R.id.masouths_scroll_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrl.smoothScrollTo(0 , (int) findViewById(R.id.MASOUTHS_CAT_OFFERS).getTop());
            }
        });
        findViewById(R.id.galaxias_scroll_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrl.smoothScrollTo(0 , (int) findViewById(R.id.GALAXIAS_CAT_OFFERS).getTop());
            }
        });
    }


    private void NavigateCart(){

        findViewById(R.id.cart_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                startActivity(CartIntent);
                overridePendingTransition(0, 0);

            }
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
    private class AsyncProducts extends AsyncTask<String,String,JSONObject>{

        private String API;

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
        protected JSONObject doInBackground(String... strings) {
            JSONObject JSON_OBJECT = new JSONObject();


            try {
                JSON_OBJECT = new JSONObject();
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
                JSON_OBJECT = new JSONObject(json);
            } catch (Exception e) {e.printStackTrace();JSON_OBJECT = null;}


            return JSON_OBJECT;
        }

        @Override
        protected void onPostExecute(JSONObject JSON_OBJECT) {
            try {




                JSONArray data = JSON_OBJECT.getJSONArray("data");
                for(int i=0; i<data.length(); i++){
                    JSONObject product = data.getJSONObject(i);
                    String product_id = product.getString("id");
                    String product_name = product.getString("name");
                    String product_desc = product.getString("description");
                    String product_brand = product.getString("brand_id");
                    String product_img = null;
                    JSONArray ASSORTMENTS =  product.getJSONArray("assortments");
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    String coupon_value = "null";
                    String coupon_value_discount = "null";

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
                    model.setMarket("NULL");
                    model.setID(product_id);
                    model.setASSORTEMTNS_DATA(ASSORTEMTNS_DATA);
                    model.setDesc(product_desc);
                    model.setPrice(String.valueOf(FINAL_PRICE) + " €");
                    model.setOrigianlName(product_name);
                    model.setBrand_id(product_brand);
                    model.setValue_discount(coupon_value_discount);
                    model.setCoupon_value(coupon_value);

                    if(FINAL_NAME.equals("MYMARKET")) list_mymarket.add(model);
                    if(FINAL_NAME.equals("ΣΚΛΑΒΕΝΙΤΗΣ")) list_sklavenitis.add(model);
                    if(FINAL_NAME.equals("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ")) list_ab.add(model);
                    if(FINAL_NAME.equals("ΜΑΣΟΥΤΗΣ")) list_masouths.add(model);
                    if(FINAL_NAME.equals("ΓΑΛΑΞΙΑΣ")) list_galaxias.add(model);
                }

                Collections.sort(list_mymarket, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });
                Collections.sort(list_sklavenitis, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });
                Collections.sort(list_ab, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });
                Collections.sort(list_masouths, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });
                Collections.sort(list_galaxias, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });

                LinearLayout s1 = findViewById(R.id.MYMARKET_CAT_OFFERS);
                LinearLayout s2 = findViewById(R.id.SKLAVENITHS_CAT_OFFERS);
                LinearLayout s3 = findViewById(R.id.AB_CAT_OFFERS);
                LinearLayout s4 = findViewById(R.id.MASOUTHS_CAT_OFFERS);
                LinearLayout s5 = findViewById(R.id.GALAXIAS_CAT_OFFERS);


                if(list_mymarket.isEmpty())    S(s1 , true); else S(s1 , false);
                if(list_sklavenitis.isEmpty()) S(s2 , true); else S(s2 , false);
                if(list_ab.isEmpty())          S(s3 , true); else S(s3 , false);
                if(list_masouths.isEmpty())    S(s4 , true); else S(s4 , false);
                if(list_galaxias.isEmpty())    S(s5 , true); else S(s5 , false);

                adp.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        startActivity(position , model , list_mymarket);
                    }
                });
                adp2.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        startActivity(position , model , list_sklavenitis);
                    }
                });
                adp3.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        startActivity(position , model , list_ab);
                    }
                });
                adp4.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        startActivity(position , model , list_masouths);
                    }
                });
                adp5.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        startActivity(position , model , list_galaxias);
                    }
                });
            }catch (Exception e){e.printStackTrace();}


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

            adp.notifyDataSetChanged();
            adp2.notifyDataSetChanged();
            adp3.notifyDataSetChanged();
            adp4.notifyDataSetChanged();
            adp5.notifyDataSetChanged();
        }
    }

    private void startActivity(int position, ProductClass model , List<ProductClass> list){
        Intent intent = new Intent(Offers_activity.this, ProductView.class);
        intent.putExtra("original_name" , list.get(position).getOrigianlName());
        intent.putExtra("id" , list.get(position).getID());
        intent.putExtra("name" , list.get(position).getName());
        intent.putExtra("img" , list.get(position).getUrl());
        intent.putExtra("assortments" , list.get(position).getASSORTEMTNS_DATA());
        intent.putExtra("desc" , list.get(position).getDesc());
        intent.putExtra("brand_id" , list.get(position).getBrand_id());
        intent.putExtra("coupon_value" , list.get(position).getCoupon_value());
        intent.putExtra("coupon_value_discount" , list.get(position).getValue_discount());

        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}
}
