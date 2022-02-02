package com.willdev.openvpn.fromanother.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.CLIPBOARD_SERVICE;


public class ReferenceCodeFragment extends Fragment {

    private Method method;
    private String user_code;
    private TextView textView;
    private MaterialTextView textView_noData;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutCopy;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reference_code_fragment, container, false);


        ImageView goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(v -> getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE));

        method = new Method(getActivity());

        progressBar = view.findViewById(R.id.progressbar_reference_code);
        relativeLayout = view.findViewById(R.id.rel_reference_code_fragment);
        linearLayoutCopy = view.findViewById(R.id.linearLayout_copy_reference_code);
        textView = view.findViewById(R.id.textView_reference_code);
        textView_noData = view.findViewById(R.id.textView_noDataFound_reference_code);

        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                profile(method.userId());
            } else {
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;

    }




    public void profile(String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_profile");
            jsObj.addProperty("user_id", id);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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

                                textView_noData.setVisibility(View.VISIBLE);

                            } else {

                                JSONObject object = jsonObject.getJSONObject(Constant.tag);
                                String success = object.getString("success");

                                if(success.equals("1")){

                                    user_code = object.getString("user_code");
                                    textView.setText(user_code);

                                    relativeLayout.setVisibility(View.VISIBLE);

                                    linearLayoutCopy.setOnClickListener(v -> {
                                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", user_code);
                                        assert clipboard != null;
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getActivity(), getResources().getString(R.string.copy_text), Toast.LENGTH_SHORT).show();
                                    });
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

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

    }

}
