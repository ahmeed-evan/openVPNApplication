package com.willdev.openvpn.fromanother.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AboutUs extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private WebView webView;
    private ImageView imageView;
    private LinearLayout llMain;
    private MaterialCardView cardViewEmail;
    private MaterialCardView cardViewWebsite;
    private MaterialCardView cardViewPhone;
    private MaterialTextView textViewNoData;
    private MaterialTextView textViewAppName;
    private MaterialTextView textViewAppVersion;
    private MaterialTextView textViewAppAuthor;
    private MaterialTextView textViewAppContact;
    private MaterialTextView textViewAppEmail;
    private MaterialTextView textViewAppWebsite;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        method = new Method(AboutUs.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_about_us);
        toolbar.setTitle(getResources().getString(R.string.about_us));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        llMain = findViewById(R.id.ll_main_about_us);
        progressBar = findViewById(R.id.progressBar_about_us);
        imageView = findViewById(R.id.app_logo_about_us);
        webView = findViewById(R.id.webView_about_us);
        cardViewEmail = findViewById(R.id.cardView_email_about);
        cardViewWebsite = findViewById(R.id.cardView_website_about);
        cardViewPhone = findViewById(R.id.cardView_phone_about);
        textViewNoData = findViewById(R.id.textView_noData_about_us);
        textViewAppName = findViewById(R.id.textView_app_name_about_us);
        textViewAppVersion = findViewById(R.id.textView_app_version_about_us);
        textViewAppAuthor = findViewById(R.id.textView_app_author_about_us);
        textViewAppContact = findViewById(R.id.textView_app_contact_about_us);
        textViewAppEmail = findViewById(R.id.textView_app_email_about_us);
        textViewAppWebsite = findViewById(R.id.textView_app_website_about_us);

        progressBar.setVisibility(View.GONE);
        textViewNoData.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_about_us);
        method.adView(linearLayout);

        if (method.isNetworkAvailable()) {
            about();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void about() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AboutUs.this));
        jsObj.addProperty("method_name", "app_about");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @SuppressLint("SetJavaScriptEnabled")
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
                        textViewNoData.setVisibility(View.VISIBLE);

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String success = object.getString("success");

                        if (success.equals("1")) {

                            String app_name = object.getString("app_name");
                            String app_logo = object.getString("app_logo");
                            String app_version = object.getString("app_version");
                            String app_author = object.getString("app_author");
                            String app_contact = object.getString("app_contact");
                            String app_email = object.getString("app_email");
                            String app_website = object.getString("app_website");
                            String app_description = object.getString("app_description");

                            textViewAppName.setText(app_name);

                            Glide.with(AboutUs.this).load(app_logo)
                                    .placeholder(R.drawable.logo)
                                    .into(imageView);

                            textViewAppVersion.setText(app_version);
                            textViewAppAuthor.setText(app_author);
                            textViewAppContact.setText(app_contact);
                            textViewAppEmail.setText(app_email);
                            textViewAppWebsite.setText(app_website);

                            webView.setBackgroundColor(Color.TRANSPARENT);
                            webView.setFocusableInTouchMode(false);
                            webView.setFocusable(false);
                            webView.getSettings().setDefaultTextEncodingName("UTF-8");
                            webView.getSettings().setJavaScriptEnabled(true);
                            String mimeType = "text/html";
                            String encoding = "utf-8";

                            String text = "<html><head>"
                                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/opensans_semi_bold.TTF\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}"
                                    + "a {color:" + method.webViewLink() + "text-decoration:none}"
                                    + "</style></head>"
                                    + "<body>"
                                    + app_description
                                    + "</body></html>";

                            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                            llMain.setVisibility(View.VISIBLE);

                            cardViewEmail.setOnClickListener(v -> {
                                try {
                                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{app_email});
                                    emailIntent.setData(Uri.parse("mailto:"));
                                    startActivity(emailIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }
                            });

                            cardViewWebsite.setOnClickListener(v -> {
                                try {
                                    String url = app_website;
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "http://" + url;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                } catch (Exception e) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }

                            });

                            cardViewPhone.setOnClickListener(v -> {
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + app_contact));
                                    startActivity(callIntent);
                                } catch (Exception e) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }
                            });

                        } else {
                            textViewNoData.setVisibility(View.VISIBLE);
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
                textViewNoData.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
