package com.willdev.openvpn.fromanother.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.fragment.EarnPointAdapter;
import com.willdev.openvpn.fromanother.item.EarnPointList;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EarnPoint extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private MaterialTextView textView_noData;
    private RecyclerView recyclerView;
    private List<EarnPointList> earnPointLists;
    private EarnPointAdapter earnPointAdapter;
    private RelativeLayout relMain;
    private LinearLayout linearLayout;
    private LayoutAnimationController animation;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_point);

        method = new Method(EarnPoint.this);
        method.forceRTLIfSupported();

        earnPointLists = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(EarnPoint.this, resId);

        toolbar = findViewById(R.id.toolbar_ep);
        toolbar.setTitle(getResources().getString(R.string.earn_point));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.linearLayout_ep);
        method.adView(linearLayout);

        relMain = findViewById(R.id.rel_main_ep);
        progressBar = findViewById(R.id.progressbar_ep);
        textView_noData = findViewById(R.id.textView_ep);
        recyclerView = findViewById(R.id.recyclerView_ep);

        progressBar.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);
        relMain.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EarnPoint.this);
        recyclerView.setLayoutManager(layoutManager);

        if (method.isNetworkAvailable()) {
            Point();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void Point() {

        earnPointLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EarnPoint.this));
        jsObj.addProperty("method_name", "points_details");
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
                        method.alertBox(message);

                        textView_noData.setVisibility(View.VISIBLE);

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String title = object.getString("title");
                            String point = object.getString("point");

                            earnPointLists.add(new EarnPointList(title, point));
                        }

                        if (earnPointLists.size() == 0) {
                            textView_noData.setVisibility(View.VISIBLE);
                        } else {
                            textView_noData.setVisibility(View.GONE);
                            earnPointAdapter = new EarnPointAdapter(EarnPoint.this, earnPointLists);
                            recyclerView.setAdapter(earnPointAdapter);
                            recyclerView.setLayoutAnimation(animation);
                        }

                        relMain.setVisibility(View.VISIBLE);

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
                textView_noData.setVisibility(View.VISIBLE);
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
