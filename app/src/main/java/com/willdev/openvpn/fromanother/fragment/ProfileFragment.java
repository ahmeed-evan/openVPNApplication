package com.willdev.openvpn.fromanother.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.item.ProfileList;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ProfileFragment extends Fragment {

    private Method method;
    private Animation myAnim;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private ProfileList profileList;
    private CoordinatorLayout coordinatorLayout;
    private MaterialButton button_follow;
    private Button saveButton;
    private FloatingActionButton fabButton;
    private boolean isVideo_type = false;
    private RelativeLayout relativeLayout;
    private ImageView imageView_profile, imageView_loginType, imageView_youtube, imageView_instagram;
    private LinearLayout linearLayout_followings, linearLayout_follower;
    private String type, user_id, name, email, phone, instagram, youtube, getUser_id;
    private MaterialTextView textViewFollowing, textViewFollower, textView_totalVideo, textViewUserName, textView_noData;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);


        progressDialog = new ProgressDialog(getActivity());

        ImageView goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        assert getArguments() != null;
        type = getArguments().getString("type");
        getUser_id = getArguments().getString("id");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        method = new Method(getActivity());

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout_pro);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        textView_noData = view.findViewById(R.id.textView_information_profile);
        progressBar = view.findViewById(R.id.progressbar_profile);
        fabButton = view.findViewById(R.id.fab_profile);
        textViewUserName = view.findViewById(R.id.textView_name_pro);
        imageView_profile = view.findViewById(R.id.imageView_pro);
        imageView_loginType = view.findViewById(R.id.imageView_loginType_pro);
        imageView_youtube = view.findViewById(R.id.imageView_youtube_pro);
        imageView_instagram = view.findViewById(R.id.imageView_instagram_pro);
        linearLayout_followings = view.findViewById(R.id.linearLayout_followings_pro);
        linearLayout_follower = view.findViewById(R.id.linearLayout_follower_pro);
        textView_totalVideo = view.findViewById(R.id.textView_video_pro);
        textViewFollowing = view.findViewById(R.id.textView_following_pro);
        textViewFollower = view.findViewById(R.id.textView_followers_pro);
        button_follow = view.findViewById(R.id.button_follow_pro);

        progressBar.setVisibility(View.GONE);
        fabButton.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);
        imageView_loginType.setVisibility(View.GONE);

        if (method.isDarkMode()) {
            imageView_instagram.setImageDrawable(getResources().getDrawable(R.drawable.logo));
            imageView_youtube.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        } else {
            imageView_instagram.setImageDrawable(getResources().getDrawable(R.drawable.logo));
            imageView_youtube.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }

        relativeLayout.setOnClickListener(v -> {

        });


        isVideo_type = false;
        callData();

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.ic_searchView);
        if (method.isLogin()) {
            searchItem.setVisible(true);
        } else {
            searchItem.setVisible(false);
        }
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

    private void callData() {
        if (getActivity() != null) {
            if (method.isNetworkAvailable()) {
                if (method.isLogin()) {
                    profile(method.userId(), getUser_id);
                } else {
                    if (!type.equals("user")) {
                        profile("", getUser_id);
                    } else {
                        coordinatorLayout.setVisibility(View.GONE);
                        textView_noData.setVisibility(View.VISIBLE);
                        textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                    }
                }
            } else {
                coordinatorLayout.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.no_data_found));
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void profile(final String id, final String other_user_id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            if (id.equals(getUser_id)) {
                jsObj.addProperty("method_name", "user_profile");
            } else {
                jsObj.addProperty("method_name", "other_user_profile");
                jsObj.addProperty("other_user_id", other_user_id);
            }
            jsObj.addProperty("user_id", id);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

                        String res = new String(responseBody);

                        String already_follow = null;
                        String total_point = null;

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

                                if (success.equals("1")) {

                                    fabButton.setVisibility(View.VISIBLE);

                                    user_id = object.getString("user_id");
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    if (id.equals(getUser_id)) {
                                        phone = object.getString("phone");
                                        total_point = object.getString("total_point");
                                    } else {
                                        already_follow = object.getString("already_follow");
                                    }
                                    String is_verified = object.getString("is_verified");
                                    String total_status = object.getString("total_status");
                                    youtube = object.getString("user_youtube");
                                    instagram = object.getString("user_instagram");
                                    String user_image = object.getString("user_image");
                                    String user_code = object.getString("user_code");
                                    String total_followers = object.getString("total_followers");
                                    String total_following = object.getString("total_following");

                                    profileList = new ProfileList(user_id, name, email, phone, is_verified, user_image, total_status, youtube, instagram, user_code, total_point, total_followers, total_following, already_follow);

                                    if (id.equals(getUser_id)) {
                                        method.editor.putString(method.userImage, user_image);
                                        method.editor.commit();
                                    }

                                    if (method.isLogin()) {
                                        if (id.equals(other_user_id)) {
                                            button_follow.setText(getResources().getString(R.string.edit_profile));
                                            if (method.getLoginType().equals("google")) {
                                                imageView_loginType.setVisibility(View.VISIBLE);
                                                imageView_loginType.setImageDrawable(getResources().getDrawable(R.drawable.google_user_pro));
                                            } else if (method.getLoginType().equals("facebook")) {
                                                imageView_loginType.setVisibility(View.VISIBLE);
                                                imageView_loginType.setImageDrawable(getResources().getDrawable(R.drawable.fb_user_pro));
                                            } else {
                                                imageView_loginType.setVisibility(View.GONE);
                                            }
                                        } else {
                                            if (profileList.getAlready_follow().equals("true")) {
                                                button_follow.setText(getResources().getString(R.string.unfollow));
                                            } else {
                                                button_follow.setText(getResources().getString(R.string.follow));
                                            }
                                        }
                                    } else {
                                        button_follow.setText(getResources().getString(R.string.follow));
                                    }

                                    if (!profileList.getUser_image().equals("")) {
                                        Glide.with(getActivity().getApplicationContext()).load(profileList.getUser_image())
                                                .placeholder(R.drawable.user_profile).into(imageView_profile);
                                    }

                                    textViewUserName.setText(profileList.getUser_name());
                                    if (profileList.getIs_verified().equals("true")) {
                                        textViewUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
                                    }
                                    textViewFollower.setText(method.format(Double.parseDouble(profileList.getTotal_followers())));
                                    textViewFollowing.setText(method.format(Double.parseDouble(profileList.getTotal_following())));
                                    textView_totalVideo.setText(method.format(Double.parseDouble(profileList.getTotal_status())));



                                    imageView_youtube.setOnClickListener(v -> {
                                        imageView_youtube.startAnimation(myAnim);
                                        String string = profileList.getUser_youtube();
                                        if (string.equals("")) {
                                            method.alertBox(getResources().getString(R.string.user_not_youtube_link));
                                        } else {
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(string));
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                method.alertBox(getResources().getString(R.string.wrong));
                                            }
                                        }
                                    });

                                    imageView_instagram.setOnClickListener(v -> {
                                        imageView_instagram.startAnimation(myAnim);
                                        String string = profileList.getUser_instagram();
                                        if (string.equals("")) {
                                            method.alertBox(getResources().getString(R.string.user_not_instagram_link));
                                        } else {
                                            Uri uri = Uri.parse(string);
                                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                                            likeIng.setPackage("com.instagram.android");
                                            try {
                                                startActivity(likeIng);
                                            } catch (ActivityNotFoundException e) {
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse(string)));
                                                } catch (Exception e1) {
                                                    method.alertBox(getResources().getString(R.string.wrong));
                                                }
                                            }
                                        }
                                    });

                                    button_follow.setOnClickListener(v -> {
                                        if (method.isNetworkAvailable()) {
                                            if (method.isLogin()) {
                                                if (id.equals(other_user_id)) {
                                                    editProfile(id);
                                                } else {
                                                    follow(id, other_user_id);
                                                }
                                            } else {
                                                method.alertBox(getResources().getString(R.string.you_have_not_login));
                                            }
                                        } else {
                                            method.alertBox(getResources().getString(R.string.internet_connection));
                                        }
                                    });

                                    linearLayout_followings.setOnClickListener(v -> {
                                        if (!profileList.getTotal_following().equals("0")) {
                                            UserFollowFragment userFollowFragment = new UserFollowFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", "following");
                                            bundle.putString("user_id", profileList.getUser_id());
                                            bundle.putString("search", "");
                                            userFollowFragment.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, userFollowFragment, getResources().getString(R.string.following)).addToBackStack(getResources().getString(R.string.following)).commitAllowingStateLoss();
                                        } else {
                                            method.alertBox(getResources().getString(R.string.not_following));
                                        }

                                    });

                                    linearLayout_follower.setOnClickListener(v -> {
                                        if (!profileList.getTotal_followers().equals("0")) {
                                            UserFollowFragment userFollowFragment = new UserFollowFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", "follower");
                                            bundle.putString("user_id", profileList.getUser_id());
                                            bundle.putString("search", "");
                                            userFollowFragment.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, userFollowFragment, getResources().getString(R.string.following)).addToBackStack("sub").commitAllowingStateLoss();
                                        } else {
                                            method.alertBox(getResources().getString(R.string.not_follower));
                                        }
                                    });



                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            textView_noData.setVisibility(View.VISIBLE);
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

    private void editProfile(String id) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("instagram", instagram);
        args.putString("youtube", youtube);
        args.putString("user_image", profileList.getUser_image());
        args.putString("profileId", id);
        editProfileFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, editProfileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commit();
    }

    private void follow(final String user_id, final String other_user) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_follow");
            jsObj.addProperty("user_id", other_user);
            jsObj.addProperty("follower_id", user_id);
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
                                method.alertBox(message);

                            } else {

                                JSONObject object = jsonObject.getJSONObject(Constant.tag);
                                String msg = object.getString("msg");
                                String success = object.getString("success");

                                if (success.equals("1")) {
                                    String activity_status = object.getString("activity_status");
                                    if (activity_status.equals("1")) {
                                        button_follow.setText(getResources().getString(R.string.unfollow));
                                    } else {
                                        button_follow.setText(getResources().getString(R.string.follow));
                                    }
                                    getUser_id = other_user;
                                    profile(user_id, other_user);
                                } else {
                                    method.alertBox(msg);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

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

    }

}
