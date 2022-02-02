package com.willdev.openvpn.fromanother.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.willdev.openvpn.R;

import com.willdev.openvpn.fromanother.item.PaymentList;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Events;
import com.willdev.openvpn.fromanother.util.util.GlobalBus;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
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


public class RewardPointClaim extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Method method;
    private MaterialToolbar toolbar;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private MaterialButton buttonSubmit;
    private InputMethodManager imm;
    private LinearLayout linearLayout;
    private List<PaymentList> paymentLists;
    private TextInputEditText editTextDetail;
    private MaterialCardView cardView;
    private MaterialTextView textViewNoData;
    private String payment_type, user_id, user_points;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_point_claim);

        method = new Method(RewardPointClaim.this);
        method.forceRTLIfSupported();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        paymentLists = new ArrayList<>();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_points = intent.getStringExtra("user_points");

        progressDialog = new ProgressDialog(RewardPointClaim.this);

        toolbar = findViewById(R.id.toolbar_reward_point_claim);
        toolbar.setTitle(getResources().getString(R.string.payment_detail));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.linearLayout_reward_point_claim);
        method.adView(linearLayout);

        cardView = findViewById(R.id.cardView_reward_point_claim);
        textViewNoData = findViewById(R.id.textView_noData_reward_point_claim);
        spinner = findViewById(R.id.spinner_reward_point_claim);
        editTextDetail = findViewById(R.id.editText_detail_reward_point_claim);
        buttonSubmit = findViewById(R.id.button_reward_point_claim);

        cardView.setVisibility(View.GONE);
        textViewNoData.setVisibility(View.GONE);

        buttonSubmit.setOnClickListener(v -> detail());

        if (method.isNetworkAvailable()) {
            paymentMethod();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload));
        } else {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
        }
        payment_type = paymentLists.get(position).getMode_title();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void detail() {

        String detail = editTextDetail.getText().toString();

        editTextDetail.setError(null);

        if (payment_type.equals(getResources().getString(R.string.select_payment_type)) || payment_type.equals("") || payment_type.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_payment));
        } else if (detail.equals("") || detail.isEmpty()) {
            editTextDetail.requestFocus();
            editTextDetail.setError(getResources().getString(R.string.please_enter_detail));
        } else {

            editTextDetail.clearFocus();
            imm.hideSoftInputFromWindow(editTextDetail.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                detail_submit(user_id, user_points, payment_type, detail);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }

    }

    public void paymentMethod() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RewardPointClaim.this));
        jsObj.addProperty("method_name", "get_payment_mode");
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

                        textViewNoData.setVisibility(View.VISIBLE);

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String mode_title = object.getString("mode_title");

                            paymentLists.add(new PaymentList(id, mode_title));

                        }

                        if (paymentLists.size() != 0) {

                            List<String> arrayList = new ArrayList<String>();
                            for (int i = 0; i < paymentLists.size(); i++) {
                                arrayList.add(paymentLists.get(i).getMode_title());
                            }

                            spinner.setOnItemSelectedListener(RewardPointClaim.this);

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RewardPointClaim.this, android.R.layout.simple_spinner_item, arrayList);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);

                            cardView.setVisibility(View.VISIBLE);

                        } else {
                            textViewNoData.setVisibility(View.VISIBLE);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                textViewNoData.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void detail_submit(final String user_id, final String user_points, String payment_mode, String detail) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RewardPointClaim.this));
        jsObj.addProperty("method_name", "user_redeem_request");
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("user_points", user_points);
        jsObj.addProperty("payment_mode", payment_mode);
        jsObj.addProperty("bank_details", detail);
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

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String msg = object.getString("msg");
                            String success = object.getString("success");

                            if (success.equals("1")) {
                                Events.RewardNotify rewardNotify = new Events.RewardNotify("");
                                GlobalBus.getBus().post(rewardNotify);
                                onBackPressed();
                            }

                            Toast.makeText(RewardPointClaim.this, msg, Toast.LENGTH_SHORT).show();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }
}
