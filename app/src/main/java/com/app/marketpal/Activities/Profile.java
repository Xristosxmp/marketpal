package com.app.marketpal.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Adapters.AdapteryIII;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.RecentlyViewerDatabase;
import com.app.marketpal.enumActivities.ActivityType;
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
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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
    private CompoundButton c1;
    private CompoundButton c2;
    private CompoundButton c3;
    private CompoundButton c4;
    private CompoundButton c5;
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
    private final String base_product_url = "https://v8api.pockee.com/api/v8/public/products/";
    private SetRecommented TASK1;
    private SetRecommented TASK2;
    private SetRecommented TASK3;
    private SetFavorites   FAVORITES_TASK;
    private Dialog dialog_loading;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        dialog_loading = new Dialog(this);
        dialog_loading.setContentView(R.layout.loading_dialog_layout);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 30);
        dialog_loading.getWindow().setBackgroundDrawable(inset);
        dialog_loading.setCancelable(false);
        Window window = dialog_loading.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(155));

   }
    @Override protected void onResume() {
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


        drawer.closeDrawer(Gravity.LEFT);
        SetRecentlyViewed(false);
        SetFavoritesProducts(false);
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
            l.setOnClickListener(vi -> {
                    Dialog dialog = new Dialog(Profile.this);
                    dialog.setContentView(R.layout.webview_dialog);
                    ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                    InsetDrawable inset = new InsetDrawable(back, 0);
                    dialog.getWindow().setBackgroundDrawable(inset);
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    String URL = URLS[finalI];
                    PDFView v = dialog.findViewById(R.id.pdfView);
                    dialog.findViewById(R.id.exit).setOnClickListener(vii -> { dialog.dismiss(); });
                    ProgressBar b = dialog.findViewById(R.id.loading);

//                    v.setPageFling(true);
//                    v.setPageSnap(true);
                    new RetrivePdfStream(v,b).execute(URL);
                    dialog.show();
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

        Set<String> k = shopping_cart.getAll().keySet();
        CountDownLatch latch = new CountDownLatch(shopping_cart.getAll().size());
        for (String key : k)
            new update_shopping_cart("https://v8api.pockee.com/api/v8/public/products/" + shopping_cart.getString(key, "").split("\n")[0].split(" ")[3], latch).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dialog_loading.show();
        new Thread(() -> {
            try {
                latch.await();
                runOnUiThread(() -> {
                    dialog_loading.dismiss();
                    MainActivity.main_activity_object.finish();
                    onResume();
                });
            } catch (InterruptedException e) {e.printStackTrace();}
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


        c1.setOnCheckedChangeListener((CompoundButton c, boolean b) -> {
            if (!h()) {c.setChecked(true); return; }
            if (b) supermarkets.edit().putBoolean("MYMARKET", true).apply();
            else supermarkets.edit().remove("MYMARKET").apply();
            s_update();
        });

        c2.setOnCheckedChangeListener((CompoundButton c, boolean b) -> {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΣΚΛΑΒΕΝΙΤΗΣ" , true).apply();
                else  supermarkets.edit().remove("ΣΚΛΑΒΕΝΙΤΗΣ").apply();
                s_update();
        });
        c3.setOnCheckedChangeListener((CompoundButton c, boolean b) -> {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ" , true).apply();
                else  supermarkets.edit().remove("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ").apply();
                s_update();
        });
        c4.setOnCheckedChangeListener((CompoundButton c, boolean b) -> {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΜΑΣΟΥΤΗΣ" , true).apply();
                else  supermarkets.edit().remove("ΜΑΣΟΥΤΗΣ").apply();
                s_update();
        });
        c5.setOnCheckedChangeListener((CompoundButton c, boolean b) -> {
                if(!h()) {c.setChecked(true); return;}
                if(b == true)supermarkets.edit().putBoolean("ΓΑΛΑΞΙΑΣ" , true).apply();
                else  supermarkets.edit().remove("ΓΑΛΑΞΙΑΣ").apply();
                s_update();
        });

        findViewById(R.id.supermarkets_settings_viewer).setOnClickListener(v -> {drawer.openDrawer((int) Gravity.LEFT);});
    }
    private void NavigateHome(){
        findViewById(R.id.homenav).setOnClickListener(v -> {
                    HomeIntent = new Intent(getBaseContext() , MainActivity.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(HomeIntent);
                    overridePendingTransition(0, 0);
        });
    }
    private void NavigateOffers(){
        findViewById(R.id.today_offers).setOnClickListener(today_offers_v -> {
                OffersIntent = new Intent(getBaseContext() , Offers_activity.class);
                startActivity(OffersIntent);
                overridePendingTransition(0, 0);
        });
    }
    private void NavigateCart(){
        findViewById(R.id.cart_container_nav).setOnClickListener(v ->{
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.putExtra("activity" , "profile");
                startActivity(CartIntent);
                overridePendingTransition(0, 0);
        });
        findViewById(R.id.cart_container_profile).setOnClickListener(v -> {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.putExtra("activity" , "profile");
                startActivity(CartIntent);
                overridePendingTransition(0, 0);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void NavigateSearch(){
        findViewById(R.id.focussearchbar).setOnClickListener(v ->  {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);
        });
    }
    private void Services(){
        ScrollView s = findViewById(R.id.activity_profile_scroller);
        findViewById(R.id.view_recently_product).setOnClickListener(v ->  {s.smoothScrollTo(0 , (int )findViewById(R.id.RECENTLY_VIEWED_LAYOUT).getTop());});
        findViewById(R.id.view_recommended_product).setOnClickListener(v-> {s.smoothScrollTo(0 , (int )findViewById(R.id.RECOMMENDED_LAYOUT).getTop()); });
        findViewById(R.id.view_cashack_product).setOnClickListener(v-> {s.smoothScrollTo(0 , (int )findViewById(R.id.CASHBACK_LAYOUT).getTop());});
        findViewById(R.id.view_one_plus_one_product).setOnClickListener(v-> {s.smoothScrollTo(0 , (int )findViewById(R.id.PLUSGIFT_LAYOUT).getTop());});
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
    private List<Integer> IDS;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                IDS.sort((a, b) -> {
                    Integer A = m.get(a);
                    Integer B = m.get(b);
                    if (A != null && B != null) return Integer.compare(A, B);
                    else if (A != null) return 1;
                    else if (B != null) return -1;
                    else return 0;
                });
            }else{
                Collections.sort(IDS, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer a, Integer b) {
                        Integer A = m.get(a);
                        Integer B = m.get(b);
                        if (A != null && B != null) return Integer.compare(A, B);
                        else if (A != null) return 1;
                        else if (B != null) return -1;
                        else return 0;
                    }
                });
            }
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
        if(favorites.getAll().size() != favorites_size || flag == 1 || c) {
            if (FAVORITES_TASK == null)
                FAVORITES_TASK = (SetFavorites) new SetFavorites(API, findViewById(R.id.profile_favorites_recycler)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else if(FAVORITES_TASK.getStatus() == AsyncTask.Status.RUNNING){
                FAVORITES_TASK.cancel(true);
                FAVORITES_TASK = (SetFavorites) new SetFavorites(API, findViewById(R.id.profile_favorites_recycler)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                FAVORITES_TASK = (SetFavorites) new SetFavorites(API, findViewById(R.id.profile_favorites_recycler)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        favorites_size = favorites.getAll().size();
        flag = 0;
    }
    private class SetFavorites extends AsyncTask<String,String,ArrayList<JSONObject>>{

        private RecyclerView rv_01;
        private String API[];

        SetFavorites(String API[]  , RecyclerView rv_01){
            this.API = API;
            this.rv_01 = rv_01;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(rv_01 != null)  rv_01.removeAllViews();
            FavoritesList.clear();
            FavoritesList = new ArrayList<>();
        }

        @Override
        protected ArrayList<JSONObject> doInBackground(String... strings) {
            ArrayList<JSONObject> l = new ArrayList<>();

            for(int i=0; i<API.length; i++) {
                try {
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

                    if(response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null)
                            l.add(new JSONObject(responseBody.string()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                for(JSONObject jsonObject : l) {
                        JSONObject product = jsonObject.getJSONObject("data");
                        if (product == null) continue;
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        ProductClass model = new ProductClass();
                        double final_price = Double.MAX_VALUE;
                        JSONArray assortments = product.getJSONArray("assortments");
                        String[][] assortmentsData = new String[assortments.length()][2];

                        if (product.has("coupons") && product.getJSONArray("coupons").length() > 0) {
                            JSONObject coupon = product.getJSONArray("coupons").getJSONObject(0);
                            if (coupon.getDouble("value") > 0)  model.setCoupon_value(decimalFormat.format(coupon.getDouble("value")));                            else model.setCoupon_value("null");
                            if (coupon.getDouble("value_discount") > 0)  model.setValue_discount(decimalFormat.format(coupon.getDouble("value_discount")));
                            else model.setValue_discount("null");
                        }

                        if (!product.isNull("image_versions")) model.setUrl(product.getJSONObject("image_versions").getString("thumb"));
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
                        model.setBrand_id(product.getString("brand_id"));
                        FavoritesList.add(model);
                    }
            }catch (Exception e){
                Log.e("FAVORITES" , e.getLocalizedMessage());
            }

            return l;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> l) {
            super.onPostExecute(l);

            if(API.length == 0) return;
            try {

                Collections.sort(FavoritesList, new Comparator<ProductClass>() {
                    @Override
                    public int compare(ProductClass product1, ProductClass product2) {
                        double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                        double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                        return Double.compare(price1, price2);
                    }
                });

                Adaptery adp = new Adaptery(getBaseContext(), FavoritesList, ActivityType.MAIN_ACTIVITY);
                rv_01.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                rv_01.setAdapter(adp);
                adp.setOnClickListener((position, model) -> {
                    Intent intent = new Intent(getBaseContext(), ProductView.class);
                    intent.putExtra("PRODUCT_OBJ", model);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                });

                adp.notifyDataSetChanged();
            }catch (Exception e){e.printStackTrace();}

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
            if(API.length == 0) return;
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
                        product_img = JSON_OBJECT[i].getJSONObject("data").getJSONObject("image_versions").getString("thumb");
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
                adp.setOnClickListener((position, model) -> {
                    Intent intent = new Intent(getBaseContext(), ProductView.class);
                    intent.putExtra("PRODUCT_OBJ", model);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                });


            }catch (Exception e){e.printStackTrace();}
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


            if(JSON_OBJECT == null) return;
            if(JSON_OBJECT.length() <= 0 || !JSON_OBJECT.has("data"))  return;

            try {
                JSONArray data = JSON_OBJECT.getJSONArray("data");
                if(data.length() <= 0) return;

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

                    if (!product.isNull("image_versions")) model.setUrl(product.getJSONObject("image_versions").getString("thumb"));
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
                Adaptery adp = new Adaptery(getBaseContext(), product_list, ActivityType.MAIN_ACTIVITY);
                RecommentedRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                RecommentedRecycler.setAdapter(adp);
                adp.setOnClickListener((position, model) -> {
                    Intent intent = new Intent(getBaseContext(), ProductView.class);
                    intent.putExtra("PRODUCT_OBJ", model);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                });



            }catch (Exception e){e.printStackTrace();}

        }



    }
    private class update_shopping_cart extends AsyncTask<String,String, Void>{

        String API;
        private CountDownLatch latch;
        update_shopping_cart(String API,CountDownLatch latch){
            this.API = API;
            this.latch = latch;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(false)
                        .build();

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    if(responseBody != null){
                        try {
                            JSONObject p = new JSONObject(responseBody.string()).getJSONObject("data");

                            String pimg = !p.isNull("image_versions")
                                    ? p.getJSONObject("image_versions").getString("thumb")
                                    : "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png";

                            double final_price = Double.MAX_VALUE;
                            String FINAL_NAME = "";
                            JSONArray ASSORTMENTS = p.getJSONArray("assortments");
                            String assortments[][] = new String[ASSORTMENTS.length()][2];

                            for(int j=0; j<ASSORTMENTS.length(); j++){
                                JSONObject item = ASSORTMENTS.getJSONObject(j);
                                JSONObject retailer = item.getJSONObject("retailer");
                                JSONObject productPivot = item.getJSONObject("product_pivot");
                                if(!supermarkets.contains(retailer.getString("name"))) continue;
                                double current_price = productPivot.isNull("final_price")
                                        ? productPivot.getDouble("start_price")
                                        : productPivot.getDouble("final_price");

                                if (current_price < final_price) {
                                    FINAL_NAME = retailer.getString("name");
                                    final_price = current_price;
                                }
                                assortments[j][0] = retailer.getString("name");
                                assortments[j][1] = String.valueOf(current_price);
                            }
                            if(final_price == Double.MAX_VALUE){
                                shopping_cart.edit().remove(p.getString("name")).apply();
                                return null;
                            }

                            StringBuilder b = new StringBuilder();
                            b.append(final_price + " " + FINAL_NAME.replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "") + " " + pimg + " " + p.getString("id") + "\n");
                            for(int j=0; j<assortments.length; j++)
                                if(assortments[j][0] == null) continue;
                                else{
                                    if(j == assortments.length-1) b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim());
                                    else                          b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim() + "\n");
                                }
                            shopping_cart.edit().putString(p.getString("name") , b.toString().trim()).apply();
                        } catch (JSONException e) { Log.e("SplashScreen" , e.getLocalizedMessage()); }
                    }
                }
            } catch (Exception e) {Log.e("SplashScreen" , e.getLocalizedMessage());} finally {
                latch.countDown();
            }

            return null;
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
