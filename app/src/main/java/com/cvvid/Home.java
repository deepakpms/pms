package com.cvvid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.cvvid.activities.candidate.Signupmenu;
import com.cvvid.adaptors.candidate.HomeAdaptor;
import com.cvvid.apicall.CheckNetwork;
import com.cvvid.common.Signin;

public class Home extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDot;
    private TextView[]dotstv;
    private int[]layouts;
    private Button btnSignin;
    private Button btnSignup;
    private HomeAdaptor homeAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(!CheckNetwork.isInternetAvailable(Home.this))
        {
            TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "Oops! Something went wrong check your network connection.", TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#000000"));
            TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        layoutDot = (LinearLayout) findViewById(R.id.dotLayout);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        layouts = new int[]{R.layout.slider_1, R.layout.slider_2, R.layout.slider_3};
        homeAdaptor = new HomeAdaptor(layouts, getApplicationContext());
        viewPager.setAdapter(homeAdaptor);

        btnSignin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // navigate login activity
                Intent intent = new Intent(Home.this, Signin.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // navigate signup activity
                Intent intent = new Intent(Home.this, Signupmenu.class);
                startActivity(intent);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1){

                }
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);

    }

    private void setDotStatus(int page){
        layoutDot.removeAllViews();
        dotstv = new TextView[layouts.length];
        for(int i=0;i < dotstv.length; i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            layoutDot.addView(dotstv[i]);
        }
        //set current dot active
        if(dotstv.length > 0){
            dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }
    }
}
