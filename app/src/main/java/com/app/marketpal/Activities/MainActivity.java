/**
 *
 *              Copyright | 2023
 *
 *              Android Εφαρμογή
 *
 *
 *
 *
 *
 *
 *
 * **/

package com.app.marketpal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.app.marketpal.Adapters.Adaptery;
import com.app.marketpal.Adapters.AdapteryII;
import com.app.marketpal.Models.CategoryClass;
import com.app.marketpal.Models.ProductClass;
import com.app.marketpal.R;
import com.app.marketpal.enumActivities.ActivityType;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {


    private SearchView searchView;


    private Intent SearchIntent;
    private Intent OffersIntent;
    private Intent ProfileIntent;
    private Intent CartIntent;
    private Intent MessagesIntent;

    private RecyclerView parent_recycler_view;
    private List<CategoryClass> category_list;
    private AdapteryII adapter;

    private String CategorySelector;

    private CategoryClass config;

    private DrawerLayout drawer;
    private NavigationView d;

    private SharedPreferences supermarkets;
    private int supermarkets_size;

    private LinearLayoutManager RecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        d = findViewById(R.id.NavigationViewLeft);
        d.setItemIconTintList(null);
        drawer = findViewById(R.id.application_menu);

        supermarkets = getBaseContext().getSharedPreferences("supermarkets", Context.MODE_PRIVATE);
        supermarkets_size = supermarkets.getAll().size();

        category_list = new ArrayList<>();
        category_list.add(new CategoryClass(CategoryClass.HEADER_TYPE));
        adapter = new AdapteryII(this , category_list);
        adapter.setHasStableIds(true);

        parent_recycler_view = findViewById(R.id.main_recycler);
        parent_recycler_view.setItemAnimator(null);
        parent_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        parent_recycler_view.setAdapter(adapter);
        parent_recycler_view.setHasFixedSize(true);
        parent_recycler_view.setItemViewCacheSize(15);
        parent_recycler_view.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        parent_recycler_view.setNestedScrollingEnabled(false);
        RecyclerViewManager = (LinearLayoutManager) parent_recycler_view.getLayoutManager();

        Settings();
        HomeNavigation();
        OffersNavigation();
        NavigateSearch();
        setBorderOnScroll();
        ProfileNavigation();
        NavigateCart();
        DataInit();
        Messages();

    }



    private void DataInit(){


        int corePoolSize = Runtime.getRuntime().availableProcessors(); // Adjust as needed
        int maximumPoolSize = corePoolSize * 2; // Adjust as needed
        long keepAliveTime = 15L; // Time for non-core threads to be kept alive
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(128 * 2); // Adjust the capacity

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue
        ); pool.allowCoreThreadTimeOut(true);

        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=158&page=1&per_page=30&in_stock=true" , "Καλάθι του Νοικοκυριού" ,
                "Ανακαλύψτε το καλάθι του νοικοκοιριού σε προϊόντα της εβδομάδας. Τρόφιμα, Γαλακτοκομικά, Τυριά και Χυμούς, Σνακς, Κάβα, Προσωπική Φροντίδα, Οικιακή Φροντίδα, Παιδικά, Βρεφικά & Διάφορα" , "Τρόφιμα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=645&page=1&per_page=30&in_stock=true" , "Γαλακτοκομικά & Τυριά", new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=159&page=1&per_page=30&in_stock=true" , "Χυμοί, Σνακς & Κάβα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=30&page=1&per_page=30&in_stock=true" , "Προσωπική φροντίδα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=39&page=1&per_page=30&in_stock=true" , "Οικιακή φροντίδα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=40&page=1&per_page=30&in_stock=true" , "Παιδικά & Βρεφικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?type=HOME_BASKET&category_id=463&page=1&per_page=30&in_stock=true" , "Διάφορα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=214&page=1&per_page=30&in_stock=true" , "Παντοπωλείο" ,
                "Οι καλύτερες τιμές στο παντοπωλείο απευθείας σε εσάς. χυμός/πελτές τομάτας, σάλτσες, dips & dressing, αλάτι & μπαχαρικά, ζάχαρη, αλεύρι κ.ά., είδη επάλειψης, λάδι, ξύδι, ζαχαροπλαστική, ετοιμα γεύματα ψυγείου, delicatessen, αυγά και ψάρια/θαλασσινά σε κονσέρβα" , "Χυμός/πελτές τομάτας"  , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=215&page=1&per_page=30&in_stock=true" , "Σάλτσες, Dips & Dressing" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=299&page=1&per_page=30&in_stock=true" , "Αλάτι & Μπαχαρικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=217&page=1&per_page=30&in_stock=true" , "Ζάχαρη, Αλεύρι κ.ά." , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=218&page=1&per_page=30&in_stock=true" , "Είδη επάλειψης" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=54&page=1&per_page=30&in_stock=true" , "Λάδι" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=823&page=1&per_page=30&in_stock=true" , "Ξύδι" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=298&page=1&per_page=30&in_stock=true" , "Ζαχαροπλαστική" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=311&page=1&per_page=30&in_stock=true" , "Έτοιμα γεύματα ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=698&page=1&per_page=30&in_stock=true" , "Delicatessen" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=778&page=1&per_page=30&in_stock=true" , "Αυγά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=997&page=1&per_page=30&in_stock=true" , "Ψάρια/Θαλασσινά σε κονσέρβα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=367&page=1&per_page=30&in_stock=true" , "Γάλατα Ραφιού",
                "Οι καλύτερες τιμές για γάλατα ραφιού απευθείας σε εσάς. εβαπορέ γάλατα, UHT γάλατα μακράς διάρκειας, φυτικά γάλατα ραφιού και ζαχαρούχα γάλατα", "Εβαπορέ Γάλατα"   , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=368&page=1&per_page=30&in_stock=true" , "UHT Γάλατα Μακράς Διάρκειας" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=639&page=1&per_page=30&in_stock=true" , "Φυτικά Γάλατα Ραφιού" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=644&page=1&per_page=30&in_stock=true" , "Ζαχαρούχα Γάλατα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=370&page=1&per_page=30&in_stock=true" , "Φρέσκα Γάλατα" , "Οι καλύτερες τιμές για φρέσκα γάλατα απευθείας σε εσάς. φρέσκο γάλα, γάλα υψηλής θερμικής επεξεργασίας, φυτικά ροφήματα, αριάνι/κεφίρ/ξινόγαλο, σοκολατούχα γάλατα, ροφήματα γιαουρτιού και παιδικά γάλατα ψυγείου", "Φρέσκα Γάλατα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=638&page=1&per_page=30&in_stock=true" , "Γάλατα Υψηλής Θερμικής Επεξεργασίας" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=373&page=1&per_page=30&in_stock=true" , "Φυτικά Ροφήματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=374&page=1&per_page=30&in_stock=true" , "Αριάνι/Κεφίρ/Ξινόγαλο" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=376&page=1&per_page=30&in_stock=true" , "Σοκολατούχο/Με Γεύσεις Γάλατα Ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=476&page=1&per_page=30&in_stock=true" , "Ροφήματα Γιαουρτιού" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=484&page=1&per_page=30&in_stock=true" , "Παιδικά Γάλατα ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=34&page=1&per_page=30&in_stock=true" , "Τυριά" , "Οι καλύτερες τιμές για τυριά απευθείας σε εσάς. Κίτρινα τυριά, λευκά τυριά, άλλα τυριά", "Κίτρινα Τυριά"  , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=391&page=1&per_page=30&in_stock=true" , "Λευκά Τυριά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=398&page=1&per_page=30&in_stock=true" , "Άλλα Τυριά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=648&page=1&per_page=30&in_stock=true" , "Γιαούρτια & Επιδόρπια" , "Οι καλύτερες τιμές σε γιαούρτια & επιδόρπια απευθείας σε εσάς. Γιαούρτια, παραδοσιακά γιαούρτια, παιδικά & βρεφικά γιαούρτια, επιδόρπια ψυγείου" , "Γιαούρτια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=380&page=1&per_page=30&in_stock=true" , "Παραδοσιακά Γιαούρτια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=641&page=1&per_page=30&in_stock=true" , "Παιδικά & Βρεφικά Γιαούρτια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=641&page=1&per_page=30&in_stock=true" , "Παιδικά & Βρεφικά Γιαούρτια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=665&page=1&per_page=30&in_stock=true" , "Επιδόρπια Ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=663&page=1&per_page=30&in_stock=true" , "Κρέμες Γάλακτος & Βούτυρα" , "Οι καλύτερες τιμές σε κρέμες γάλακτος & βούτυρα απευθείας σε εσάς. Κρέμες γάλακτος & σαντιγί, βούτυρα & μαργαρίνες" , "Κρέμες Γάλακτος & Σαντιγί" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=664&page=1&per_page=30&in_stock=true" , "Βούτυρα & Μαργαρίνες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=513&page=1&per_page=30&in_stock=true" , "Παγωτά & Είδη Ψυγείου" , "Οι καλύτερες τιμές σε παγωτά & είδη ψυγείου απευθείας σε εσάς." , "Παγωτά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=670&page=1&per_page=30&in_stock=true" , "Λοιπά Είδη Ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=230&page=1&per_page=30&in_stock=true" , "Μπύρες, Ποτά, Νερά" , "Οι καλύτερες τιμές σε μπύρες, ποτά, νερά απευθείας σε εσάς. Μπύρες & μηλίτης, κρασί, ουίσκι, ούζο, τσίπουρο & άλλα αποστάγματα, άλλα ποτά, φυσικά μεταλλικά & ανθρακούχα νερά" , "Μπύρες & Μηλίτης" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=231&page=1&per_page=30&in_stock=true" , "Κρασιά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=313&page=1&per_page=30&in_stock=true" , "Ουίσκια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=319&page=1&per_page=30&in_stock=true" , "Ούζο, Τσίπουρο & Αλλα Αποστάγματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=232&page=1&per_page=30&in_stock=true" , "Άλλα Ποτά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=233&page=1&per_page=30&in_stock=true" , "Φυσικά Μεταλλικά & Ανθρακούχα Νερά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=227&page=1&per_page=30&in_stock=true" , "Χυμοί & Αναψυκτικά" , "Οι καλύτερες τιμές σε χυμούς και αναψυκτικά, Χυμοί, Cola & άλλα αναψυκτικά και κρύο τσάι" , "Χυμοί" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=229&page=1&per_page=30&in_stock=true" , "Cola & Άλλα Αναψυκτικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=6353&page=1&per_page=30&in_stock=true" , "Κρύο τσάι" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=220&page=1&per_page=30&in_stock=true" , "Σνακς" , "Οι καλύτερες τιμές σε σνακς, Πατατάκια, γαριδάκια & easy snacks, σοκολάτες, μπισκότα, κρουασάν & άλλα σνακς, ξηροί καρποί & αποξηραμένα φρούτα, τσίχλες, καραμέλες" , "Πατατάκια, Γαριδάκια & Easy Snacks" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=221&page=1&per_page=30&in_stock=true" , "Σοκολάτες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=846&page=1&per_page=30&in_stock=true" , "Μπισκότα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=222&page=1&per_page=30&in_stock=true" , "Κρουασάν & Άλλα Σνακς" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=223&page=1&per_page=30&in_stock=true" , "Ξηροί καρποί & Αποξηραμένα Φρούτα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=491&page=1&per_page=30&in_stock=true" , "Τσίχλες, Καραμέλες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=224&page=1&per_page=30&in_stock=true" , "Καφές & Ροφήματα" , "Οι καλύτερες τιμές σε καφέδες & ροφήματα. Καφές, τσάι & αφεψήματα, ρόφηματα κακάο" , "Καφές" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=225&page=1&per_page=30&in_stock=true" , "Τσάι & Αφεψήματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=226&page=1&per_page=30&in_stock=true" , "Ρόφημα Κακάο" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=608&page=1&per_page=30&in_stock=true" , "Φρούτα & Λαχανικά" ,
                "Οι καλύτερες τιμές σε φρούτα & λαχανικά απευθείας σε εσάς." , "Φρούτα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=46&page=1&per_page=30&in_stock=true" , "Λαχανικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=205&page=1&per_page=30&in_stock=true" , "Αλλαντικά & Κρέατα" ,
                "Οι καλύτερες τιμές για αλλαντικά & κρέατα απευθείας σε εσάς. Αλλαντικά, Κατεψυγμένο κρέας & γεύματα με κρέας, Νωπό κρέας & Πουλερικά" , "Αλλαντικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=207&page=1&per_page=30&in_stock=true" , "Κατεψυγμένο Κρέατα & Γεύματα με Κρέας" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=50&page=1&per_page=30&in_stock=true" , "Νωπό Κρέας & Πουλερικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=783&page=1&per_page=30&in_stock=true" , "Ψάρια & Θαλασσινά" ,
                "Οι καλύτερες τιμές για Ψάρια Ιχθυοκαλλιέργειας, Χταπόδια, Καλαμάρια, Σουπιές & Οστρακοειδή απευθείας σε εσάς." , "Φρέσκα Ψάρια & θαλασσινά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=782&page=1&per_page=30&in_stock=true" , "Κατεψυγμένα Ψάρια & θαλασσινά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=49&page=1&per_page=30&in_stock=true" , "Ψωμί & Δημητριακά" ,
                "Οι καλύτερες τιμές για ψωμί & δημητριακά απευθείας σε εσάς. Ψωμί, ψωμάκια, πίτες, δημητρικά, μπάρες δημητριακών, κέικ και άλλα γλυκά" , "Ψωμί, Ψωμάκια & Πίτες"  , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=192&page=1&per_page=30&in_stock=true" , "Δημητριακά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=193&page=1&per_page=30&in_stock=true" , "Μπάρες Δημητριακών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=297&page=1&per_page=30&in_stock=true" , "Κέικ και άλλα γλυκά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=197&page=1&per_page=30&in_stock=true" , "Ζυμαρικά & Όσπρια" ,
                "Οι καλύτερες τιμές για Ζυμαρικά, Ρύζι, Όσπρια, Κύβοι, Σούπες, Πουρές και άλλα σχετικά απευθείας σε εσάς." , "Ζυμαρικά", new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=48&page=1&per_page=30&in_stock=true" , "Ρύζι" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=199&page=1&per_page=30&in_stock=true" , "Όσπρια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=200&page=1&per_page=30&in_stock=true" , "Κύβοι, Σούπες, Πουρές και άλλα σχετικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=213&page=1&per_page=30&in_stock=true" , "Προϊόντα Ζύμης" ,
                "Οι καλύτερες τιμές σε προιόντα ζύμης απευθείας σε εσάς. Έτοιμες πίτες, πιτάκια, τρίγωνα, croissants κ.ά., κατεψυγμένα φύλλα ζύμης, φύλλα ζύμης ψυγείου, πίτσες & πεϊνιρλί, γλυκες πίτες και ζύμες" , "Έτοιμες Πίτες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=211&page=1&per_page=30&in_stock=true" , "Πιτάκια,Τρίγωνα, Croissants κ.ά." , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=209&page=1&per_page=30&in_stock=true" , "Κατεψυγμένα φύλλα ζύμης" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=210&page=1&per_page=30&in_stock=true" ,"Φύλλα ζύμης ψυγείου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=212&page=1&per_page=30&in_stock=true" , "Πίτσες & Πεϊνιρλί" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=796&page=1&per_page=30&in_stock=true" ,"Γλυκές Πίτες και Ζύμες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=255&page=1&per_page=30&in_stock=true" , "Γυναικεία Περιποίηση" ,
                "Οι καλύτερες τιμές σε προιόντα γυναικείας περιποίησης απευθείας σε εσάς. ξυριστικά & αποτρίχωση, περιποίηση προσώπου, σερβιέτες, σερβιετάκια κ.ά., μακιγιάζ & αρώματα, σετ δώρου γυναικεία" , "Ξυριστικά & Αποτρίχωση" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=256&page=1&per_page=30&in_stock=true" ,"Περιποίηση Προσώπου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=257&page=1&per_page=30&in_stock=true" , "Σερβιέτες, Σερβιετάκια κ.ά." , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=258&page=1&per_page=30&in_stock=true" , "Μακιγιάζ & Αρώματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=1020&page=1&per_page=30&in_stock=true" , "Σετ δώρου γυναικεία" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=259&page=1&per_page=30&in_stock=true" , "Αντρική Περιποίηση" ,
                "Οι καλύτερες τιμές σε προιόντα αντρικής περιποίησης απευθείας σε εσάς. Ξυραφάκια & μηχανές, ανταλλακτικά ξυρίσματος, αφροί, gel & κρέμες ξυρίσματος, after shave & αρώματα, σετ δώρου αντρικά" , "Ξυραφάκια & Μηχανές" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=260&page=1&per_page=30&in_stock=true" , "Ανταλλακτικά ξυρίσματος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=261&page=1&per_page=30&in_stock=true" , "Αφροί, Gel & Κρέμες Ξυρίσματος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=262&page=1&per_page=30&in_stock=true" , "After Shave & Αρώματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=1018&page=1&per_page=30&in_stock=true" , "Σετ δώρου ανδρικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=306&page=1&per_page=30&in_stock=true" , "Καθαριότητα & Προσωπική Υγιεινή" ,
                "Οι καλύτερες τιμές σε προιόντα καθαριότητας & προσωπικής υγιεινής απευθείας σε εσάς. Προφυλακτικά, αλκοολούχες λοσιόν, προϊόντα ακράτειας, υγρομάντηλα, βαμβάκια, μπατονέτες κ.ά, λίμες & νυχοκόπτες, αντισηπτικά χεριών, μάσκες προσώπου και σφουγγάρια μπάνιου" , "Προφυλακτικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=790&page=1&per_page=30&in_stock=true" , "Αλκοολούχες Λοσιόν" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=408&page=1&per_page=30&in_stock=true" , "Προϊόντα Ακράτειας" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=449&page=1&per_page=30&in_stock=true" , "Υγρομάντηλα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=411&page=1&per_page=30&in_stock=true" , "Βαμβάκια, Μπατονέτες κ.ά." , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=412&page=1&per_page=30&in_stock=true" , "Λίμες & Νυχοκόπτες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=685&page=1&per_page=30&in_stock=true" , "Αντισηπτικά Χεριών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=694&page=1&per_page=30&in_stock=true" , "Μάσκες Προσώπου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=1028&page=1&per_page=30&in_stock=true" , "Σφουγγάρια Μπάνιου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=263&page=1&per_page=30&in_stock=true" , "Περιποίηση Μαλλιών" ,
                "Οι καλύτερες τιμές σε προιόντα περιποίησης μαλλιών απευθείας σε εσάς. Σαμπουάν μαλλιών, Conditioner & Μάσκες μαλλιών, Προϊόντα Styling, Βαφές Μαλλιών" , "Σαμπουάν μαλλιών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=264&page=1&per_page=30&in_stock=true" , "Conditioner & Μάσκες Μαλλιών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=265&page=1&per_page=30&in_stock=true" , "Προϊόντα Styling" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=360&page=1&per_page=30&in_stock=true" , "Βαφές Μαλλιών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=270&page=1&per_page=30&in_stock=true" , "Στοματική Υγιεινή" ,
                "Οι καλύτερες τιμές σε προιόντα στοματικής υγιεινή απευθείας σε εσάς. Οδοντόκρεμες, Οδοντόβουρτσες, Στοματικά διαλύματα, Οδοντικά νήματα, Προϊόντα τεχνητής οδοντοστοιχίας" , "Οδοντόκρεμες\n" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=271&page=1&per_page=30&in_stock=true" , "Οδοντόβουρτσες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=272&page=1&per_page=30&in_stock=true" , "Στοματικά Διαλύματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=273&page=1&per_page=30&in_stock=true" , " Οδοντικά Νήματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=687&page=1&per_page=30&in_stock=true" , " Προϊόντα τεχνητής οδοντοστοιχίας" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=274&page=1&per_page=30&in_stock=true" , "Ένδυση & Υπόδηση" ,
                "Οι καλύτερες τιμές σε προιόντα ένδυσης & υπόδησης απευθείας σε εσάς. Ενδύματα, Υποδήματα, Περιποίηση υποδημάτων" , "Ενδύματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=275&page=1&per_page=30&in_stock=true" , " Υποδήματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=276&page=1&per_page=30&in_stock=true" , " Περιποίηση Υποδημάτων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=266&page=1&per_page=30&in_stock=true" , "Προϊόντα Περιποίησης" ,
                "Οι καλύτερες τιμές σε προϊόντα περιποίησης απευθείας σε εσάς. Αφρόλουτρα, Αποσμητικά, Αντηλιακά, Προϊόντα φροντίδας σώματος, Κρεμοσάπουνα & Σαπούνια Χεριών, Εντομοαπωθητικά σώματος" , "Αφρόλουτρα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=267&page=1&per_page=30&in_stock=true" , " Αποσμητικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=268&page=1&per_page=30&in_stock=true" , " Αντηλιακά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=269&page=1&per_page=30&in_stock=true" , " Προϊόντα Φροντίδας Σώματος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=307&page=1&per_page=30&in_stock=true" , " Κρεμοσάπουνα & Σαπούνια Χεριών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=415&page=1&per_page=30&in_stock=true" , " Εντομοαπωθητικά Σώματος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=234&page=1&per_page=30&in_stock=true" , "Κουζίνα & Μπάνιο" ,
                "Οι καλύτερες τιμές σε προϊόντα κουζίνας & μπάνιου απευθείας σε εσάς. Απορρυπαντικά πιάτων, Φύλαξη & προστασία τροφίμων, Μπλοκ τουαλέτας, αποφρακτικά & καθαριστικά, Χαρτί κουζίνας & χαρτοπετσέτες, Χαρτί υγείας & χαρτομάντηλα" , "Απορρυπαντικά Πιάτων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=235&page=1&per_page=30&in_stock=true" , "Φύλαξη & Προστασία Τροφίμων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=237&page=1&per_page=30&in_stock=true" , "Μπλοκ Τουαλέτας, Αποφρακτικά & Καθαριστικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=238&page=1&per_page=30&in_stock=true" , "Χαρτί Κουζίνας & Χαρτοπετσέτες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=239&page=1&per_page=30&in_stock=true" , "Χαρτί Υγείας & Χαρτομάντηλα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=242&page=1&per_page=30&in_stock=true" , "Καθαριότητα Σπιτιού" ,
                "Οι καλύτερες τιμές σε προϊόντα καθαριότητα σπιτιού απευθείας σε εσάς. Καθαριστικά Μικρών επιφανειών, Καθαριστικά Μεγάλων επιφανειών, Καθαριστικά με Χλώριο, Σφουγγάρια, σπογγοπετσέτες & πανάκια, Γάντια, Εντομοκτόνα, Εντομοαπωθητικά, Σακούλες απορριμμάτων" , "Καθαριστικά Μικρών Επιφανειών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=720&page=1&per_page=30&in_stock=true" , "Καθαριστικά Μεγάλων Επιφανειών" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=721&page=1&per_page=30&in_stock=true" , "Καθαριστικά με Χλώριο" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=244&page=1&per_page=30&in_stock=true" , "Σφουγγάρια, Σπογγοπετσέτες & Πανάκια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=424&page=1&per_page=30&in_stock=true" , "Γάντια" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=246&page=1&per_page=30&in_stock=true" , "Εντομοκτόνα, Εντομοαπωθητικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=247&page=1&per_page=30&in_stock=true" , "Σακούλες Απορριμμάτων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=714&page=1&per_page=30&in_stock=true" , "Πλύσιμο Ρούχων" ,
                "Οι καλύτερες τιμές σε προϊόντα πλύσιμο ρούχων απευθείας σε εσάς. Απορρυπαντικά Πλυντηρίου Ρούχων, Πλύσιμο στο χέρι, Μαλακτικά Ρούχων, Βοηθητικά Πλύσης, Φύλαξη, Σιδέρωμα & Άπλωμα Ρούχων, ποσκληρυντικά Πλυντηρίου" , "Απορρυπαντικά Πλυντηρίου Ρούχων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=251&page=1&per_page=30&in_stock=true" , "Πλύσιμο στο Χέρι" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=252&page=1&per_page=30&in_stock=true" , "Μαλακτικά Ρούχων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=253&page=1&per_page=30&in_stock=true" , "Βοηθητικά Πλύσης" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=254&page=1&per_page=30&in_stock=true" , "Φύλαξη, Σιδέρωμα & Άπλωμα Ρούχων" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=326&page=1&per_page=30&in_stock=true" , "Αποσκληρυντικά Πλυντηρίου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=308&page=1&per_page=30&in_stock=true" , "Εξοπλισμός Σπιτιού" ,
                "Οι καλύτερες τιμές σε προϊόντα εξοπλισμό σπιτιού απευθείας σε εσάς. Αρωματικά χώρου, Καθαρισμός Πατώματος & Ξεσκόνισμα, Προϊόντα μιας Χρήσης, Ηλεκτρικές Συσκευές, Λευκά Είδη, Είδη Σερβιρίσματος, Μπαταρίες, Γραφική ύλη & αναλώσιμα, Λάμπες, Φιάλες-Εστίες Υγραερίου" , "Αρωματικά Χώρου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=310&page=1&per_page=30&in_stock=true" , "Καθαρισμός Πατώματος & Ξεσκόνισμα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=301&page=1&per_page=30&in_stock=true" , "Προϊόντα μιας Χρήσης" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=302&page=1&per_page=30&in_stock=true" , "Ηλεκτρικές Συσκευές" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=303&page=1&per_page=30&in_stock=true" , "Λευκά Είδη" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=304&page=1&per_page=30&in_stock=true" , "Είδη Σερβιρίσματος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=342&page=1&per_page=30&in_stock=true" , "Μπαταρίες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=430&page=1&per_page=30&in_stock=true" , "Γραφική Ύλη & Αναλώσιμα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=431&page=1&per_page=30&in_stock=true" , "Λάμπες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=693&page=1&per_page=30&in_stock=true" , "Φιάλες-Εστίες Υγραερίου" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=277&page=1&per_page=30&in_stock=true" , "Περιποίηση Βρέφους" ,
                "Οι καλύτερες τιμές σε προϊόντα περιποίησης βρέφους απευθείας σε εσάς. Παιδικά Σαμπουάν, Αφρόλουτρα παιδικά, Κρέμες, Λάδια & Πούδρες" , "Παιδικά Σαμπουάν" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=278&page=1&per_page=30&in_stock=true" , "Παιδικά Αφρόλουτρα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=279&page=1&per_page=30&in_stock=true" , "Κρέμες, Λάδια & Πούδρες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=282&page=1&per_page=30&in_stock=true" , "Βρεφικές Τροφές" ,
                "Οι καλύτερες τιμές σε προϊόντα βρεφικών τροφών απευθείας σε εσάς. Έτοιμα Βρεφικά Γεύματα" , "Έτοιμα Βρεφικά Γεύματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=281&page=1&per_page=30&in_stock=true" , "Παιδικά Ροφήματα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=283&page=1&per_page=30&in_stock=true" , "Βρεφικές Κρέμες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=628&page=1&per_page=30&in_stock=true" , "Βρεφικά Σνακς" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=485&page=1&per_page=30&in_stock=true" , "Παιδικό Γάλα σε Σκόνη" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=284&page=1&per_page=30&in_stock=true" , "Πάνες & Μωρομάντηλα" ,
                "Οι καλύτερες τιμές σε προϊόντα πάνων & μωρομάντηλων απευθείας σε εσάς. Πάνες, Μωρομάντηλα" , "Πάνες" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=285&page=1&per_page=30&in_stock=true" , "Μωρομάντηλα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=286&page=1&per_page=30&in_stock=true" , "Βρεφικά Απορρυπαντικά" ,
                "Οι καλύτερες τιμές σε προϊόντα βρεφικών απορρυπαντικών απευθείας σε εσάς. Βρεφικά Απορρυπαντικά , Βρεφικά Μαλακτικά" , "Βρεφικά Απορρυπαντικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=287&page=1&per_page=30&in_stock=true" , "Βρεφικά Μαλακτικά" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=184&page=1&per_page=30&in_stock=true" , "Κατικοίδια" ,
                "Οι καλύτερες τιμές για κατικοίδια απευθείας σε εσάς. Βρεφικά Απορρυπαντικά , Βρεφικά Μαλακτικά" , "Σκύλος" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=185&page=1&per_page=30&in_stock=true" , "Γάτα" , new ArrayList<>()).executeOnExecutor(pool);
        new CollectData("https://v8api.pockee.com/api/v8/public/products?category_id=186&page=1&per_page=30&in_stock=true" , "Αξεσουάρ & Υγιεινή Ζώων" , new ArrayList<>()).executeOnExecutor(pool);

        HorizontalScrollView category_horizontal_scroll = findViewById(R.id.nav_categories_inside_scroll);
        LinearLayout categoryContainer = findViewById(R.id.CategoryContainer);
        int ChildLoops = categoryContainer.getChildCount();
        parent_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int start_index = layoutManager.findFirstVisibleItemPosition();
                int last_index = layoutManager.findLastVisibleItemPosition();
                String categ = null;
                int maxCount = 0;
                Map<String, Integer> categoryTitleCount = new HashMap<>();
                for (int i = start_index; i <= last_index; i++) {
                    CategoryClass config = category_list.get(i);
                    String categoryTitle = config.getCategory_title();

                    if (categoryTitle != null) {
                        int count;
                        if (categoryTitleCount.containsKey(categoryTitle)) count = categoryTitleCount.get(categoryTitle) + 1;
                        else count = 1;
                        categoryTitleCount.put(categoryTitle, count);
                        if (count > maxCount) {
                            maxCount = count;
                            categ = categoryTitle;
                        }
                    }
                }
                int scrollViewWidth = category_horizontal_scroll.getWidth();
                View firstChild = category_horizontal_scroll.getChildAt(0);
                if (categ != null) {
                    for (int j = 0; j < ChildLoops; j++) {
                        View ln = categoryContainer.getChildAt(j);
                        if (ln instanceof LinearLayout) {
                            TextView category = (TextView) ((LinearLayout) ln).getChildAt(0);
                            if (categ.equals(category.getText())) {
                                category.setTextColor(ContextCompat.getColor(getBaseContext(), android.R.color.black));
                                View underline = ((LinearLayout) ln).getChildAt(1);
                                underline.setVisibility(View.VISIBLE);
                                int targetViewWidth = ln.getWidth();
                                int targetScrollX = (int) (ln.getX() + ln.getWidth() / 2 - scrollViewWidth / 2);
                                targetScrollX = Math.max(0, Math.min(targetScrollX, firstChild.getWidth() - scrollViewWidth));
                               category_horizontal_scroll.smoothScrollTo(targetScrollX , 0);
                             } else {
                                category.setTextColor(Color.parseColor("#696969")); // Use your own color resource
                                View underline = ((LinearLayout) ln).getChildAt(1);
                                underline.setVisibility(View.GONE);
                            }
                        }
                    }
                }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView productAmount = findViewById(R.id.product_amount);
        TextView productAmountNav = findViewById(R.id.product_amount_nav);

        int size = getSharedPreferences("shopping_cart", Context.MODE_PRIVATE).getAll().size();
        if(size == 0) {productAmount.setVisibility(View.GONE);productAmountNav.setVisibility(View.GONE);}
        else {productAmountNav.setVisibility(View.VISIBLE);productAmount.setVisibility(View.VISIBLE);productAmount.setText(String.valueOf(size));productAmountNav.setText(String.valueOf(size));}
    }

    private void Settings(){

        findViewById(R.id.category_dropout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer((int) Gravity.LEFT);
            }
        });

        FrameLayout cart = findViewById(R.id.cart_container);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ShoppingCart.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout cart_nav = findViewById(R.id.cart_container_nav);
        cart_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ShoppingCart.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setBorderOnScroll() {
        LinearLayout hz = findViewById(R.id.Header);
        parent_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(-1)) {
                    hz.setElevation(0f);
                } else {
                    hz.setElevation(dpToPx(8));
                }
            }
        });
    }

    private void Messages(){
        findViewById(R.id.messages_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagesIntent = new Intent(getBaseContext() , MessagesActivity.class);
                startActivity(MessagesIntent);
                overridePendingTransition(0, 0);
            }
        });
    }
    private void ProfileNavigation(){
        LinearLayout ln = findViewById(R.id.account_nav);
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileIntent = new Intent(getBaseContext() , Profile.class);
                startActivity(ProfileIntent);
                overridePendingTransition(0, 0);
            }
        });
    }
    private void NavigateSearch(){
        findViewById(R.id.focussearchbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchIntent = new Intent(getBaseContext() , SearchActivity.class);
                startActivity(SearchIntent);
                overridePendingTransition(0, 0);
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

    }
    private void HomeNavigation() {
        findViewById(R.id.homenav).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                 ((LinearLayoutManager)parent_recycler_view.getLayoutManager()).scrollToPositionWithOffset(0,0);
            }
        });
    }
    private void OffersNavigation(){
        findViewById(R.id.today_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersIntent = new Intent(getBaseContext() , Offers_activity.class);
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
                startActivity(CartIntent);
                overridePendingTransition(0, 0);

            }
        });
        findViewById(R.id.cart_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartIntent = new Intent(getBaseContext() , ShoppingCart.class);
                CartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                CartIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(CartIntent);
                overridePendingTransition(0, 0);

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.gr");
            return !ipAddr.equals("");
        } catch (Exception e) {return false;}
    }


    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}


    private class CollectData extends AsyncTask<String,String,List<ProductClass>> {

        private List<ProductClass> PRODUCTS;
        private String API;

        private CategoryClass c;
        private Adaptery adp;

        private String TITLE;
        private String DESC;
        private String BRAND;


        /**
         * Category
         **/
        CollectData(String API,
                    String TITLE,
                    String DESC,
                    String BRAND,
                    List<ProductClass> PRODUCTS) {
            this.API = API;
            this.PRODUCTS = PRODUCTS;

            CategorySelector = TITLE;

            this.TITLE = TITLE;
            this.DESC = DESC;
            this.BRAND = BRAND;
        }

        /**
         * Solo
         **/
        CollectData(String API, String BRAND, List<ProductClass> PRODUCTS) {
            this.API = API;
            this.PRODUCTS = PRODUCTS;
            this.BRAND = BRAND;
            this.TITLE = "NULL";
            this.DESC = "NULL";
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            c = new CategoryClass();
            c.setCategory_title(TITLE);
            c.setCategory_desc(DESC);
            c.setCategory_brand(BRAND);

            if (!c.getCategory_title().equals("NULL")) {
                category_list.add(c);
            } else {
                c = new CategoryClass(CategoryClass.NO_TITLE_TYPE);
                c.setCategory_title(CategorySelector);
                c.setCategory_brand(BRAND);
                category_list.add(c);
            }

            if(BRAND.equals("Αξεσουάρ & Υγιεινή Ζώων")){
                LinearLayout categoryContainer = findViewById(R.id.CategoryContainer);
                for(int pos = 0; pos < category_list.size(); pos++){
                    CategoryClass clas = category_list.get(pos);
                    if(clas.type == clas.DEFAULT_TYPE){
                        LinearLayout l = new LinearLayout(getBaseContext());
                        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                        l.setLayoutParams(linearLayoutParams);
                        l.setOrientation(LinearLayout.VERTICAL);
                        l.setGravity(Gravity.CENTER);
                        l.setPadding(dpToPx(15), 0, dpToPx(15), 0);
                        TextView t = new TextView(getBaseContext());
                        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        t.setLayoutParams(textViewParams);
                        t.setText(clas.getCategory_title());
                        t.setTextSize(12);
                        t.setTextColor(Color.parseColor("#696969"));
                        t.setTypeface(null, android.graphics.Typeface.BOLD);
                        View h = new View(getBaseContext());
                        LinearLayout.LayoutParams hv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dpToPx(3));
                        hv.gravity = Gravity.BOTTOM;
                        h.setLayoutParams(hv);
                        h.setBackgroundColor(Color.BLACK);
                        h.setVisibility(View.GONE);
                        l.addView(t);
                        l.addView(h);
                        categoryContainer.addView(l);
                        int p = pos;
                        l.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                parent_recycler_view.post(() -> RecyclerViewManager.scrollToPositionWithOffset(p , 0));
                            }
                        });
                    }
                }

                for(int j=0; j<d.getMenu().size(); j++){
                    MenuItem item = d.getMenu().getItem(j);
                    if(item != null){
                        if(item.getTitle() != null){
                            for(int pos = 0; pos < category_list.size(); pos++){
                                CategoryClass clas = category_list.get(pos);
                                if(clas.type == clas.DEFAULT_TYPE){
                                    if(item.getTitle().equals(clas.getCategory_title())){
                                        int p = pos;
                                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                                drawer.closeDrawer(Gravity.LEFT);
                                                parent_recycler_view.post(() -> RecyclerViewManager.scrollToPositionWithOffset(p, 0));
                                                return false;
                                            }
                                        });
                                    }
                                }else if(clas.type == clas.NO_TITLE_TYPE) {
                                    switch (item.getTitle().toString()) {
                                        case "Σκύλος":
                                            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                                    drawer.closeDrawer(Gravity.LEFT);
                                                    parent_recycler_view.post(() -> RecyclerViewManager.scrollToPositionWithOffset(category_list.size() - 3, 0));
                                                    return false;
                                                }
                                            });
                                            break;
                                        case "Γάτα":
                                            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                                    drawer.closeDrawer(Gravity.LEFT);
                                                    parent_recycler_view.post(() -> RecyclerViewManager.scrollToPositionWithOffset(category_list.size() - 1, 0));
                                                    return false;
                                                }
                                            });
                                            break;
                                        default:
                                            if(item.getTitle().equals(clas.getCategory_brand())){
                                                int p = pos;
                                                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                    @Override
                                                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                                                        drawer.closeDrawer(Gravity.LEFT);
                                                        parent_recycler_view.post(() -> RecyclerViewManager.scrollToPositionWithOffset(p, 0));
                                                        return false;
                                                    }
                                                });
                                            }
                                            break;
                                    }
                                }
                            }
                            Log.d("MENU", item.getTitle().toString());
                        }
                    }
                }
            }
        }

        List<ProductClass> productList = new ArrayList<>();
        @Override
        protected List<ProductClass> doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(API)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data.length() == 0) return productList;
                        c.setCategory_brand(BRAND);
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
                            productList.add(model);
                        }
                    }
                }
                response.close();
            } catch (Exception e) {
                // Handle exception
            }
            return productList;
        }

        @Override
        protected void onPostExecute(List<ProductClass> productList) {
            super.onPostExecute(productList);
            try {
                if(!productList.isEmpty()){
                    PRODUCTS.addAll(productList);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) PRODUCTS.sort(Comparator.comparingDouble(product -> Double.parseDouble(product.getPrice().replace(" €", ""))));
                    else {
                        Collections.sort(PRODUCTS, new Comparator<ProductClass>() {
                            @Override
                            public int compare(ProductClass p1, ProductClass p2) {
                                double price1 = Double.parseDouble(p1.getPrice().replace(" €", ""));
                                double price2 = Double.parseDouble(p2.getPrice().replace(" €", ""));
                                return Double.compare(price1, price2);
                            }
                        });
                    }
                    adp = new Adaptery(getBaseContext(), PRODUCTS, ActivityType.MAIN_ACTIVITY);
                    adp.setHasStableIds(true);
                    adp.setOnClickListener((position, model) -> {
                        Intent intent = new Intent(MainActivity.this, ProductView.class);
                        intent.putExtra("PRODUCT_OBJ", model);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    });
                    c.setCategory_holder(adp);
                    NotifyAdapter(c);
                } else c.setCategory_holder(null);
            } catch (Exception e) {
                NotifyAdapter(c);
                sss(e.toString());
            }
        }

    }

    private void NotifyAdapter(CategoryClass c) {
        switch (c.getCategory_brand()) {
            case "Τρόφιμα": adapter.notifyItemChanged(1); break;
            case "Καλάθι του Νοικοκυριού": adapter.notifyItemChanged(1); break;
            case "Γαλακτοκομικά & Τυριά": adapter.notifyItemChanged(2); break;
            case "Χυμοί, Σνακς & Κάβα": adapter.notifyItemChanged(3); break;
            case "Προσωπική φροντίδα": adapter.notifyItemChanged(4); break;
            case "Οικιακή φροντίδα": adapter.notifyItemChanged(5); break;
            case "Παιδικά & Βρεφικά": adapter.notifyItemChanged(6); break;
            case "Διάφορα": adapter.notifyItemChanged(7); break;
            default: break;
        }
    }
    private void sss(String e){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Πρόβλημα Async. Report Bug")
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

