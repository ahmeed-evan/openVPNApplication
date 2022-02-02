package com.willdev.openvpn.fromanother.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.ViewpagerRewardAdapter;
import com.willdev.openvpn.fromanother.activity.RewardPointClaim;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Events;
import com.willdev.openvpn.fromanother.util.util.GlobalBus;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RewardPointFragment extends Fragment {

    private Method method;
    private Menu menu;
    private ProgressBar progressBar;
    private String total_point = null;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MaterialButton button;
    private AppBarLayout appbar;
    private FragmentManager childFragManger;
    private String type = "";
    private CoordinatorLayout coordinatorLayout;
    private MaterialTextView textView_menu_point_count, textView_point_menu, textView_point,
            textView_money, textView_noData;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reward_point_fragment, container, false);

        GlobalBus.getBus().register(this);

        childFragManger = getChildFragmentManager();

        type = getArguments().getString("type");


        method = new Method(getActivity());

        coordinatorLayout = view.findViewById(R.id.main_content);
        progressBar = view.findViewById(R.id.progressbar_reward_point_fragment);
        textView_noData = view.findViewById(R.id.textView_noData_reward_point_fragment);
        textView_point = view.findViewById(R.id.textView_total_reward_point_fragment);
        textView_money = view.findViewById(R.id.textView_money_reward_point_fragment);
        tabLayout = view.findViewById(R.id.tablayout_reward_point_fragment);
        viewPager = view.findViewById(R.id.viewPager_reward_point_fragment);
        button = view.findViewById(R.id.button_reward_point_fragment);
        appbar = view.findViewById(R.id.appbar_reward_point_fragment);

        coordinatorLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    if (total_point != null) {

                    }
                } else if (isShow) {
                    isShow = false;
                    if (total_point != null) {

                    }
                }
            }
        });

        String[] tabName = {getResources().getString(R.string.current_point),
                getResources().getString(R.string.withdrawal_history)};

        for (int i = 0; i < 2; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabName[i]));
        }

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    public void callData() {

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                userData(method.userId());
            } else {
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                method.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.point_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void userData(String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "reward_points");
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

                                if (success.equals("1")) {

                                    String user_id = object.getString("user_id");
                                    total_point = object.getString("total_point");
                                    String redeem_points = object.getString("redeem_points");
                                    String redeem_money = object.getString("redeem_money");
                                    String minimum_redeem_points = object.getString("minimum_redeem_points");

                                    String money = redeem_points
                                            + " " + getResources().getString(R.string.point)
                                            + " " + getResources().getString(R.string.equal)
                                            + " " + redeem_money;
                                    textView_money.setText(money);

                                    if (total_point.equals("")) {
                                        button.setVisibility(View.GONE);
                                    } else {
                                        button.setVisibility(View.VISIBLE);
                                    }


                                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                    tabLayout.setTabMode(TabLayout.MODE_FIXED);


                                    ViewpagerRewardAdapter viewpagerRewardAdapter = new ViewpagerRewardAdapter(childFragManger, tabLayout.getTabCount(), getActivity());
                                    viewPager.setAdapter(viewpagerRewardAdapter);

                                    if (type.equals("payment_withdraw")) {
                                        viewPager.setCurrentItem(1);
                                    }


                                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


                                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            viewPager.setCurrentItem(tab.getPosition());
                                        }

                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {

                                        }

                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {

                                        }
                                    });

                                    textView_point.setText(total_point);

                                    coordinatorLayout.setVisibility(View.VISIBLE);

                                    button.setOnClickListener(v -> {
                                        int point = Integer.parseInt(minimum_redeem_points);
                                        int compair = Integer.parseInt(total_point);
                                        String minimum_point = getResources().getString(R.string.minimum)
                                                + " " + minimum_redeem_points
                                                + " " + getResources().getString(R.string.point_require);

                                        if (compair >= point) {
                                            startActivity(new Intent(getActivity(), RewardPointClaim.class)
                                                    .putExtra("user_id", user_id)
                                                    .putExtra("user_points", total_point));
                                        } else {
                                            method.alertBox(minimum_point);
                                        }
                                    });

                                    if (menu != null) {
                                        changeCart(menu);
                                    }

                                } else {
                                    textView_noData.setVisibility(View.VISIBLE);
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

    private void changeCart(Menu menu) {
        View cart = menu.findItem(R.id.action_point).getActionView();
        textView_menu_point_count = cart.findViewById(R.id.textView_menu_point_count_layout);
        textView_point_menu = cart.findViewById(R.id.textView_menu_point_layout);
        textView_menu_point_count.setVisibility(View.GONE);
        textView_point_menu.setVisibility(View.GONE);
        if (total_point != null) {
            if (textView_menu_point_count != null) {
                textView_menu_point_count.setText(total_point);
            }
        }
        textView_menu_point_count.setTypeface(textView_menu_point_count.getTypeface(), Typeface.BOLD);
    }

    @Subscribe
    public void getReward(Events.RewardNotify rewardNotify) {
        callData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        GlobalBus.getBus().unregister(this);
    }

}