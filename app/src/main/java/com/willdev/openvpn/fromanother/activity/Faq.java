package com.willdev.openvpn.fromanother.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
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


public class Faq extends AppCompatActivity {

    private Method method;
    public MaterialToolbar toolbar;
    private WebView webView;
    private MaterialTextView textViewNoData;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        method = new Method(Faq.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_faq);
        toolbar.setTitle(getResources().getString(R.string.faq));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webView = findViewById(R.id.webView_faq);
        progressBar = findViewById(R.id.progressBar_faq);
        textViewNoData = findViewById(R.id.textView_noData_found_faq);
        linearLayout = findViewById(R.id.linearLayout_faq);

        progressBar.setVisibility(View.GONE);
        textViewNoData.setVisibility(View.GONE);

        method.adView(linearLayout);

        if (method.isNetworkAvailable()) {
            faq();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void faq() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Faq.this));
        jsObj.addProperty("method_name", "app_faq");
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

                            String app_faq = object.getString("app_faq");

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
                                    + app_faq
                                    + "</body></html>";

                            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

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
