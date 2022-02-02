package com.willdev.openvpn.fromanother.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.willdev.openvpn.R;

import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.fromanother.util.util.PrefManager;
import com.google.android.material.textview.MaterialTextView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class WelcomeActivity extends AppCompatActivity {

    private Method method;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private RelativeLayout rel_next;
    private ImageView imageView_next;
    private MaterialTextView textView_skip, textView_next;
    private PrefManager prefManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (!prefManager.isWelcome()) {
            if (!prefManager.isLanguage()) {
                startActivity(new Intent(WelcomeActivity.this, SplashScreen.class));
                finish();
            } else {
                launchHomeScreen();
            }
        }


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        method = new Method(WelcomeActivity.this);
        method.forceRTLIfSupported();

        viewPager = findViewById(R.id.view_pager);
        textView_skip = findViewById(R.id.textView_skip);
        textView_next = findViewById(R.id.textView_next);
        imageView_next = findViewById(R.id.imageView_next);
        rel_next = findViewById(R.id.rel_next);

        textView_next.setVisibility(View.GONE);


        layouts = new int[]{
                R.layout.welcome_slide_one,
                R.layout.welcome_slide_two,
                R.layout.welcome_slide_three};


        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        textView_skip.setOnClickListener(v -> launchHomeScreen());

        rel_next.setOnClickListener(v -> {

            int current = getItem(+1);
            if (current < layouts.length) {

                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstWelcome(false);
        prefManager.setFirstLanguage(false);
        startActivity(new Intent(WelcomeActivity.this, SplashScreen.class)
                .putExtra("type", "welcome"));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {


            if (position == layouts.length - 1) {

                textView_skip.setVisibility(View.GONE);
                imageView_next.setVisibility(View.GONE);
                textView_next.setVisibility(View.VISIBLE);
            } else {

                textView_skip.setVisibility(View.VISIBLE);
                imageView_next.setVisibility(View.VISIBLE);
                textView_next.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}