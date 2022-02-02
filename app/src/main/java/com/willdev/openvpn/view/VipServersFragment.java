package com.willdev.openvpn.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.adapter.VipServerAdapter;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.utils.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VipServersFragment extends Fragment implements VipServerAdapter.OnSelectListener {

    @BindView(R.id.rcv_servers)
    RecyclerView rcvServers;
    private RelativeLayout animationHolder;
    @BindView(R.id.purchase_layout)
    RelativeLayout mPurchaseLayout;
    @BindView(R.id.vip_unblock)
    ImageButton mUnblockButton;
    private VipServerAdapter serverAdapter;

    private RewardedVideoAd rewardedVideoAd;
    private String TAG = "RewarderVideoAd";
    Server serverr;
    private RewardedAd mRewardedAd;
    private Boolean facebookAd = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vip_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

            mPurchaseLayout.setVisibility(View.GONE);


        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
                loadAds();
            }
        });

        Log.e("rewardID", WebAPI.ADMOB_REWARD_ID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        serverAdapter = new VipServerAdapter(getActivity());
        serverAdapter.setOnSelectListener(this);
        rcvServers.setLayoutManager(layoutManager);
        rcvServers.setAdapter(serverAdapter);
        loadServers();
    }

    private void loadServers() {
        ArrayList<Server> servers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(WebAPI.PREMIUM_SERVERS);
            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Server(object.getString("serverName"),
                        object.getString("flagURL"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
                Log.v("Servers",object.getString("ovpnConfiguration"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverAdapter.setData(servers);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick(R.id.vip_unblock)
    void openPurchase(){


    }

    @Override
    public void onSelected(Server server) {
        if (getActivity() != null)
        {
            if (Config.vip_subscription || Config.all_subscription) {
                Intent mIntent = new Intent();
                mIntent.putExtra("server", server);
                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                getActivity().finish();

            } else
            {
                serverr = server;
                showDialog(getActivity());
            }

        }
    }

    public void showDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ads_dialog, null);
        builder.setView(dialogView);
        Button purchase = dialogView.findViewById(R.id.purchase);
        Button watchAd = dialogView.findViewById(R.id.watchAd);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PurchaseActivity.class);
                startActivity(intent);
            }
        });

        watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = getActivity();
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                            Log.d("TAG", "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                }
                else if (facebookAd) {
                    rewardedVideoAd.show();
                } else {
                    Toast.makeText(activity, "ads Not Available Or Buy Subscription", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void loadAds()
    {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getActivity(), WebAPI.ADMOB_REWARD_ID,
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {

                                Log.d(TAG, "Ad was shown.");
                                mRewardedAd = null;
                                Intent mIntent = new Intent();
                                mIntent.putExtra("server", serverr);
                                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                                getActivity().finish();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {

                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                            }
                        });
                    }
                });


        rewardedVideoAd = new RewardedVideoAd(getActivity(), WebAPI.ADMOB_REWARD_ID);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {

                Log.e(TAG, "FB Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

                Log.d(TAG, "FB Rewarded video ad is loaded and ready to be displayed!");
                facebookAd = true;
            }

            @Override
            public void onAdClicked(Ad ad) {

                Log.d(TAG, "FB Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {

                Log.d(TAG, "FB Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {

                Log.d(TAG, "FB Rewarded video completed!");
                facebookAd = false;
                Intent mIntent = new Intent();
                mIntent.putExtra("server", serverr);
                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                getActivity().finish();

            }

            @Override
            public void onRewardedVideoClosed() {

                Log.d(TAG, "FB Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }
}