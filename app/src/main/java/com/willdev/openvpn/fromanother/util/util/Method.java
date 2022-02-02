package com.willdev.openvpn.fromanother.util.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.willdev.openvpn.R;
import com.willdev.openvpn.database.DatabaseHandler;
import com.willdev.openvpn.fromanother.activity.Login;
import com.willdev.openvpn.fromanother.interfaces.FavouriteIF;
import com.willdev.openvpn.fromanother.interfaces.FullScreen;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.interfaces.VideoAd;
import com.willdev.openvpn.fromanother.service.DownloadIGService;
import com.facebook.login.LoginManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

public class Method {

    private Activity activity;
    public static boolean loginBack = false;
    public static boolean isUpload = true, isDownload = true;
    public boolean personalization_ad = false;
    private OnClick onClick;
    private VideoAd videoAd;
    private FullScreen fullScreen;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String pref_login = "pref_login";
    public String profileId = "profileId";
    public String userImage = "userImage";
    public String loginType = "loginType";
    public String show_login = "show_login";
    public String notification = "notification";
    public String verification_code = "verification_code";
    public String is_verification = "is_verification";
    public String them_setting = "them";

    public String reg_name = "reg_name";
    public String reg_email = "reg_email";
    public String reg_password = "reg_password";
    public String reg_phoneNo = "reg_phoneNo";
    public String reg_reference = "reg_reference";

    public String language_ids = "language_ids";

    private String filename;
    private DatabaseHandler db;

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, VideoAd videoAd) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
        this.videoAd = videoAd;
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, OnClick onClick) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, OnClick onClick, VideoAd videoAd, FullScreen fullScreen) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.onClick = onClick;
        this.videoAd = videoAd;
        this.fullScreen = fullScreen;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    public void login() {
        String firstTime = "firstTime";
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }


    public boolean isLogin() {
        return pref.getBoolean(pref_login, false);
    }

    public String userId() {
        return pref.getString(profileId, "");
    }


    public String getLoginType() {
        return pref.getString(loginType, "");
    }


    public String getLanguageIds() {
        return pref.getString(language_ids, "");
    }


    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }


    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }


    public boolean isAppInstalledWhatsapp() {
        String packageName = "com.whatsapp";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledInstagram() {
        String packageName = "com.instagram.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledFacebook() {
        String packageName = "com.facebook.katana";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledFbMessenger() {
        String packageName = "com.facebook.orca";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledTwitter() {
        String packageName = "com.twitter.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }


    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnHeight = point.y;
        return columnHeight;
    }



    public void VideoAdDialog(String type, String value) {

        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isRewarded_video_ads()) {
                if (value.equals("view_ad")) {
                    showAdDialog(type);
                } else {
                    Constant.REWARD_VIDEO_AD_COUNT = Constant.REWARD_VIDEO_AD_COUNT + 1;
                    if (Constant.REWARD_VIDEO_AD_COUNT == Constant.REWARD_VIDEO_AD_COUNT_SHOW) {
                        Constant.REWARD_VIDEO_AD_COUNT = 0;
                        showAdDialog(type);
                    } else {
                        callVideoAdData(type);
                    }
                }
            } else {
                callVideoAdData(type);
            }
        } else {
            callVideoAdData(type);
        }

    }

    private void showAdDialog(String type) {
        Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_ad);
        if (isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        MaterialButton buttonYes = dialog.findViewById(R.id.button_yes_viewAd);
        MaterialButton buttonNo = dialog.findViewById(R.id.button_no_viewAd);

        buttonYes.setOnClickListener(v -> {
            dialog.dismiss();
            showVideoAd(type);
        });

        buttonNo.setOnClickListener(v -> {
            dialog.dismiss();
            if (Constant.aboutUsList.isInterstitial_ad()) {
                skipVideoAd(type);
            } else {
                callVideoAdData(type);
            }
        });

        dialog.show();
    }

    private void showVideoAd(String type) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
/*
        RewardedAd rewardedAd = new RewardedAd(activity, Constant.aboutUsList.getRewarded_video_ads_id()) {
            @Override
            public void setServerSideVerificationOptions(@Nullable ServerSideVerificationOptions serverSideVerificationOptions) {

            }

            @Override
            public void setOnAdMetadataChangedListener(@Nullable OnAdMetadataChangedListener onAdMetadataChangedListener) {

            }

            @Nullable
            @Override
            public OnAdMetadataChangedListener getOnAdMetadataChangedListener() {
                return null;
            }

            @NonNull
            @Override
            public Bundle getAdMetadata() {
                return null;
            }

            @Override
            public void show(@NonNull Activity activity, @NonNull OnUserEarnedRewardListener onUserEarnedRewardListener) {

            }

            @NonNull
            @Override
            public RewardItem getRewardItem() {
                return null;
            }

            @NonNull
            @Override
            public ResponseInfo getResponseInfo() {
                return null;
            }

            @Override
            public void setOnPaidEventListener(@Nullable OnPaidEventListener onPaidEventListener) {

            }

            @Nullable
            @Override
            public OnPaidEventListener getOnPaidEventListener() {
                return null;
            }

            @Override
            public void setFullScreenContentCallback(@Nullable FullScreenContentCallback fullScreenContentCallback) {

            }

            @Nullable
            @Override
            public FullScreenContentCallback getFullScreenContentCallback() {
                return null;
            }

            @NonNull
            @Override
            public String getAdUnitId() {
                return null;
            }

            @Override
            public void setImmersiveMode(boolean b) {

            }
        };
        AdRequest adRequest;
        Bundle extras = new Bundle();
        if (!personalization_ad) {
            extras.putString("npa", "1");
        }
        extras.putBoolean("_noRefresh", true);
        adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened() {
                    }

                    @Override
                    public void onRewardedAdClosed() {
                        callVideoAdData(type);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    }

                };
                rewardedAd.show(activity, adCallback);
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                callVideoAdData(type);
                progressDialog.dismiss();
            }
        };
        rewardedAd.loadAd(adRequest, adLoadCallback);
*/
    }

    private void skipVideoAd(String type) {
/*
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (Constant.aboutUsList.getInterstitial_ad_type().equals("admob")) {
            final InterstitialAd interstitialAd = new InterstitialAd(activity) {
                @NonNull
                @Override
                public String getAdUnitId() {
                    return null;
                }

                @Override
                public void show(@NonNull Activity activity) {

                }

                @Override
                public void setFullScreenContentCallback(@Nullable FullScreenContentCallback fullScreenContentCallback) {

                }

                @Nullable
                @Override
                public FullScreenContentCallback getFullScreenContentCallback() {
                    return null;
                }

                @Override
                public void setImmersiveMode(boolean b) {

                }

                @NonNull
                @Override
                public ResponseInfo getResponseInfo() {
                    return null;
                }

                @Override
                public void setOnPaidEventListener(@Nullable OnPaidEventListener onPaidEventListener) {

                }

                @Nullable
                @Override
                public OnPaidEventListener getOnPaidEventListener() {
                    return null;
                }
            };
            AdRequest adRequest;
            if (personalization_ad) {
                adRequest = new AdRequest.Builder()
                        .build();
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            }
            interstitialAd.setAdUnitId(Constant.aboutUsList.getInterstitial_ad_id());
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    interstitialAd.show();
                    progressDialog.dismiss();
                }

                public void onAdClosed() {
                    callVideoAdData(type);
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    Log.d("admob_error", String.valueOf(adError));
                    callVideoAdData(type);
                    super.onAdFailedToLoad(adError);
                    progressDialog.dismiss();
                }

            });
        } else {
            com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, Constant.aboutUsList.getInterstitial_ad_id());
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    Log.e("fb_ad", "Interstitial ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    progressDialog.dismiss();
                    callVideoAdData(type);
                    Log.e("fb_ad", "Interstitial ad dismissed.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    callVideoAdData(type);
                    progressDialog.dismiss();
                    Log.e("fb_ad", "Interstitial ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("fb_ad", "Interstitial ad is loaded and ready to be displayed!");
                    progressDialog.dismiss();
                    interstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("fb_ad", "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("fb_ad", "Interstitial ad impression logged!");
                }
            };

            com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig().
                    withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
            interstitialAd.loadAd(loadAdConfig);
        }
*/
    }


    private void callVideoAdData(String video_ad_type) {
        videoAd.videoAdClick(video_ad_type);
    }


    public void onClickData(int position, String title, String type, String statusType, String id, String tag) {
/*
        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (Constant.aboutUsList != null) {

            if (Constant.aboutUsList.isInterstitial_ad()) {

                Constant.AD_COUNT = Constant.AD_COUNT + 1;
                if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                    Constant.AD_COUNT = 0;

                    if (Constant.aboutUsList.getInterstitial_ad_type().equals("admob")) {

                        final InterstitialAd interstitialAd = new InterstitialAd(activity);
                        AdRequest adRequest;
                        if (personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        interstitialAd.setAdUnitId(Constant.aboutUsList.getInterstitial_ad_id());
                        interstitialAd.loadAd(adRequest);
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                interstitialAd.show();
                                progressDialog.dismiss();
                            }

                            public void onAdClosed() {
                                onClick.position(position, title, type, statusType, id, tag);
                                super.onAdClosed();
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                Log.d("admob_error", String.valueOf(adError));
                                onClick.position(position, title, type, statusType, id, tag);
                                super.onAdFailedToLoad(adError);
                                progressDialog.dismiss();
                            }

                        });

                    } else {

                        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, Constant.aboutUsList.getInterstitial_ad_id());
                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                Log.e("fb_ad", "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                progressDialog.dismiss();
                                onClick.position(position, title, type, statusType, id, tag);
                                Log.e("fb_ad", "Interstitial ad dismissed.");
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                onClick.position(position, title, type, statusType, id, tag);
                                progressDialog.dismiss();
                                Log.e("fb_ad", "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                Log.d("fb_ad", "Interstitial ad is loaded and ready to be displayed!");
                                progressDialog.dismiss();
                                interstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                Log.d("fb_ad", "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                Log.d("fb_ad", "Interstitial ad impression logged!");
                            }
                        };

                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig().
                                withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
                        interstitialAd.loadAd(loadAdConfig);

                    }

                } else {
                    progressDialog.dismiss();
                    onClick.position(position, title, type, statusType, id, tag);
                }
            } else {
                progressDialog.dismiss();
                onClick.position(position, title, type, statusType, id, tag);
            }

        } else {
            progressDialog.dismiss();
            onClick.position(position, title, type, statusType, id, tag);
        }
*/
    }



    public void adView(LinearLayout linearLayout) {

        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isBanner_ad()) {
                if (Constant.aboutUsList.getBanner_ad_type().equals("admob")) {
                    if (personalization_ad) {
                        showPersonalizedAds(linearLayout);
                    } else {
                        showNonPersonalizedAds(linearLayout);
                    }
                } else {
                    FbBannerAd(linearLayout);
                }
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }

    }

    public void FbBannerAd(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isBanner_ad()) {
                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, Constant.aboutUsList.getBanner_ad_id(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                linearLayout.addView(adView);
                adView.loadAd();
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void showPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isBanner_ad()) {
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                adView.setAdUnitId(Constant.aboutUsList.getBanner_ad_id());
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void showNonPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isBanner_ad()) {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
                adView.setAdUnitId(Constant.aboutUsList.getBanner_ad_id());
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }



    public void ShowFullScreen(boolean isFullScreen) {
        fullScreen.fullscreen(isFullScreen);
    }



    public void download(String id, String status_name, String category, String
            status_image_s, String status_image_b,
                         String video_uri, String layout_type, String status_type, String
                                 watermark_image, String watermark_on_off) {

        String filePath = null;


        if (status_type.equals("image") || status_type.equals("gif")) {

            if (status_type.equals("image")) {
                filename = "filename-" + id + ".jpg";
            } else {
                filename = "filename-" + id + ".gif";
            }

            File root_file = new File(Constant.image_path);
            if (!root_file.exists()) {
                root_file.mkdirs();
            }

            File file = new File(Constant.image_path, filename);
            filePath = file.toString();

            Intent serviceIntent = new Intent(activity, DownloadIGService.class);
            serviceIntent.setAction(DownloadIGService.ACTION_START);
            serviceIntent.putExtra("id", id);
            serviceIntent.putExtra("downloadUrl", status_image_b);
            serviceIntent.putExtra("file_path", root_file.toString());
            serviceIntent.putExtra("file_name", filename);
            serviceIntent.putExtra("status_type", status_type);
            activity.startService(serviceIntent);

        }

        new DownloadImage().execute(status_image_b, id, status_name, category, layout_type, status_type, filePath);

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<String, String, String> {

        private String filePath = null;
        private String iconsStoragePath;
        private String id, status_name, category, layout_type, status_type, file_path;

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                id = params[1];
                status_name = params[2];
                category = params[3];
                layout_type = params[4];
                status_type = params[5];
                file_path = params[6];

                if (status_type.equals("video")) {

                    iconsStoragePath = Constant.video_path;

                    File sdIconStorageDir = new File(iconsStoragePath);

                    if (!sdIconStorageDir.exists()) {
                        sdIconStorageDir.mkdirs();
                    }

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmapDownload = BitmapFactory.decodeStream(input);

                    String fname = "Image-" + id;
                    filePath = iconsStoragePath + fname + ".jpg";

                    File file = new File(iconsStoragePath, filePath);
                    if (file.exists()) {
                        Log.d("file_exists", "file_exists");
                    } else {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                            bitmapDownload.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        } catch (IOException e) {
                            Log.w("TAG", "Error saving image file: " + e.getMessage());
                        }
                    }

                }

            } catch (IOException e) {
                Log.w("error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (status_type.equals("image")) {
                Toast.makeText(activity, activity.getResources().getString(R.string.download), Toast.LENGTH_SHORT).show();
            }


            super.onPostExecute(s);
        }

    }




    public void addToFav(String id, String userId, String statusType, FavouriteIF favouriteIF) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("post_id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", statusType);
        jsObj.addProperty("method_name", "status_favourite");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("-2")) {
                            suspend(message);
                        } else {
                            alertBox(message);
                        }

                        favouriteIF.isFavourite("", "");

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String success = object.getString("success");
                        String msg = object.getString("msg");
                        if (success.equals("1")) {
                            String is_favourite = object.getString("is_favourite");
                            favouriteIF.isFavourite(is_favourite, msg);
                        } else {
                            favouriteIF.isFavourite("", msg);
                        }

                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    favouriteIF.isFavourite("", activity.getResources().getString(R.string.failed_try_again));
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                favouriteIF.isFavourite("", activity.getResources().getString(R.string.failed_try_again));
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }


    public void alertBox(String message) {

        if (!activity.isFinishing()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                    (arg0, arg1) -> {
                        if (message.equals(activity.getResources().getString(R.string.you_have_not_login))) {
                            activity.startActivity(new Intent(activity, Login.class));
                            activity.finishAffinity();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }


    public void suspend(String message) {

        if (isLogin()) {

            String type_login = pref.getString(loginType, "");
            if (type_login.equals("google")) {


                GoogleSignInClient mGoogleSignInClient;

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();


                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            } else if (type_login.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }

            editor.putBoolean(pref_login, false);
            editor.commit();
            Events.Login loginNotify = new Events.Login("");
            GlobalBus.getBus().post(loginNotify);
        }

        if (!activity.isFinishing()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                    (arg0, arg1) -> {

                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }


    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:

                return false;
            case Configuration.UI_MODE_NIGHT_YES:

                return true;
            default:
                return false;
        }
    }

    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webTextDark;
        } else {
            color = Constant.webTextLight;
        }
        return color;
    }

    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webLinkDark;
        } else {
            color = Constant.webLinkLight;
        }
        return color;
    }

    public String imageGalleryToolBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.darkGallery;
        } else {
            color = Constant.lightGallery;
        }
        return color;
    }

    public String imageGalleryProgressBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.progressBarDarkGallery;
        } else {
            color = Constant.progressBarLightGallery;
        }
        return color;
    }

}
