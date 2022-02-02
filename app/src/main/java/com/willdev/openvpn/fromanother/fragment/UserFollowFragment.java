package com.willdev.openvpn.fromanother.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;

import com.willdev.openvpn.fromanother.UserFollowAdapter;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.item.UserFollowList;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.EndlessRecyclerViewScrollListener;
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

public class UserFollowFragment extends Fragment {

    private OnClick onClick;
    private Method method;
    private String type, user_id, search;
    private List<UserFollowList> userFollowLists;
    private ProgressBar progressBar;
    private MaterialTextView textView_noData;
    private RecyclerView recyclerView;
    private UserFollowAdapter userFollowAdapter;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int pagination_index = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.portrait_fragment, container, false);

        assert getArguments() != null;
        type = getArguments().getString("type");
        user_id = getArguments().getString("user_id");
        search = getArguments().getString("search");


        userFollowLists = new ArrayList();

        onClick = (position, title, type, status_type, id, tag) -> {
            if (getActivity() != null) {
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle_profile = new Bundle();
                bundle_profile.putString("type", "other_user");
                bundle_profile.putString("id", id);
                profileFragment.setArguments(bundle_profile);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
            } else {
                method.alertBox(getResources().getString(R.string.wrong));
            }
        };
        method = new Method(getActivity(), onClick);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_portrait_fragment);
        textView_noData = view.findViewById(R.id.textView_portrait_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_portrait_fragment);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagination_index++;
                            callData();
                        }
                    }, 1000);
                } else {
                    userFollowAdapter.hideHeader();
                }
            }
        });

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.ic_searchView);
        searchItem.setVisible(method.isLogin());
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (method.isNetworkAvailable()) {
                    if (method.isLogin()) {
                        UserFollowFragment userFollowFragment = new UserFollowFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "search_user");
                        bundle.putString("user_id", method.userId());
                        bundle.putString("search", query);
                        userFollowFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, userFollowFragment, query).addToBackStack(query).commitAllowingStateLoss();
                    } else {
                        method.alertBox(getResources().getString(R.string.you_have_not_login));
                    }
                    return false;
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void callData() {
        if (method.isNetworkAvailable()) {
            userFollow(user_id);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void userFollow(String userId) {

        if (getActivity() != null) {

            if (userFollowAdapter == null) {
                userFollowLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            if (type.equals("follower")) {
                jsObj.addProperty("method_name", "user_followers");
            } else if (type.equals("following")) {
                jsObj.addProperty("method_name", "user_following");
            } else {
                jsObj.addProperty("search_keyword", search);
                jsObj.addProperty("method_name", "user_search");
            }
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("page", pagination_index);
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
                                    String user_id = object.getString("user_id");
                                    String user_name = object.getString("user_name");
                                    String user_follower_image = object.getString("user_image");
                                    String is_verified_user_followers = object.getString("is_verified");

                                    userFollowLists.add(new UserFollowList(user_id, user_name, user_follower_image, is_verified_user_followers));

                                }

                                if (jsonArray.length() == 0) {
                                    if (userFollowAdapter != null) {
                                        isOver = true;
                                        userFollowAdapter.hideHeader();
                                    }
                                }

                                if (userFollowAdapter == null) {
                                    if (userFollowLists.size() == 0) {
                                        textView_noData.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_noData.setVisibility(View.GONE);
                                        userFollowAdapter = new UserFollowAdapter(getActivity(), userFollowLists, "follow_following", onClick);
                                        recyclerView.setAdapter(userFollowAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    userFollowAdapter.notifyDataSetChanged();
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
