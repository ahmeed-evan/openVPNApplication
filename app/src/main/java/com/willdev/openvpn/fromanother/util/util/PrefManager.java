package com.willdev.openvpn.fromanother.util.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "video";

    private static final String IS_WELCOME = "is_welcome";
    private static final String IS_LANGUAGE = "is_language";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstWelcome(boolean isFirstTime) {
        editor.putBoolean(IS_WELCOME, isFirstTime);
        editor.commit();
    }

    public void setFirstLanguage(boolean isFirstTime) {
        editor.putBoolean(IS_LANGUAGE, isFirstTime);
        editor.commit();
    }

    public boolean isWelcome() {
        return pref.getBoolean(IS_WELCOME, true);
    }

    public boolean isLanguage() {
        return pref.getBoolean(IS_LANGUAGE, true);
    }

}
