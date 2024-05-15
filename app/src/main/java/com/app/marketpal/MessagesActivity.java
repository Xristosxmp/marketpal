package com.app.marketpal;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MessagesActivity extends AppCompatActivity {

    private LinearLayout c;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        if (Build.VERSION.SDK_INT > 26) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if(Build.VERSION.SDK_INT > 21){
            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        c = findViewById(R.id.messages_container);
        //CreateView("this is a title of the announchment" , "this is the body of the message");

            GetAnnouncement();

    }

    private void GetAnnouncement() {


    }
    private void CreateView(String title, String message){


        LinearLayout.LayoutParams P = new LinearLayout.LayoutParams(
                dpToPx(20),
                dpToPx(20));
        P.setMargins(dpToPx(10), 0, 0, 0);

        LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setOrientation(LinearLayout.HORIZONTAL);

        ImageView m = new ImageView(this);
        m.setLayoutParams(P);
        m.setImageResource(R.drawable.exlamantion_mark);


        P = new LinearLayout.LayoutParams(dpToPx(150),dpToPx(20));
        P.setMargins(dpToPx(10), 0, 0, 0);
        TextView t = new TextView(this);
        t.setTypeface(null, Typeface.BOLD);
        t.setLayoutParams(P);
        t.setText(title);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setHorizontallyScrolling(false);
        t.setMaxLines(1);

        int borderWidth = 2; // Set the desired border width in pixels
        int borderColor = Color.BLACK; // Set the desired border color
        Drawable borderDrawable = createBorderDrawable(borderWidth, borderColor);
        t.setBackground(borderDrawable);

        l.addView(m);
        l.addView(t);
        c.addView(l);

        P = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        P.setMargins(dpToPx(40), 0, dpToPx(20), 0);
        TextView tt = new TextView(this);
        tt.setTypeface(null, Typeface.BOLD);
        tt.setLayoutParams(P);
        tt.setText(message);
        tt.setTextColor(Color.parseColor("#76757a"));
        tt.setEllipsize(TextUtils.TruncateAt.END);
        tt.setHorizontallyScrolling(false);
        tt.setMaxLines(1);
        tt.setBackground(borderDrawable);

        c.addView(tt);

    }

    private Drawable createBorderDrawable(int borderWidth, int borderColor) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setColor(Color.TRANSPARENT); // Set background color
        borderDrawable.setStroke(borderWidth, borderColor); // Set border width and color
        return borderDrawable;
    }
    private int dpToPx(int dp) {return (int) (dp * Resources.getSystem().getDisplayMetrics().density);}

}
