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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean cc = isNetworkConnected(this);

        flanch = getBaseContext().getSharedPreferences("flanch", Context.MODE_PRIVATE);
        date = getBaseContext().getSharedPreferences("date", Context.MODE_PRIVATE);
        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);


        if(!flanch.contains("run"))  {flanch.edit().putString("run" , "init").apply();
            supermarkets.edit().putBoolean("MYMARKET" , true).apply();
            supermarkets.edit().putBoolean("ΜΑΣΟΥΤΗΣ" , true).apply();
            supermarkets.edit().putBoolean("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ" , true).apply();
            supermarkets.edit().putBoolean("ΣΚΛΑΒΕΝΙΤΗΣ" , true).apply();
            supermarkets.edit().putBoolean("ΓΑΛΑΞΙΑΣ" , true).apply();
        }
        if(!date.contains("date")) date.edit().putString("date" , G()).apply();
        else{

            if(!cc){
                rec = findViewById(R.id.btn_reconnect);
                rec_error = findViewById(R.id.splash_error_text);
                rec.setVisibility(View.VISIBLE);
                rec_error.setVisibility(View.VISIBLE);
                rec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = getIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else {
                String current_date = G();
                String pref_date = date.getString("date", "");
                if (!current_date.equals(pref_date))
                    new update_shopping_cart().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                date.edit().putString("date", G()).apply();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }
                }, SPLASH);
            }
        }
    }

    private String G(){
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        return d.format(new Date());
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
                        //assortments[j][1] = "5.5";
                    }

                    StringBuilder b = new StringBuilder();
                    b.append(FINAL_PRICE + " " + FINAL_NAME.replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "") + " " + pimg + " " + pid + "\n");
                    for(int j=0; j<assortments.length; j++)
                        if(assortments[j][0] == null) continue;
                        else{
                            if(j == assortments.length-1) b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim());
                            else                          b.append(assortments[j][1] + " " + assortments[j][0].replace(" ΒΑΣΙΛΟΠΟΥΛΟΣ" , "").trim() + "\n");
                        }
                    shopping_cart.edit().putString(pname , b.toString().trim()).apply();
                } catch (JSONException e) {throw new RuntimeException(e);}
            }
        }
    }

}

