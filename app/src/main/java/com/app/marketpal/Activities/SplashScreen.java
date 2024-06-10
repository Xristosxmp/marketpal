package com.app.marketpal.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.app.marketpal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class SplashScreen extends AppCompatActivity {

    Animation top,bottom;
    ImageView TopImage;
    TextView  logo;

    Button rec;
    TextView rec_error;


    int SPLASH = 1500;
    private SharedPreferences flanch;
    private SharedPreferences supermarkets;
    private SharedPreferences date;
    private SharedPreferences shopping_cart;

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        flanch = getBaseContext().getSharedPreferences("flanch", Context.MODE_PRIVATE);
        date = getBaseContext().getSharedPreferences("date", Context.MODE_PRIVATE);
        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        int TASKS = shopping_cart.getAll().size();
        CountDownLatch latch = new CountDownLatch(TASKS);


        if(!flanch.contains("run"))  {
            flanch.edit().putString("run" , "init").apply();
            supermarkets.edit().putBoolean("MYMARKET" , true).apply();
            supermarkets.edit().putBoolean("ΜΑΣΟΥΤΗΣ" , true).apply();
            supermarkets.edit().putBoolean("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ" , true).apply();
            supermarkets.edit().putBoolean("ΣΚΛΑΒΕΝΙΤΗΣ" , true).apply();
            supermarkets.edit().putBoolean("ΓΑΛΑΞΙΑΣ" , true).apply();
        }
        if(!date.contains("date")){
            date.edit().putString("date" , G()).apply();

        }else{
            String current_date = G();
            String pref_date = date.getString("date" , "");
            //pref_date = "2024-01-12";

            if(!current_date.equals(pref_date)) {
                Set<String> k = shopping_cart.getAll().keySet();
                for (String key : k)
                    new update_shopping_cart("https://v8api.pockee.com/api/v8/public/products/" + shopping_cart.getString(key, "").split("\n")[0].split(" ")[3], latch).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else TASKS = 0;
            date.edit().putString("date" , G()).apply();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean cc = isNetworkConnected(this);

        if(!cc){
            rec = findViewById(R.id.btn_reconnect);
            rec_error = findViewById(R.id.splash_error_text);
            rec.setVisibility(View.VISIBLE);
            rec_error.setVisibility(View.VISIBLE);
            rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }else {

            if(TASKS == 0){
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {latch.await();} catch (InterruptedException e) {throw new RuntimeException(e);}
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            });
                    }
                }).start();
            }
        }
    }

    private String G(){
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        return d.format(new Date());
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
                if(response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    if(responseBody != null){
                        try {
                            JSONObject p = new JSONObject(responseBody.string()).getJSONObject("data");

                            String pimg = !p.isNull("image_versions")
                                    ? "https://d3kdwhwrhuoqcv.cloudfront.net/uploads/products/product-image-404.png"
                                    : p.getJSONObject("image_versions").getString("original");

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
                        } catch (JSONException e) {throw new RuntimeException(e);}
                    }
                }
            } catch (Exception e) {e.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            latch.countDown();
        }
    }


}

