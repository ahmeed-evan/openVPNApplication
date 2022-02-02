package com.willdev.openvpn.fromanother.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.view.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EnterReferenceCode extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private String user_id;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private TextInputEditText editText;
    private MaterialButton button_continue, button_skip;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_reference_code);

        method = new Method(EnterReferenceCode.this);
        method.forceRTLIfSupported();

        user_id = getIntent().getStringExtra("user_id");

        toolbar = findViewById(R.id.toolbar_erc);
        toolbar.setTitle(getResources().getString(R.string.reference_code));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(EnterReferenceCode.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = findViewById(R.id.editText_erc);
        button_continue = findViewById(R.id.button_continue_erc);
        button_skip = findViewById(R.id.button_skip_erc);

        button_continue.setOnClickListener(v -> {

            editText.setError(null);

            String ref_code = editText.getText().toString();

            if (ref_code.equals("") || ref_code.isEmpty()) {
                editText.requestFocus();
                editText.setError(getResources().getString(R.string.please_enter_reference_code));
            } else {

                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                if (method.isNetworkAvailable()) {
                    referenceCode(user_id, ref_code);
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }

            }

        });

        button_skip.setOnClickListener(v -> {
            startActivity(new Intent(EnterReferenceCode.this, MainActivity.class));
            finishAffinity();
        });

    }

    public void referenceCode(String userId, String code) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EnterReferenceCode.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("user_refrence_code", code);
        jsObj.addProperty("method_name", "apply_user_refrence_code");
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
                        String msg = object.getString("msg");

                        if (success.equals("1")) {
                            Toast.makeText(EnterReferenceCode.this, msg, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EnterReferenceCode.this, MainActivity.class));
                            finishAffinity();
                        } else {
                            method.alertBox(msg);
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

}
