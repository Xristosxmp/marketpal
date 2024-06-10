package com.app.marketpal.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class optimal_cart extends AppCompatActivity {

    private SharedPreferences shopping_cart;
    private SharedPreferences shopping_cart_amount;
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
    double MY_MARKET_SUM;
    double SKLAVENITIS_SUM;
    double AB_SUM;
    double GALAXIAS_SUM;
    double MASOUTHS_SUM;
    private Intent Cart;
    private Intent Profile;
    private TextView total_cost_optimal;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimal_cart);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        shopping_cart_amount = getBaseContext().getSharedPreferences("shopping_cart_amount", Context.MODE_PRIVATE);

        findViewById(R.id.cart_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart = new Intent(getBaseContext() , ShoppingCart.class);
                startActivity(Cart);
                overridePendingTransition(0, 0);

                finish();
            }
        });
        findViewById(R.id.go_to_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile = new Intent(getBaseContext() , com.app.marketpal.Activities.Profile.class);
                startActivity(Profile);
                overridePendingTransition(0, 0);

                finish();
            }
        });
        total_cost_optimal = findViewById(R.id.total_cost_optimal);

        ConfigureVariables();
        Config();

    }
    @Override protected void onResume() {
        super.onResume();
        Config();
    }
    void Config(){
        MY_MARKET_LIST.clear();       MY_MARKET_SUM = 0;
        SKLAVENITIS_LIST.clear();     SKLAVENITIS_SUM = 0;
        AB_LIST.clear();              AB_SUM = 0;
        GALAXIAS_LIST.clear();        GALAXIAS_SUM = 0;
        MASOUTHS_LIST.clear();        MASOUTHS_SUM = 0;
        double TOTAL_PRICE_PR = 0;

        Map<String, ?> allEntries = shopping_cart.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();

            String PRICE = String.valueOf(entry.getValue()).split("\n")[0].split(" ")[0];
            Double PRICE_TYPECAST_DOUBLE = Double.parseDouble(String.valueOf(entry.getValue()).split("\n")[0].split(" ")[0]) * Integer.parseInt(shopping_cart_amount.getString(key , "1").toString());
            TOTAL_PRICE_PR += PRICE_TYPECAST_DOUBLE;


            ProductClass model = new ProductClass();
            model.setName(key);
            model.setUrl(String.valueOf(entry.getValue()).split("\n")[0].split(" ")[2]);
            model.setMarket("NULL");
            model.setOrigianlName(key);

            switch(String.valueOf(entry.getValue()).split("\n")[0].split(" ")[1]) {
                case "MYMARKET":
                    model.setPrice(PRICE + " €");
                    MY_MARKET_SUM += PRICE_TYPECAST_DOUBLE;
                    MY_MARKET_LIST.add(model);
                    break;
                case "ΣΚΛΑΒΕΝΙΤΗΣ":
                    model.setPrice(PRICE + " €");
                    SKLAVENITIS_SUM += PRICE_TYPECAST_DOUBLE;
                    SKLAVENITIS_LIST.add(model);
                    break;
                case "ΑΒ":
                    model.setPrice(PRICE + " €");
                    AB_SUM += PRICE_TYPECAST_DOUBLE;
                    AB_LIST.add(model);
                    break;
                case "ΓΑΛΑΞΙΑΣ":
                    model.setPrice(PRICE + " €");
                    GALAXIAS_SUM += PRICE_TYPECAST_DOUBLE;
                    GALAXIAS_LIST.add(model);
                    break;
                case "ΜΑΣΟΥΤΗΣ":
                    model.setPrice(PRICE + " €");
                    MASOUTHS_SUM += PRICE_TYPECAST_DOUBLE;
                    MASOUTHS_LIST.add(model);
                    break;
                default: break;
            }
        }
        MY_MARKET_ADAPTERY.notifyDataSetChanged();
        SKLAVENITIS_ADAPTERY.notifyDataSetChanged();
        AB_ADAPTERY.notifyDataSetChanged();
        GALAXIAS_ADAPTERY.notifyDataSetChanged();
        MASOUTHS_ADAPTERY.notifyDataSetChanged();


        total_cost_optimal.setText("Συνολικό Κόστος " + String.format("%.2f", TOTAL_PRICE_PR).replace('.', ',') + "€");

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
                public int compare(ProductClass p1, ProductClass p2) {
                    double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(SKLAVENITIS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass p1, ProductClass p2) {
                    double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(AB_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass p1, ProductClass p2) {
                    double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(GALAXIAS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass p1, ProductClass p2) {
                    double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
            Collections.sort(MASOUTHS_LIST, new Comparator<ProductClass>() {
                @Override
                public int compare(ProductClass p1, ProductClass p2) {
                    double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                    double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                    return Double.compare(price1, price2);
                }
            });
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());  symbols.setDecimalSeparator(',');
        TextView detail;
        detail = findViewById(R.id.MYMARKET_DETAILS);
        detail.setText("MYMARKET • " + new DecimalFormat("#0.00", symbols).format(MY_MARKET_SUM) +" €");

        detail = findViewById(R.id.SKLAVENTIS_DETAILS);
        detail.setText("ΣΚΛΑΒΕΝΙΤΗΣ • " + new DecimalFormat("#0.00", symbols).format(SKLAVENITIS_SUM) +" €");

        detail = findViewById(R.id.AB_DETAILS);
        detail.setText("ΑΒ ΒΑΣΙΛΟΠΟΥΛΟΣ • " + new DecimalFormat("#0.00", symbols).format(AB_SUM) +" €");

        detail = findViewById(R.id.GALAXIAS_DETAILS);
        detail.setText("ΓΑΛΑΞΙΑΣ • " + new DecimalFormat("#0.00", symbols).format(GALAXIAS_SUM) +" €");

        detail = findViewById(R.id.MASOUTHS_DETAILS);
        detail.setText("ΜΑΣΟΥΤΗΣ • " + new DecimalFormat("#0.00", symbols).format(MASOUTHS_SUM) +" €");

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
        MY_MARKET_RECYCLER.setItemViewCacheSize(8);
        SKLAVENITIS_RECYCLER.setItemViewCacheSize(8);
        AB_RECYCLER.setItemViewCacheSize(8);
        GALAXIAS_RECYCLER.setItemViewCacheSize(8);
        MASOUTHS_RECYCLER.setItemViewCacheSize(8);
        MY_MARKET_RECYCLER.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        SKLAVENITIS_RECYCLER.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        AB_RECYCLER.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        GALAXIAS_RECYCLER.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        MASOUTHS_RECYCLER.setRecycledViewPool(new RecyclerView.RecycledViewPool());
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
}
