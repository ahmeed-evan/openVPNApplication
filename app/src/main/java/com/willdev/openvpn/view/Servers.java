package com.willdev.openvpn.view;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.willdev.openvpn.R;
import com.willdev.openvpn.adapter.TabAdapter;
import com.willdev.openvpn.api.WebAPI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class Servers extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Toolbar toolbar = findViewById(R.id.toolbarold);
        toolbar.setTitle("Free Servers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());

        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB))
        {
            adapter.addFragment(new FreeServersFragmentAdMob(), "Free Servers");
        }
        else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS))
        {
            adapter.addFragment(new FreeServersFragment(), "Free Servers");
        }
        adapter.addFragment(new VipServersFragment(), "Premium Servers");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                toolbar.setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Servers.super.onBackPressed();
            }
        });
    }
}
