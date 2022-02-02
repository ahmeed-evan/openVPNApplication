package com.willdev.openvpn.fromanother.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Events;
import com.willdev.openvpn.fromanother.util.util.GlobalBus;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.view.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Login extends AppCompatActivity {

    private TextInputEditText editText_email, editText_password;
    private String email, password;

    private Method method;

    public static final String mypreference = "mypref";
    public static final String pref_email = "pref_email";
    public static final String pref_password = "pref_password";
    public static final String pref_check = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;


    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;


    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        method = new Method(Login.this);
        method.forceRTLIfSupported();

        pref = getSharedPreferences(mypreference, 0);
        editor = pref.edit();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(Login.this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        callbackManager = CallbackManager.Factory.create();

        editText_email = findViewById(R.id.editText_email_login_activity);
        editText_password = findViewById(R.id.editText_password_login_activity);

        MaterialButton button_login = findViewById(R.id.button_login_activity);
        final LinearLayout linearLayout_googleSign = findViewById(R.id.linearLayout_google_login);
        final FrameLayout frameLayout_fbSign = findViewById(R.id.frameLayout_login);
        MaterialButton button_skip = findViewById(R.id.button_skip_login_activity);
        MaterialTextView textView_register = findViewById(R.id.textView_register_login);
        MaterialTextView textView_forgotPassword = findViewById(R.id.textView_forget_password_login);
        final SmoothCheckBox checkBox = findViewById(R.id.checkbox_login_activity);
        checkBox.setChecked(false);

        if (pref.getBoolean(pref_check, false)) {
            editText_email.setText(pref.getString(pref_email, null));
            editText_password.setText(pref.getString(pref_password, null));
            checkBox.setChecked(true);
        } else {
            editText_email.setText("");
            editText_password.setText("");
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener((checkBox1, isChecked) -> {
            if (isChecked) {
                editor.putString(pref_email, editText_email.getText().toString());
                editor.putString(pref_password, editText_password.getText().toString());
                editor.putBoolean(pref_check, true);
            } else {
                editor.putBoolean(pref_check, false);
            }
            editor.commit();
        });

        button_login.setOnClickListener(v -> {

            email = editText_email.getText().toString();
            password = editText_password.getText().toString();

            login(checkBox);
        });

        linearLayout_googleSign.setOnClickListener(view -> signIn());

        frameLayout_fbSign.setOnClickListener(v -> {
            if (v == frameLayout_fbSign) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList(EMAIL));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbUser(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        textView_register.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, Register.class));
        });

        button_skip.setOnClickListener(v -> {
            if (Method.loginBack) {
                Method.loginBack = false;
                onBackPressed();
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });

        textView_forgotPassword.setOnClickListener(v -> {
            Method.loginBack = false;
            startActivity(new Intent(Login.this, ForgetPassword.class));
        });

    }


    private void signIn() {
        if (method.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);



            assert account != null;
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();

            registerSocialNetwork(id, name, email, "google");

        } catch (ApiException e) {

        }
    }


    private void fbUser(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    registerSocialNetwork(id, name, email, "facebook");
                } catch (JSONException e) {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        registerSocialNetwork(id, name, "", "facebook");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login(SmoothCheckBox checkBox) {

        editText_email.setError(null);
        editText_password.setError(null);

        if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.isEmpty()) {
            editText_password.requestFocus();
            editText_password.setError(getResources().getString(R.string.please_enter_password));
        } else {

            editText_email.clearFocus();
            editText_password.clearFocus();
            imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_password.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                login(email, password, checkBox);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void login(final String sendEmail, final String sendPassword, final SmoothCheckBox checkBox) {

        /*
        OSDeviceState device = OneSignal.getDeviceState();
        boolean subscribed = device.isSubscribed();
        if (!subscribed) {
            OneSignal.addTrigger("unsubscribed", "true");
        }
        */

        //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        //status.getPermissionStatus().getEnabled();

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("player_id", "123");
        jsObj.addProperty("method_name", "user_login");
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
                        method.alertBox(message);

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String success = object.getString("success");
                        String msg = object.getString("msg");

                        if (success.equals("1")) {

                            String user_id = object.getString("user_id");
                            String name = object.getString("name");

                            if (checkBox.isChecked()) {
                                editor.putString(pref_email, editText_email.getText().toString());
                                editor.putString(pref_password, editText_password.getText().toString());
                                editor.putBoolean(pref_check, true);
                                editor.commit();
                            }

                            OneSignal.sendTag("user_id", user_id);
                            //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                            //status.getPermissionStatus().getEnabled();
                            OneSignal.sendTag("player_id", OneSignal.getDeviceState().getUserId());

                            method.editor.putBoolean(method.pref_login, true);
                            method.editor.putString(method.profileId, user_id);
                            method.editor.putString(method.loginType, "normal");
                            method.editor.commit();
                            editText_email.setText("");
                            editText_password.setText("");

                            if (Method.loginBack) {
                                Events.Login loginNotify = new Events.Login("");
                                GlobalBus.getBus().post(loginNotify);
                                Method.loginBack = false;
                                onBackPressed();
                            } else {
                                startActivity(new Intent(Login.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            }

                        } else {
                            method.alertBox(msg);
                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }

        });
    }


    @SuppressLint("HardwareIds")
    public void registerSocialNetwork(String id, String sendName, String sendEmail, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        String device_id;
        try {
            device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            device_id = "Not Found";
        }


       // OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        //status.getPermissionStatus().getEnabled();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("method_name", "user_register");
        jsObj.addProperty("type", type);
        jsObj.addProperty("auth_id", id);
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("player_id", OneSignal.getDeviceState().getUserId());
        jsObj.addProperty("device_id", device_id);
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
                        method.alertBox(message);

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String success = object.getString("success");
                        String msg = object.getString("msg");

                        method.editor.putBoolean(method.is_verification, false);
                        method.editor.commit();

                        if (success.equals("1")) {

                            String user_id = object.getString("user_id");
                            String email = object.getString("email");
                            String name = object.getString("name");
                            String auth_id = object.getString("auth_id");
                            String referral_code = object.getString("referral_code");

                            OneSignal.sendTag("user_id", user_id);
                            //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                            //status.getPermissionStatus().getEnabled();
                            OneSignal.sendTag("player_id", OneSignal.getDeviceState().getUserId());

                            method.editor.putBoolean(method.pref_login, true);
                            method.editor.putString(method.profileId, user_id);
                            method.editor.putString(method.loginType, type);
                            method.editor.commit();

                            if (Method.loginBack) {
                                Events.Login loginNotify = new Events.Login("");
                                GlobalBus.getBus().post(loginNotify);
                                Method.loginBack = false;
                                onBackPressed();
                            } else {
                                if (referral_code.equals("true")) {
                                    startActivity(new Intent(Login.this, EnterReferenceCode.class)
                                            .putExtra("user_id", user_id)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }
                                finishAffinity();
                            }

                        } else {
                            String is_suspended = object.getString("is_suspended");
                            if (is_suspended.equals("true")) {
                                if (type.equals("google")) {
                                    mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    method.editor.putBoolean(method.pref_login, false);
                                                    method.editor.commit();
                                                }
                                            });
                                } else {
                                    LoginManager.getInstance().logOut();
                                }

                            }
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
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }

        });
    }

    @Override
    public void onBackPressed() {
        Method.loginBack = false;
        super.onBackPressed();
    }
}
