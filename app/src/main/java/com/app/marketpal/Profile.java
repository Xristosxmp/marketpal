package com.app.marketpal;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Profile extends AppCompatActivity {


    private SharedPreferences favorites;
    private int favorites_size;
    private int flag;
    private int rFLAG;

    private Intent HomeIntent;
    private Intent OffersIntent;
    private Intent CartIntent;
    private Intent SearchIntent;
    private Intent MessagesIntent;


    CompoundButton c1;
    CompoundButton c2;
    CompoundButton c3;
    CompoundButton c4;
    CompoundButton c5;

    private SharedPreferences supermarkets;
    private int supermarkets_size;

    private DrawerLayout drawer;
    private NavigationView d;

    private List<ProductClass> FavoritesList;
    private List<ProductClass> RecentlyViewedList;

    private List<ProductClass> recommendedList;
    private List<ProductClass> CashbackList;
    private List<ProductClass> OfferPlusList;
    private SharedPreferences shopping_cart;

    private RecentlyViewerDatabase recently_view_db;

    private final String APP_URL = "https://play.google.com/store/apps/details?id=com.app.marketpal";

    SetRecommented TASK1;
    SetRecommented TASK2;
    SetRecommented TASK3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        favorites = getBaseContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        supermarkets_size = supermarkets.getAll().size();
        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);

        Supermarket_settings();
        NavigateHome();
        NavigateOffers();
        NavigateCart();
        NavigateSearch();
        Services();
        SetBrochures();

        FavoritesList   = new ArrayList<>();
        recommendedList = new ArrayList<>();
        CashbackList    = new ArrayList<>();
        OfferPlusList   = new ArrayList<>();
        RecentlyViewedList  = new ArrayList<>();
        IDS = new ArrayList<>();

        favorites_size = favorites.getAll().size();
        flag  = 1;
        rFLAG = 1;

        recently_view_db = new RecentlyViewerDatabase(this);

        TASK1 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?type=TYPE_RECOMMENDED&filters[]=FILTER_OFFERS_ONLY&page=1&per_page=30&in_stock=true"  , findViewById(R.id.profile_recommended_recycler) , recommendedList);
        TASK2 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_50_PER&page=1&per_page=30&in_stock=true"  , findViewById(R.id.offer_1_1_50) , OfferPlusList);
        TASK3 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_COUPONS&page=1&per_page=30&in_stock=true" , findViewById(R.id.cashback) , CashbackList);
        TASK1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        TASK2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        TASK3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void SetBrochures(){
        String[] URLS = new String[]{
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/galaxias_catalog.pdf",
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/kritikos_catalog.pdf",
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/lidl_catalog.pdf",
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/marketin_catalog.pdf",
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/masoutis_catalog.pdf",
                "https://warply.s3.eu-west-1.amazonaws.com/applications/ed840ad545884deeb6c6b699176797ed/basket-retailers/synka_catalog.pdf"
        };
        LinearLayout[] p = new LinearLayout[]{
                findViewById(R.id.brochure_galaxias),
                findViewById(R.id.brochure_krhthkos),
                findViewById(R.id.brochure_lidl),
                findViewById(R.id.brochure_marketin),
                findViewById(R.id.brochure_masouths),
                findViewById(R.id.brochure_synka)

        };

        int i = 0;
        for(LinearLayout l : p){
            int finalI = i;
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(Profile.this);
                    dialog.setContentView(R.layout.webview_dialog);
                    ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);

                    InsetDrawable inset = new InsetDrawable(back, 0);
                    dialog.getWindow().setBackgroundDrawable(inset);
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    String URL = URLS[finalI];
                    PDFView v = dialog.findViewById(R.id.pdfView);
                    dialog.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    ProgressBar b = dialog.findViewById(R.id.loading);
                    v.setPageFling(true);
                    v.setPageSnap(true);
                    new RetrivePdfStream(v,b).execute(URL);
                    dialog.show();
                }
            }); i++;
        }


    }

    private boolean h(){
        if (!c1.isChecked() && !c2.isChecked() && !c3.isChecked()
                && !c4.isChecked() && !c5.isChecked()) {
            Toast.makeText(getApplicationContext(), "Πρέπει να έχεις τουλάχιστον ένα supermarket.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void s_update(){

        if (TASK1 != null) TASK1.cancel(true);
        if (TASK2 != null) TASK2.cancel(true);
        if (TASK3 != null) TASK3.cancel(true);

        recommendedList.clear();
        CashbackList.clear();
        OfferPlusList.clear();

        TASK1 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?type=TYPE_RECOMMENDED&filters[]=FILTER_OFFERS_ONLY&page=1&per_page=30&in_stock=true", findViewById(R.id.profile_recommended_recycler), recommendedList);
        TASK2 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_50_PER&page=1&per_page=30&in_stock=true", findViewById(R.id.offer_1_1_50), OfferPlusList);
        TASK3 = new SetRecommented("https://v8api.pockee.com/api/v8/public/products?filters[]=FILTER_COUPONS&page=1&per_page=30&in_stock=true", findViewById(R.id.cashback), CashbackList);

        TASK1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        TASK2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        TASK3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        SetRecentlyViewed(true);
        SetFavoritesProducts(true);
        new update_shopping_cart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.loading_dialog_layout);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 30);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(155));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //new UpdateShoppingCartSharedPreferences(getBaseContext());
                        dialog.dismiss();
                    }
                });
            }
        }).start();

    }
    private void Supermarket_settings(){
        d = findViewById(R.id.NavigationViewLeft);
        d.setItemIconTintList(null);
        drawer = findViewById(R.id.application_menu);

        MenuItem m1 = d.getMenu().findItem(R.id.SETTING_MYMARKET);
        MenuItem m2 = d.getMenu().findItem(R.id.SETTING_Sklavenitis);
        MenuItem m3 = d.getMenu().findItem(R.id.SETTING_ABVasilopoulos);
        MenuItem m4 = d.getMenu().findItem(R.id.SETTING_Masoutis);
        MenuItem m5 = d.getMenu().findItem(R.id.SETTING_Galaxias);


        c1 = (CompoundButton) m1.getActionView();
        c2 = (CompoundButton) m2.getActionView();
        c3 = (CompoundButton) m3.getActionView();
        c4 = (CompoundButton) m4.getActionView();
        c5 = (CompoundButton) m5.getActionView();

        if(supermarkets.contains("MYMARKET")) c1.setChecked(true);
        if(supermarkets.contains("ΣΚΛΑΒΕΝΙΤΗΣ")) c2.setChecked(true);
        if(supermarkets.contains("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ")) c3.setChecked(true);
        if(supermarkets.contains("ΜΑΣΟΥΤΗΣ")) c4.setChecked(true);
        if(supermarkets.contains("ΓΑΛΑΞΙΑΣ")) c5.setChecked(true);


        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton c, boolean b) {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("MYMARKET" , true).apply();
                else  supermarkets.edit().remove("MYMARKET").apply();
                s_update();

            }
        });
        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton c, boolean b) {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΣΚΛΑΒΕΝΙΤΗΣ" , true).apply();
                else  supermarkets.edit().remove("ΣΚΛΑΒΕΝΙΤΗΣ").apply();
                s_update();
            }
        });
        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton c, boolean b) {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ" , true).apply();
                else  supermarkets.edit().remove("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ").apply();
                s_update();

            }
        });
        c4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton c, boolean b) {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΜΑΣΟΥΤΗΣ" , true).apply();
                else  supermarkets.edit().remove("ΜΑΣΟΥΤΗΣ").apply();
                s_update();

            }
        });
        c5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton c, boolean b) {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΓΑΛΑΞΙΑΣ" , true).apply();
                else  supermarkets.edit().remove("ΓΑΛΑΞΙΑΣ").apply();
                s_update();

            }
        });


        findViewById(R.id.supermarkets_settings_viewer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer((int) Gravity.LEFT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView headercart = findViewById(R.id.product_amount_profile);
        TextView productAmountNav = findViewById(R.id.product_amount_nav);
        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) {
            productAmountNav.setVisibility(View.GONE);
            headercart.setVisibility(View.GONE);
        }
        else {
            productAmountNav.setVisibility(View.VISIBLE);
            headercart.setVisibility(View.VISIBLE);
            headercart.setText(String.valueOf(size));
            productAmountNav.setText(String.valueOf(size));
        }

        SetRecentlyViewed(false);
        SetFavoritesProducts(false);
    }

    private void NavigateHome(){
        findViewById(R.id.homenav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(supermarkets_size != supermarkets.getAll().size()){
                    HomeIntent = new Intent(getBaseContext() , MainActivity.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(HomeIntent);
                    overridePendingTransition(0, 0);
                }else{
                    HomeIntent = new Intent(getBaseContext() , MainActivity.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(HomeIntent);
                    overridePendingTransition(0, 0);
                }




            }
        });
    }
    private void NavigateOffers(){
        findViewById(R.id.today_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersIntent = new Intent(getBaseContext() , Offers_activity.class);
                OffersIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                OffersIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(OffersIntent);
                overridePendingTransition(0, 0);
            }
        });
    }
    private void NavigateCart(){
        findViewById(R.id.cart_container_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                CartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(CartIntent);
                overridePendingTransition(0, 0);
            }
        });
        findViewById(R.id.cart_container_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                CartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(CartIntent);
                overridePendingTransition(0, 0);
            }
        });
    }
    private void NavigateSearch(){
        findViewById(R.id.focussearchbar).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                SearchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                SearchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);
            }
        });
    }


    private void Services(){
        ScrollView s = findViewById(R.id.activity_profile_scroller);
        findViewById(R.id.view_recently_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.smoothScrollTo(0 , (int )findViewById(R.id.RECENTLY_VIEWED_LAYOUT).getTop());
            }
        });


        findViewById(R.id.view_recommended_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.smoothScrollTo(0 , (int )findViewById(R.id.RECOMMENDED_LAYOUT).getTop());
            }
        });

        findViewById(R.id.view_cashack_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.smoothScrollTo(0 , (int )findViewById(R.id.CASHBACK_LAYOUT).getTop());
            }
        });

        findViewById(R.id.view_one_plus_one_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.smoothScrollTo(0 , (int )findViewById(R.id.PLUSGIFT_LAYOUT).getTop());
            }
        });

    }

    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}
    private LinearLayout LNerror(){
        LinearLayout LN = new LinearLayout(this);
        LN.setId(View.generateViewId()); // Set a unique ID for the LinearLayout
        LN.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
        ));
        LN.setOrientation(LinearLayout.VERTICAL);
        LN.setGravity(Gravity.CENTER);
        //LN.setVisibility(View.VISIBLE);

        // Create an ImageView
        ImageView img = new ImageView(this);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                48,
                48
        ));
        img.setImageResource(R.drawable.empty_favorites_icon);

        // Create a TextView
        TextView t = new TextView(this);
        t.setTextColor(Color.BLACK);
        t.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        t.setGravity(Gravity.CENTER);
        t.setText("Δεν Βρέθηκαν Προιόντα.");
        t.setTypeface(null, Typeface.BOLD);

        // Add ImageView and TextView to the LinearLayout
        LN.addView(img);
        LN.addView(t);
        return LN;
    }




    private void SetLoadingVisibility(RecyclerView rv , boolean c){
        if (c) {
            ViewParent parent = rv.getParent();
            if(parent instanceof LinearLayout){
                LinearLayout p = (LinearLayout) parent;
                for(int i=0; i<p.getChildCount(); i++){
                    View child = p.getChildAt(i);
                    if(child instanceof LinearLayout){
                        LinearLayout ln_child = (LinearLayout) child;
                        if(child.getTag() != null)
                            if(child.getTag().toString().equals("loading"))
                                child.setVisibility(View.VISIBLE);
                    }
                }
            }
        }else{
            ViewParent parent = rv.getParent();
            if(parent instanceof LinearLayout){
                LinearLayout p = (LinearLayout) parent;
                for(int i=0; i<p.getChildCount(); i++){
                    View child = p.getChildAt(i);
                    if(child instanceof LinearLayout){
                        if(child.getTag() != null)
                            if(child.getTag().toString().equals("loading"))
                                child.setVisibility(View.GONE);
                    }
                }
            }
        }

    }
    private void NoProductsSetter(RecyclerView rv , boolean c){

        if (c) {
            ViewParent parent = rv.getParent();
            if(parent instanceof LinearLayout){
                LinearLayout p = (LinearLayout) parent;
                for(int i=0; i<p.getChildCount(); i++){
                    View child = p.getChildAt(i);
                    if(child instanceof LinearLayout){
                        LinearLayout ln_child = (LinearLayout) child;
                        ln_child.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }
                }
            }
        }else{
            ViewParent parent = rv.getParent();
            if(parent instanceof LinearLayout){
                LinearLayout p = (LinearLayout) parent;
                for(int i=0; i<p.getChildCount(); i++){
                    View child = p.getChildAt(i);
                    if(child instanceof LinearLayout){
                        LinearLayout ln_child = (LinearLayout) child;
                        ln_child.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

    }

    private List<Integer> IDS;
    private String base_product_url = "https://v8api.pockee.com/api/v8/public/products/";
    private void SetRecentlyViewed(boolean c) {
        SQLiteDatabase db = recently_view_db.getReadableDatabase();
        String[] projection = {RecentlyViewerDatabase.COLUMN_PRODUCT_ID, RecentlyViewerDatabase.COLUMN_DATE};
        Cursor cursor = db.query(
                RecentlyViewerDatabase.TABLE_RECENTLY_VIEWED,
                projection,
                null,
                null,
                null,
                null,
                RecentlyViewerDatabase.COLUMN_DATE + " DESC",
                "5"
        );
        List<Integer> IDS_q = new ArrayList<>();
        while (cursor.moveToNext())
            IDS_q.add(cursor.getInt(cursor.getColumnIndex(RecentlyViewerDatabase.COLUMN_PRODUCT_ID)));

        cursor.close();
        db.close();


        if (IDS != null && IDS_q != null) {
            HashMap<Integer, Integer> m = new HashMap<>();
            for (int i = 0; i < IDS_q.size(); i++)
                if (IDS_q.get(i) != null)
                    m.put(IDS_q.get(i), i);
            IDS.sort((a, b) -> {
                Integer A = m.get(a);
                Integer B = m.get(b);
                if (A != null && B != null) return Integer.compare(A, B);
                else if (A != null) return 1;
                else if (B != null) return -1;
                else return 0;
            });
        }


        if(rFLAG == 1 || !IDS.equals(IDS_q) || c){
            Integer[] IDS_   = IDS_q.toArray(new Integer[0]);

            String API[] = new String[IDS_.length];
            for(int i=0; i<API.length; i++)
                API[i] = base_product_url + IDS_[i];

            new SetRecentlyViewedAsync(API, findViewById(R.id.profile_recviewed_recycler)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        IDS = IDS_q;
        rFLAG = 0;
    }
    private void SetFavoritesProducts(boolean c){
        Map<String, ?> e = favorites.getAll();
        String API[] = new String[favorites.getAll().size()];
        int index = 0;
        for (Map.Entry<String, ?> entry : e.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            API[index] = "https://v8api.pockee.com/api/v8/public/products/"+value.toString();
            index++;
        }
        if(favorites.getAll().size() != favorites_size || flag == 1 || c)
        new SetFavorites(API, findViewById(R.id.profile_favorites_recycler)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        favorites_size = favorites.getAll().size();
        flag = 0;
    }

    private class SetFavorites extends AsyncTask<String,String, JSONObject[]>{

        private RecyclerView rv_01;

        private String API[];

        SetFavorites(String API[]  , RecyclerView rv_01){
            this.API = API;
            this.rv_01 = rv_01;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(rv_01 != null)
                rv_01.removeAllViews();
            SetLoadingVisibility(rv_01 , true);
            NoProductsSetter(rv_01 , false);
            FavoritesList.clear();
            FavoritesList = new ArrayList<>();
        }

        @Override
        protected JSONObject[] doInBackground(String... strings) {
            JSONObject JSON_OBJECT[] = new JSONObject[API.length];
            for(int i=0; i<API.length; i++) {
                try {
                    JSON_OBJECT[i] = new JSONObject();
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();

                    Request request = new Request.Builder()
                            .url(API[i])
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

                    Response response = client.newCall(request).execute();

                    ResponseBody responseBody = response.body();
                    String json = responseBody != null ? responseBody.string() : "";
                    JSON_OBJECT[i] = new JSONObject(json);
                } catch (Exception e) {e.printStackTrace(); JSON_OBJECT[i] = null; continue;}
            }

            return JSON_OBJECT;
        }

        @Override
        protected void onPostExecute(JSONObject[] JSON_OBJECT) {
            super.onPostExecute(JSON_OBJECT);

            if(API.length == 0){
                NoProductsSetter(rv_01 , true);
                SetLoadingVisibility(rv_01 , false);
                return;
            }
            if(JSON_OBJECT == null){
                NoProductsSetter(rv_01 , true);
                SetLoadingVisibility(rv_01 , false);
                return;
            }
            try {
                for(int i=0; i<JSON_OBJECT.length; i++){
                    JSONArray ASSORTMENTS = JSON_OBJECT[i].getJSONObject("data").getJSONArray("assortments");
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    String coupon_value = "null";
                    String coupon_value_discount = "null";
                    String product_brand = JSON_OBJECT[i].getJSONObject("data").getString("brand_id");
                    if(JSON_OBJECT[i] == null) continue;
                    if(JSON_OBJECT[i].isNull("data")) continue;
                    if(JSON_OBJECT[i].getJSONObject("data").isNull("assortments")) continue;
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    String product_img = null;
                    if(!JSON_OBJECT[i].getJSONObject("data").isNull("image_versions"))
                        product_img = JSON_OBJECT[i].getJSONObject("data").getJSONObject("image_versions").getString("original");
                    else product_img = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";



                    if(JSON_OBJECT[i].has("coupons"))
                    if(JSON_OBJECT[i].getJSONArray("coupons").length() > 0){
                        JSONArray J = JSON_OBJECT[i].getJSONArray("coupons");
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
                    model.setName(JSON_OBJECT[i].getJSONObject("data").getString("name"));
                    model.setUrl(product_img);
                    model.setMarket(FINAL_NAME.trim());  model.setPrice(String.valueOf(FINAL_PRICE) + " €");
                    model.setID(JSON_OBJECT[i].getJSONObject("data").getString("id"));
                    model.setASSORTEMTNS_DATA(ASSORTEMTNS_DATA);
                    model.setDesc(JSON_OBJECT[i].getJSONObject("data").getString("description"));
                    model.setOrigianlName(JSON_OBJECT[i].getJSONObject("data").getString("name"));
                    model.setBrand_id(product_brand);
                    model.setValue_discount(coupon_value_discount);
                    model.setCoupon_value(coupon_value);
                    FavoritesList.add(model);
                }

                Collections.sort(FavoritesList, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });

                Adaptery adp = new Adaptery(getBaseContext(), FavoritesList);
                rv_01.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                rv_01.setAdapter(adp);
                adp.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        Intent intent = new Intent(getBaseContext(), ProductView.class);
                        intent.putExtra("original_name" , FavoritesList.get(position).getOrigianlName());
                        intent.putExtra("id" , FavoritesList.get(position).getID());
                        intent.putExtra("name" , FavoritesList.get(position).getName());
                        intent.putExtra("img" , FavoritesList.get(position).getUrl());
                        //intent.putExtra("desc" , dairy_01_list.get(position).get());
                        intent.putExtra("assortments" , FavoritesList.get(position).getASSORTEMTNS_DATA());
                        intent.putExtra("desc" , FavoritesList.get(position).getDesc());
                        intent.putExtra("brand_id" , FavoritesList.get(position).getBrand_id());
                        intent.putExtra("coupon_value" , FavoritesList.get(position).getCoupon_value());
                        intent.putExtra("coupon_value_discount" , FavoritesList.get(position).getValue_discount());
                        startActivity(intent);
                    }
                });

            }catch (Exception e){e.printStackTrace();}
            SetLoadingVisibility(rv_01 , false);
            NoProductsSetter(rv_01 , false);

        }
    }
    private class SetRecentlyViewedAsync extends AsyncTask<String,String, JSONObject[]>{

        private RecyclerView rv_01;
        private String API[];
        private String DATES[];


        SetRecentlyViewedAsync(String API[]  , RecyclerView rv_01){
            this.API = API;
            this.DATES = DATES;
            this.rv_01 = rv_01;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(rv_01 != null)
                rv_01.removeAllViews();
            SetLoadingVisibility(rv_01 , true);

            RecentlyViewedList.clear();
            RecentlyViewedList = new ArrayList<>();
        }

        @Override
        protected JSONObject[] doInBackground(String... strings) {
            JSONObject JSON_OBJECT[] = new JSONObject[API.length];
            for(int i=0; i<API.length; i++) {
                try {
                    JSON_OBJECT[i] = new JSONObject();
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();

                    Request request = new Request.Builder()
                            .url(API[i])
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

                    Response response = client.newCall(request).execute();

                    ResponseBody responseBody = response.body();
                    String json = responseBody != null ? responseBody.string() : "";
                    JSON_OBJECT[i] = new JSONObject(json);
                } catch (Exception e) {e.printStackTrace(); JSON_OBJECT[i] = null; continue;}
            }

            return JSON_OBJECT;
        }

        @Override
        protected void onPostExecute(JSONObject[] JSON_OBJECT) {
            super.onPostExecute(JSON_OBJECT);

            if(API.length == 0){
                NoProductsSetter(rv_01 , true);
                SetLoadingVisibility(rv_01 , false);

                return;
            }

            try {
                for(int i=0; i<JSON_OBJECT.length; i++){
                    JSONArray ASSORTMENTS = JSON_OBJECT[i].getJSONObject("data").getJSONArray("assortments");
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    String coupon_value = "null";
                    String coupon_value_discount = "null";
                    String product_brand = JSON_OBJECT[i].getJSONObject("data").getString("brand_id");
                    if(JSON_OBJECT[i] == null) continue;
                    if(JSON_OBJECT[i].isNull("data")) continue;
                    if(JSON_OBJECT[i].getJSONObject("data").isNull("assortments")) continue;
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    String product_img = null;
                    if(!JSON_OBJECT[i].getJSONObject("data").isNull("image_versions"))
                        product_img = JSON_OBJECT[i].getJSONObject("data").getJSONObject("image_versions").getString("original");
                    else product_img = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";



                    if(JSON_OBJECT[i].has("coupons"))
                        if(JSON_OBJECT[i].getJSONArray("coupons").length() > 0){
                            JSONArray J = JSON_OBJECT[i].getJSONArray("coupons");
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
                    model.setName(JSON_OBJECT[i].getJSONObject("data").getString("name"));
                    model.setUrl(product_img);
                    model.setMarket(FINAL_NAME.trim());  model.setPrice(String.valueOf(FINAL_PRICE) + " €");
                    model.setID(JSON_OBJECT[i].getJSONObject("data").getString("id"));
                    model.setASSORTEMTNS_DATA(ASSORTEMTNS_DATA);
                    model.setDesc(JSON_OBJECT[i].getJSONObject("data").getString("description"));
                    model.setOrigianlName(JSON_OBJECT[i].getJSONObject("data").getString("name"));
                    model.setValue_discount(coupon_value_discount);
                    model.setCoupon_value(coupon_value);
                    model.setBrand_id(product_brand);
                    RecentlyViewedList.add(model);
                }

                /*
                Collections.sort(FavoritesList, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });

                 */

                AdapteryIII adp = new AdapteryIII(getBaseContext(), RecentlyViewedList);
                rv_01.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                rv_01.setAdapter(adp);
                adp.setOnClickListener(new Adaptery.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductClass model) {
                        Intent intent = new Intent(getBaseContext(), ProductView.class);
                        intent.putExtra("original_name" , RecentlyViewedList.get(position).getOrigianlName());
                        intent.putExtra("id" , RecentlyViewedList.get(position).getID());
                        intent.putExtra("name" , RecentlyViewedList.get(position).getName());
                        intent.putExtra("img" , RecentlyViewedList.get(position).getUrl());
                        //intent.putExtra("desc" , dairy_01_list.get(position).get());
                        intent.putExtra("assortments" , RecentlyViewedList.get(position).getASSORTEMTNS_DATA());
                        intent.putExtra("desc" , RecentlyViewedList.get(position).getDesc());
                        intent.putExtra("brand_id" , RecentlyViewedList.get(position).getBrand_id());
                        intent.putExtra("coupon_value" , RecentlyViewedList.get(position).getCoupon_value());
                        intent.putExtra("coupon_value_discount" , RecentlyViewedList.get(position).getValue_discount());
                        startActivity(intent);
                    }
                });

            }catch (Exception e){e.printStackTrace();}

            SetLoadingVisibility(rv_01 , false);
            NoProductsSetter(rv_01 , false);
        }
    }
    private class SetRecommented extends AsyncTask<String,String,JSONObject>{

        private List<ProductClass> product_list;
        private RecyclerView RecommentedRecycler;
        private String API;


        SetRecommented(String API , RecyclerView RecommentedRecycler , List<ProductClass> product_list){
            this.API = API;
            this.RecommentedRecycler = RecommentedRecycler;
            this.product_list = product_list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            product_list.clear();
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
                NoProductsSetter(RecommentedRecycler , true);
                SetLoadingVisibility(RecommentedRecycler , false);
                return;
            }
            if(JSON_OBJECT.length() <= 0 || !JSON_OBJECT.has("data")){
                    NoProductsSetter(RecommentedRecycler , true);
                    SetLoadingVisibility(RecommentedRecycler , false);
                    return;
            }



            NoProductsSetter(RecommentedRecycler , false);
            try {
                JSONArray data = JSON_OBJECT.getJSONArray("data");
                if(data.length() <= 0){
                    NoProductsSetter(RecommentedRecycler , true);
                    SetLoadingVisibility(RecommentedRecycler , false);
                    return;
                }

                for(int i=0; i<data.length(); i++){
                    JSONObject product = data.getJSONObject(i);
                    String product_id = product.getString("id");
                    String product_name = product.getString("name");
                    String product_desc = product.getString("description");
                    String product_brand = product.getString("brand_id");
                    String coupon_value = "null";
                    String coupon_value_discount = "null";
                    JSONArray ASSORTMENTS =  product.getJSONArray("assortments");
                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    String ASSORTEMTNS_DATA[][] = new String[ASSORTMENTS.length()][2];
                    String product_img = null;



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
                RecommentedRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
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

        }



    }
    private class update_shopping_cart extends AsyncTask<String,String, JSONObject[]>{

        String API[];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Set<String> k = shopping_cart.getAll().keySet();
            API = new String[shopping_cart.getAll().size()]; int i = 0;
            for (String key : k) {
                String value = shopping_cart.getString(key, "");
                String[] l = value.split("\n");
                String pid = l[0].split(" ")[3];
                API[i] = "https://v8api.pockee.com/api/v8/public/products/" + pid;
                i++;
            }
        }

        @Override
        protected JSONObject[] doInBackground(String... strings) {

            JSONObject JSON_OBJECT[] = new JSONObject[API.length];
            for(int i=0; i<API.length; i++) {
                try {
                    JSON_OBJECT[i] = new JSONObject();
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();

                    Request request = new Request.Builder()
                            .url(API[i])
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

                    Response response = client.newCall(request).execute();

                    ResponseBody responseBody = response.body();
                    String json = responseBody != null ? responseBody.string() : "";
                    JSON_OBJECT[i] = new JSONObject(json);
                } catch (Exception e) {e.printStackTrace(); JSON_OBJECT[i] = null; continue;}
            }

            return JSON_OBJECT;
        }

        @Override
        protected void onPostExecute(JSONObject[] JSON_OBJECT) {
            super.onPostExecute(JSON_OBJECT);
            for(int i=0; i<JSON_OBJECT.length; i++){
                try {
                    if(JSON_OBJECT[i] == null) continue;
                    JSONObject p = JSON_OBJECT[i].getJSONObject("data");
                    String pname = p.getString("name");
                    String pid   = p.getString("id");

                    String pimg = "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";
                    if(!p.isNull("image_versions"))
                        pimg = p.getJSONObject("image_versions").getString("original");

                    double FINAL_PRICE = 55555;
                    String FINAL_NAME = "";
                    JSONArray ASSORTMENTS = p.getJSONArray("assortments");
                    String assortments[][] = new String[ASSORTMENTS.length()][2];

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
                        assortments[j][0] = name;
                        assortments[j][1] = String.valueOf(finalPrice);

                    }



                    StringBuilder b = new StringBuilder();
                    if(FINAL_PRICE == 55555) {
                        shopping_cart.edit().remove(pname).apply();
                        continue;
                    }
                    b.append(FINAL_PRICE + " " + FINAL_NAME.replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "") + " " + pimg + " " + pid + "\n");
                    for(int j=0; j<assortments.length; j++)
                        if(assortments[j][0] == null) continue;
                        else{
                            if(j == assortments.length-1) b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim());
                            else                          b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim() + "\n");
                        }

                        shopping_cart.edit().putString(pname , b.toString().trim()).apply();
                        onResume();
                } catch (JSONException e) {throw new RuntimeException(e);}
            }
            onResume();

        }
    }

    class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {

        PDFView pdfView;
        ProgressBar loading;
        RetrivePdfStream(PDFView pdfView,ProgressBar loading){
            this.pdfView = pdfView;
            this.loading = loading;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL uri = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            pdfView.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    loading.setVisibility(View.GONE);
                }
            }).load();
        }
    }

}
