package com.app.marketpal.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    double MY_MARKET_SUM = 0;
    double SKLAVENITIS_SUM = 0;
    double AB_SUM = 0;
    double GALAXIAS_SUM = 0;
    double MASOUTHS_SUM = 0;

    private Intent Cart;
    private Intent Profile;

    private TextView total_cost_optimal;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);

    }

    @Override
    protected void onResume() {
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

            String market = String.valueOf(entry.getValue()).split("\n")[0].split(" ")[1];
            String url = String.valueOf(entry.getValue()).split("\n")[0].split(" ")[2];

            ProductClass model = new ProductClass();
            model.setName(key);
            model.setUrl(url);
            model.setMarket("NULL");
            model.setOrigianlName(key);

            if(market.equals("MYMARKET")){
                model.setPrice(PRICE + " €");
                MY_MARKET_SUM += PRICE_TYPECAST_DOUBLE;
                MY_MARKET_LIST.add(model);
                MY_MARKET_ADAPTERY.notifyDataSetChanged();
                continue;
            }else if(market.equals("ΣΚΛΑΒΕΝΙΤΗΣ")){
                model.setPrice(PRICE + " €");
                SKLAVENITIS_SUM += PRICE_TYPECAST_DOUBLE;
                SKLAVENITIS_LIST.add(model);
                SKLAVENITIS_ADAPTERY.notifyDataSetChanged();
                continue;
            }else if(market.equals("ΑΒ")){
                model.setPrice(PRICE + " €");
                AB_SUM += PRICE_TYPECAST_DOUBLE;
                AB_LIST.add(model);
                AB_ADAPTERY.notifyDataSetChanged();
                continue;
            }else if(market.equals("ΓΑΛΑΞΙΑΣ")){
                model.setPrice(PRICE + " €");
                GALAXIAS_SUM += PRICE_TYPECAST_DOUBLE;
                GALAXIAS_LIST.add(model);
                GALAXIAS_ADAPTERY.notifyDataSetChanged();
                continue;
            }else if(market.equals("ΜΑΣΟΥΤΗΣ")){
                model.setPrice(PRICE + " €");
                MASOUTHS_SUM += PRICE_TYPECAST_DOUBLE;
                MASOUTHS_LIST.add(model);
                MASOUTHS_ADAPTERY.notifyDataSetChanged();
                continue;
            }
        }

        total_cost_optimal.setText("Συνολικό Κόστος " + String.format("%.2f", TOTAL_PRICE_PR).replace('.', ',') + "€");

        Collections.sort(MY_MARKET_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(SKLAVENITIS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(AB_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(GALAXIAS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(MASOUTHS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });

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

        Collections.sort(MY_MARKET_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(SKLAVENITIS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(AB_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(GALAXIAS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });
        Collections.sort(MASOUTHS_LIST, new Comparator<ProductClass>() {
            @Override
            public int compare(ProductClass product1, ProductClass product2) {
                double price1 = Double.parseDouble(product1.getPrice().replace(" €" , ""));
                double price2 = Double.parseDouble(product2.getPrice().replace(" €" , ""));
                return Double.compare(price1, price2);
            }
        });


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimal_cart);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        shopping_cart = getBaseContext().getSharedPreferences("shopping_cart", Context.MODE_PRIVATE);
        shopping_cart_amount = getBaseContext().getSharedPreferences("shopping_cart_amount", Context.MODE_PRIVATE);

        findViewById(R.id.cart_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart = new Intent(getBaseContext() , ShoppingCart.class);
                Cart.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Cart.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Cart);
                finish();
            }
        });
        findViewById(R.id.go_to_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile = new Intent(getBaseContext() , com.app.marketpal.Activities.Profile.class);
                Profile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Profile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Profile);
                finish();
            }
        });
        total_cost_optimal = findViewById(R.id.total_cost_optimal);

        ConfigureVariables();
        Config();

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


}
