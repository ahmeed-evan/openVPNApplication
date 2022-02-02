package com.willdev.openvpn.fromanother.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.willdev.openvpn.R;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.utils.Config;
import com.willdev.openvpn.view.MainActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    private Boolean isCancelled = false;
    private Method method;
    private ProgressBar progressBar;
    private String id = "", type = "", status_type = "", title = "";

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        method = new Method(SplashScreen.this);
        method.login();
        method.forceRTLIfSupported();
        String them = method.pref.getString(method.them_setting, "system");
        assert them != null;
        switch (them) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }


        setContentView(R.layout.activity_splace_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?freeServers").build();
                            Response response = okHttpClient.newCall(request).execute();
                            WebAPI.FREE_SERVERS = response.body().string();

                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?proServers").build();
                            response = okHttpClient.newCall(request).execute();
                            WebAPI.PREMIUM_SERVERS = response.body().string();

                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?admob").build();
                            response = okHttpClient.newCall(request).execute();
                            String body = response.body().string();
                            try {
                                JSONArray jsonArray = new JSONArray(body);
                                for (int i=0; i < jsonArray.length();i++){
                                    JSONObject object = (JSONObject) jsonArray.get(0);
                                    WebAPI.ADMOB_ID = object.getString("admobID");
                                    WebAPI.ADMOB_BANNER = object.getString("bannerID");
                                    WebAPI.ADMOB_INTERSTITIAL = object.getString("interstitialID");
                                    WebAPI.ADMOB_NATIVE = object.getString("nativeID");
                                    WebAPI.ADS_TYPE = object.getString("adType");
                                    WebAPI.ADMOB_REWARD_ID = object.getString("rewardID");
                                }
                                try {
                                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                    Bundle bundle = applicationInfo.metaData;
                                    applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID",WebAPI.ADMOB_ID);
                                    String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                    Log.d("AppID","The saved id is "+WebAPI.ADMOB_ID);
                                    Log.d("AppID","The saved id is "+apiKey);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            Log.v("Kabila",e.toString());
                            e.printStackTrace();
                        }
                    }
                }).start();
                try {
                    Log.v("SERVER_API",WebAPI.FREE_SERVERS);
                    Thread.sleep(3000);
                    Log.v("SERVER_API","after "+WebAPI.FREE_SERVERS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },1000);





        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        changeStatusBarColor();

        progressBar = findViewById(R.id.progressBar_splash_screen);
        progressBar.setVisibility(View.GONE);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
            assert type != null;
            if (type.equals("single_status")) {
                status_type = getIntent().getStringExtra("status_type");
            }
            if (type.equals("category") || type.equals("single_status")) {
                title = getIntent().getStringExtra("title");
            }
        }

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("method_name", "fetch_onesignal_id");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String success = object.getString("success");
                        String id = object.getString("onesignal_app_id");

                        if (success.equals("1")) {
                            Config.ONESIGNAL_APP_ID = id;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                splashScreen();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                splashScreen();
            }

        });
    }

    public void splashScreen() {

        if (method.isNetworkAvailable()) {


            new Handler().postDelayed(() -> {

                if (!isCancelled) {
                    switch (type) {
                        case "payment_withdraw":
                            call_Activity();
                            break;
                        case "account_verification":

                            break;
                        case "account_status":

                            finishAffinity();
                            break;
                        default:
                            if (method.isLogin()) {
                                login();
                            } else {
                                if (method.pref.getBoolean(method.is_verification, false)) {
                                    startActivity(new Intent(SplashScreen.this, Verification.class));
                                    finishAffinity();
                                } else {
                                    if (method.pref.getBoolean(method.show_login, true)) {
                                        method.editor.putBoolean(method.show_login, false);
                                        method.editor.commit();
                                        Intent i = new Intent(SplashScreen.this, Login.class);
                                        startActivity(i);
                                        finishAffinity();
                                    } else {
                                        call_Activity();
                                    }
                                }
                            }
                            break;
                    }
                }

            }, SPLASH_TIME_OUT);

        } else {
            call_Activity();
        }

    }


    public void login() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("user_id", method.userId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                String loginType = method.getLoginType();

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("-2")) {
                            method.suspend(message);
                        } else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String success = object.getString("success");
                        String msg = object.getString("msg");

                        if (success.equals("1")) {

                            OneSignal.sendTag("user_id", method.userId());
                            //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                            //status.getPermissionStatus().getEnabled();
                            OneSignal.sendTag("player_id", OneSignal.getDeviceState().getUserId());

                            if (loginType.equals("google")) {
                                if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                    call_Activity();
                                } else {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();
                                }
                            } else if (loginType.equals("facebook")) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                                if (isLoggedIn) {
                                    call_Activity();
                                } else {

                                    LoginManager.getInstance().logOut();

                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();

                                }

                            } else {
                                call_Activity();
                            }
                        } else {
                            OneSignal.sendTag("user_id", method.userId());

                            if (loginType.equals("google")) {

                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(SplashScreen.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });


                            } else if (loginType.equals("facebook")) {
                                LoginManager.getInstance().logOut();
                            }

                            method.editor.putBoolean(method.pref_login, false);
                            method.editor.commit();
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finishAffinity();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void call_Activity() {
        startActivity(new Intent(SplashScreen.this, MainActivity.class)
                .putExtra("type", type)
                .putExtra("id", id)
                .putExtra("status_type", status_type)
                .putExtra("title", title));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}



