package com.willdev.openvpn.fromanother.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.HistoryPointAdapter;
import com.willdev.openvpn.fromanother.item.RewardPointList;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class URHistoryFragment extends Fragment {

    private Method method;
    private String redeem_id;
    private ProgressBar progressBar;
    private MaterialTextView textView_noData;
    private RecyclerView recyclerView;
    private List<RewardPointList> rewardPointLists;
    private HistoryPointAdapter historyPointAdapter;
    private LayoutAnimationController layoutAnimationController;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.portrait_fragment, container, false);


        ImageView goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        method = new Method(getActivity());

        rewardPointLists = new ArrayList<>();

        redeem_id = getArguments().getString("redeem_id");

        int resId = R.anim.layout_animation_fall_down;
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_portrait_fragment);
        textView_noData = view.findViewById(R.id.textView_portrait_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_portrait_fragment);

        progressBar.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        callData();

        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                rewardPoint(redeem_id, method.userId());
            } else {
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void rewardPoint(String redeem_id, String id) {

        if (getActivity() != null) {

            rewardPointLists.clear();
            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_redeem_points_history");
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("redeem_id", redeem_id);
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

                                    JSONObject object_reward = jsonArray.getJSONObject(i);
                                    String id = object_reward.getString("id");
                                    String title = object_reward.getString("title");
                                    String status_thumbnail = object_reward.getString("status_thumbnail");
                                    String user_id = object_reward.getString("user_id");
                                    String activity_type = object_reward.getString("activity_type");
                                    String points = object_reward.getString("points");
                                    String date = object_reward.getString("date");
                                    String time = object_reward.getString("time");

                                    rewardPointLists.add(new RewardPointList(id, title, status_thumbnail, user_id, activity_type, points, date, time));

                                }

                                if (rewardPointLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    historyPointAdapter = new HistoryPointAdapter(getActivity(), rewardPointLists);
                                    recyclerView.setAdapter(historyPointAdapter);
                                    recyclerView.setLayoutAnimation(layoutAnimationController);
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
