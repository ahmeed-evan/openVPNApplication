package com.willdev.openvpn.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.willdev.openvpn.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;


public class IntroActivity extends MaterialIntroActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", true)) {
            onFinish();
        } else {

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.cmn_white)
                    .buttonsColor(R.color.colorPrimary)
                    .image(R.drawable.ic_user)
                    .title("vpn")
                    .description("vpn")
                    .build());
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.cmn_white)
                    .buttonsColor(R.color.colorPrimary)
                    .image(R.drawable.ic_user)
                    .title("VPN")
                    .description("VPN")
                    .build());
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.cmn_white)
                    .buttonsColor(R.color.colorPrimary)
                    .image(R.drawable.ic_user)
                    .title("VPN")
                    .description("VPN")
                    .build());
        }
    }

    @Override
    public void onFinish() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();
        super.onFinish();
    }
}

