package com.willdev.openvpn.fromanother.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.RewardPointAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RewardCurrentFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MaterialTextView textView_noData;
    private RewardPointAdapter rewardPointAdapter;
    private List<RewardPointList> rewardPointLists;
    private LayoutAnimationController layoutAnimationController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        method = new Method(getActivity());

        rewardPointLists = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_user_rm_fragment);
        textView_noData = view.findViewById(R.id.textView_user_rm_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        callData();

        return view;
    }

    private void rewardPoint(String id) {

        if (getActivity() != null) {

            rewardPointLists.clear();
            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_rewads_point");
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
                                if(status.equals("-2")){
                                    method.suspend(message);
                                }else {
                                    method.alertBox(message);
                                }

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.tag);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    JSONArray jsonArrayReward = object.getJSONArray("user_rewads_point");

                                    for (int j = 0; j < jsonArrayReward.length(); j++) {

                                        JSONObject object_reward = jsonArrayReward.getJSONObject(j);
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
                                }

                                if (rewardPointLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    rewardPointAdapter = new RewardPointAdapter(getActivity(), rewardPointLists);
                                    recyclerView.setAdapter(rewardPointAdapter);
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

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                rewardPoint(method.userId());
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

}
