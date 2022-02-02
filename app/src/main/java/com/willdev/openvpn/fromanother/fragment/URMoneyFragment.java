package com.willdev.openvpn.fromanother.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.UserRMAdapter;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.item.UserRMList;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
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


public class URMoneyFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private MaterialTextView textView_noData;
    private RecyclerView recyclerView;
    private List<UserRMList> userRMLists;
    private UserRMAdapter userRMAdapter;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        onClick = (position, title, type, status_type, id, tag) -> {
            if (tag.equals("td")) {
                TDFragment tdFragment = new TDFragment();
                Bundle bundle = new Bundle();
                bundle.putString("redeem_id", id);
                tdFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, tdFragment, type).addToBackStack(null).commitAllowingStateLoss();
            }else {
                URHistoryFragment urHistoryFragment = new URHistoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("redeem_id", id);
                urHistoryFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, urHistoryFragment, type).addToBackStack(null).commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), onClick);

        userRMLists = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_user_rm_fragment);
        textView_noData = view.findViewById(R.id.textView_user_rm_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                history(method.userId());
            } else {
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            recyclerView.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void history(final String id) {

        if (getActivity() != null) {

            userRMLists.clear();
            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_redeem_history");
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

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.tag);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String redeem_id = object.getString("redeem_id");
                                    String user_points = object.getString("user_points");
                                    String redeem_price = object.getString("redeem_price");
                                    String request_date = object.getString("request_date");
                                    String status = object.getString("status");

                                    userRMLists.add(new UserRMList(redeem_id, user_points, redeem_price, request_date, status));
                                }

                                if (userRMLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    userRMAdapter = new UserRMAdapter(getActivity(), userRMLists, onClick, "ur");
                                    recyclerView.setAdapter(userRMAdapter);
                                    recyclerView.setLayoutAnimation(animation);
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
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
