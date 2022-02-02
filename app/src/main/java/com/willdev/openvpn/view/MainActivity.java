package com.willdev.openvpn.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.willdev.openvpn.fromanother.activity.AboutUs;
import com.willdev.openvpn.fromanother.activity.ContactUs;
import com.willdev.openvpn.fromanother.activity.EarnPoint;
import com.willdev.openvpn.fromanother.activity.Faq;
import com.willdev.openvpn.fromanother.activity.Login;
import com.willdev.openvpn.fromanother.activity.PrivacyPolice;
import com.willdev.openvpn.fromanother.activity.Spinner;
import com.willdev.openvpn.fromanother.fragment.ProfileFragment;
import com.willdev.openvpn.fromanother.fragment.ReferenceCodeFragment;
import com.willdev.openvpn.fromanother.fragment.RewardPointFragment;
import com.willdev.openvpn.fromanother.fragment.SettingFragment;
import com.willdev.openvpn.fromanother.item.AboutUsList;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.willdev.openvpn.BuildConfig;
import com.willdev.openvpn.SharedPreference;
import com.willdev.openvpn.adapter.ServerListRVAdapter;
import com.willdev.openvpn.interfaces.ChangeServer;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.R;
import com.willdev.openvpn.utils.Config;

import com.facebook.ads.*;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ServerListRVAdapter serverListRVAdapter;
    private DrawerLayout drawer;
    private ChangeServer changeServer;
    private SharedPreference preference;
    private Method method;
    private boolean doubleBackToExitPressedOnce = false;
    public MutableLiveData<Server> currentVipServer = new MutableLiveData<>();
    private BillingProcessor bp;
    private boolean activateServer;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudienceNetworkAds.initialize(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ButterKnife.bind(this);
        preference = new SharedPreference(this);
        currentVipServer.setValue(preference.getVipServer());

        initializeAll();

        method = new Method(MainActivity.this, null, null, null);
        method.forceRTLIfSupported();


        openScreen(fragment, false);

        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }
        bp = new BillingProcessor(this, Config.IAP_LISENCE_KEY, this);
        bp.initialize();

        aboutUs();
    }


    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragment = new MainFragment();
        changeServer = (ChangeServer) fragment;
    }


    public void openCloseDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else {
            drawer.openDrawer(GravityCompat.START, true);
        }
    }


    void openScreen(Fragment fragmentClass, boolean addToBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        try {
            FragmentTransaction transaction = manager.beginTransaction();
            if (addToBackStack) transaction.addToBackStack(fragmentClass.getTag());
            transaction.replace(R.id.container, fragmentClass);
            transaction.commitAllowingStateLoss();
            manager.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {
        if (currentVipServer.getValue() != null) {
            preference.saveVipServer(currentVipServer.getValue());
        }
        super.onStop();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        int idd = item.getItemId();

        switch (idd) {

            case R.id.home:
                openScreen(fragment, false);
                openCloseDrawer();
                return true;

            case R.id.reward:
                if (!method.isLogin()) {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                } else {
                    RewardPointFragment rewardPointFragment_nav = new RewardPointFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "0");
                    rewardPointFragment_nav.setArguments(bundle);

                    openScreen(rewardPointFragment_nav, true);
                }

                openCloseDrawer();

                return true;



            case R.id.reference_code:
                if(!method.isLogin()) {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                } else {
                    openScreen(new ReferenceCodeFragment(), true);
                }
                openCloseDrawer();

                return true;

            case R.id.earn_point:
                if (!method.isLogin()) {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                } else {
                    startActivity(new Intent(MainActivity.this, Spinner.class));
                }
                openCloseDrawer();
                return true;

            case R.id.settings:
                openScreen(new SettingFragment(), true);
                openCloseDrawer();
               return true;

            case R.id.login:
                if (method.isLogin()) {
                    logout();
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finishAffinity();
                }
                openCloseDrawer();

                return true;

            case R.id.contact_us:

                  startActivity(new Intent(MainActivity.this, ContactUs.class));
                openCloseDrawer();

            case R.id.profile:
                if (!method.isLogin()) {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                } else {
                    Bundle bundle_profile = new Bundle();
                    bundle_profile.putString("type", "user");
                    bundle_profile.putString("id", method.userId());
                    ProfileFragment pr = new ProfileFragment();
                    pr.setArguments(bundle_profile);
                    openScreen(pr, true);
                }
                openCloseDrawer();

                return true;
            case R.id.faq:

                    startActivity(new Intent(this, Faq.class));
                openCloseDrawer();

                return true;
            case R.id.earn_poin:
                if (!method.isLogin()) {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                } else {
                    startActivity(new Intent(this, EarnPoint.class));
                }

                openCloseDrawer();

                return true;
            case R.id.share_app:

                    shareApp();

                openCloseDrawer();

                return true;
            case R.id.rate_app:

                   rateApp();

                openCloseDrawer();

                return true;
            case R.id.more_app:

                    moreApp();

                openCloseDrawer();

                return true;
            case R.id.privacy:

                    startActivity(new Intent(this, PrivacyPolice.class));

                openCloseDrawer();

                return true;
            case R.id.about_us:

                    startActivity(new Intent(this, AboutUs.class));

                openCloseDrawer();

                return true;

            default:
                return true;
        }
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplication().getPackageName())));
        }
    }

    private void moreApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_more_app))));
    }

    private void shareApp() {

        try {

            String string = "\n" + getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n" + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, string);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));

        } catch (Exception e) {

        }

    }



    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        checkSubscriptions();
    }

    private void checkSubscriptions() {


        if (bp.isSubscribed(Config.all_month_id) ||
                bp.isSubscribed(Config.all_threemonths_id) ||
                bp.isSubscribed(Config.all_sixmonths_id) ||
                bp.isSubscribed(Config.all_yearly_id)) {

            Config.all_subscription = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_CODE && data !=null)
            {
                activateServer = true;
                Server server = data.getParcelableExtra("server");
                currentVipServer.postValue(server);
            }
        }
    }


    public void logout() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.DialogTitleTextStyle);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.logout_message));
        builder.setPositiveButton(getResources().getString(R.string.logout),
                (arg0, arg1) -> {
                    OneSignal.sendTag("user_id", method.userId());
                    if (method.getLoginType().equals("google")) {

                       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finishAffinity();
                                });
                    } else if (method.getLoginType().equals("facebook")) {
                        LoginManager.getInstance().logOut();
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    } else {
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void aboutUs() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("method_name", "app_settings");
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
                            method.suspend(message);
                        } else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String success = object.getString("success");

                        if (success.equals("1")) {

                            String app_name = object.getString("app_name");
                            String privacy_policy_url = object.getString("privacy_policy_url");
                            String publisher_id = object.getString("publisher_id");
                            String spinner_opt = object.getString("spinner_opt");
                            String banner_ad_type = object.getString("banner_ad_type");
                            String banner_ad_id = object.getString("banner_ad_id");
                            String interstitial_ad_type = object.getString("interstitial_ad_type");
                            String interstitial_ad_id = object.getString("interstitial_ad_id");
                            String interstitial_ad_click = object.getString("interstitial_ad_click");
                            String rewarded_video_ads_id = object.getString("rewarded_video_ads_id");
                            String rewarded_video_click = object.getString("rewarded_video_click");
                            String app_update_status = object.getString("app_update_status");
                            int app_new_version = Integer.parseInt(object.getString("app_new_version"));
                            String app_update_desc = object.getString("app_update_desc");
                            String app_redirect_url = object.getString("app_redirect_url");
                            String cancel_update_status = object.getString("cancel_update_status");
                            boolean banner_ad = Boolean.parseBoolean(object.getString("banner_ad"));
                            boolean interstitial_ad = Boolean.parseBoolean(object.getString("interstitial_ad"));
                            boolean rewarded_video_ads = Boolean.parseBoolean(object.getString("rewarded_video_ads"));

                            Constant.aboutUsList = new AboutUsList(app_name, privacy_policy_url, publisher_id, banner_ad_type, banner_ad_id, interstitial_ad_type, interstitial_ad_id, interstitial_ad_click, rewarded_video_ads_id, rewarded_video_click, spinner_opt,
                                    app_update_status, app_update_desc, app_redirect_url, cancel_update_status, app_new_version, banner_ad, interstitial_ad, rewarded_video_ads);

                            if (Constant.aboutUsList.getApp_update_status().equals("true") && Constant.aboutUsList.getApp_new_version() > BuildConfig.VERSION_CODE) {
                                showUpdateDialog(Constant.aboutUsList.getApp_update_desc(),
                                        Constant.aboutUsList.getApp_redirect_url(),
                                        Constant.aboutUsList.getCancel_update_status());
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void showUpdateDialog(String description, String link, String isCancel) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        MaterialTextView textView_description = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel.equals("true")) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textView_description.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {

                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }



    public boolean isActivateServer()
    {
        return activateServer;
    }
}
