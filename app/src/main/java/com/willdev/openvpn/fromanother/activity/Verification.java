package com.willdev.openvpn.fromanother.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class Verification extends AppCompatActivity {

    private Method method;
    private PinView pinView;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private String verification, name, email, password, phoneNo, reference;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        method = new Method(Verification.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(Verification.this);

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            phoneNo = intent.getStringExtra("phoneNo");
            reference = intent.getStringExtra("reference");
        } else {
            name = method.pref.getString(method.reg_name, null);
            email = method.pref.getString(method.reg_email, null);
            password = method.pref.getString(method.reg_password, null);
            phoneNo = method.pref.getString(method.reg_phoneNo, null);
            reference = method.pref.getString(method.reg_reference, null);
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pinView = findViewById(R.id.firstPinView);
        MaterialButton button_verification = findViewById(R.id.button_verification);
        MaterialButton button_register = findViewById(R.id.button_register_verification);
        MaterialTextView textView = findViewById(R.id.resend_verification);

        textView.setOnClickListener(v -> {

            Random generator = new Random();
            int n = generator.nextInt(9999 - 1000) + 1000;

            String stringEmail = method.pref.getString(method.reg_email, null);
            resend_verification(stringEmail, String.valueOf(n));

        });

        button_verification.setOnClickListener(v -> {
            verification = pinView.getText().toString();
            verification();
        });

        button_register.setOnClickListener(v -> {
            method.editor.putBoolean(method.is_verification, false);
            method.editor.commit();
            startActivity(new Intent(Verification.this, Register.class));
            finishAffinity();
        });

    }

    public void verification() {

        pinView.clearFocus();
        imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

        if (verification == null || verification.equals("") || verification.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_verification_code));
        } else {
            if (method.isNetworkAvailable()) {
                pinView.setText("");
                if (verification.equals(method.pref.getString(method.verification_code, null))) {
                    register(name, email, password, phoneNo, reference);
                } else {
                    method.alertBox(getResources().getString(R.string.verification_message));
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    @SuppressLint("HardwareIds")
    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone, String reference) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        String device_id;
        try {
            device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            device_id = "Not Found";
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("method_name", "user_register");
        jsObj.addProperty("type", "normal");
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        jsObj.addProperty("device_id", device_id);
        jsObj.addProperty("user_refrence_code", reference);
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

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        method.editor.putBoolean(method.is_verification, false);
                        method.editor.commit();

                        if (success.equals("1")) {
                            startActivity(new Intent(Verification.this, Login.class));
                            finishAffinity();
                        } else {
                            startActivity(new Intent(Verification.this, Register.class));
                            finishAffinity();
                        }

                        Toast.makeText(Verification.this, msg, Toast.LENGTH_SHORT).show();

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
            }
        });
    }

    public void resend_verification(String sendEmail, String otp) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("method_name", "user_register_verify_email");
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("otp_code", otp);
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

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        if (success.equals("1")) {
                            method.editor.putString(method.verification_code, otp);
                            method.editor.commit();
                        }

                        method.alertBox(msg);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                method.alertBox(getResources().getString(R.string.server_time_out));
                progressDialog.dismiss();
            }
        });
    }
}
