package com.willdev.openvpn.adapter;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.willdev.openvpn.R;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.utils.Config;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NativeAdRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NativeAdRecyclerAdapter.OnSelectListener selectListener;

    private List<Server> mPostItems;
    private List<NativeAd> mAdItems;
    private NativeAdsManager mNativeAdsManager;
    private Activity mActivity;

    private static final int AD_DISPLAY_FREQUENCY = 3;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;
    private static int AD_STATUS = 1;

    public NativeAdRecyclerAdapter(
            Activity activity, List<Server> postItems, NativeAdsManager nativeAdsManager, String status) {
        mNativeAdsManager = nativeAdsManager;
        mPostItems = postItems;
        mAdItems = new ArrayList<>();
        mActivity = activity;

        if(status.equalsIgnoreCase("error"))
            AD_STATUS = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            NativeAdLayout inflatedView =
                    (NativeAdLayout)
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.native_ad_unit, parent, false);
            return new AdHolder(inflatedView);
        } else {
            View inflatedView =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_server, parent, false);
            return new PostHolder(inflatedView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!Config.vip_subscription && !Config.all_subscription && AD_STATUS != 0)
        {
            return (position % AD_DISPLAY_FREQUENCY == 0 && position > 0) ? AD_TYPE : POST_TYPE;
        }

        return POST_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == AD_TYPE) {
            NativeAd ad;

            if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                if (ad == null)
                {
                    return;
                }
                if (!ad.isAdInvalidated()) {
                    mAdItems.add(ad);
                } else {
                    Log.w(NativeAdRecyclerAdapter.class.getSimpleName(), "Ad is invalidated!");
                }
            }

            AdHolder adHolder = (AdHolder) holder;
            adHolder.adChoicesContainer.removeAllViews();

            if (ad != null) {

                adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                adHolder.tvAdBody.setText(ad.getAdBodyText());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.tvAdSponsoredLabel.setText("Sponsored");
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                adHolder.btnAdCallToAction.setVisibility(
                        ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                AdOptionsView adOptionsView = new AdOptionsView(mActivity, ad, adHolder.nativeAdLayout);
                adHolder.adChoicesContainer.addView(adOptionsView, 0);

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adHolder.tvAdTitle);
                clickableViews.add(adHolder.tvAdBody);
                clickableViews.add(adHolder.tvAdSocialContext);
                clickableViews.add(adHolder.tvAdSponsoredLabel);
                clickableViews.add(adHolder.ivAdIcon);
                clickableViews.add(adHolder.mvAdMedia);
                clickableViews.add(adHolder.btnAdCallToAction);
                ad.registerViewForInteraction(
                        adHolder.nativeAdLayout, adHolder.mvAdMedia, adHolder.ivAdIcon, clickableViews);
            }
        } else {
            PostHolder postHolder = (PostHolder) holder;

            int index = position - (position / AD_DISPLAY_FREQUENCY);

            Server postItem = mPostItems.get(index);
            postHolder.serverCountry.setText(postItem.getCountry());
            Glide.with(mActivity)
                    .load(postItem.getFlagUrl())
                    .into(postHolder.serverIcon);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectListener.onSelected(mPostItems.get(position));
                    Log.v("Kabila",mPostItems.get(position).getCountry());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems.size() + mAdItems.size();
    }

    private static class PostHolder extends RecyclerView.ViewHolder {

        ImageView serverIcon;
        TextView serverCountry;

        public PostHolder(@NonNull View itemView)
        {
            super(itemView);
            serverIcon = itemView.findViewById(R.id.flag);
            serverCountry = itemView.findViewById(R.id.countryName);
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;
        MediaView mvAdMedia;
        MediaView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdHolder(NativeAdLayout adLayout) {
            super(adLayout);

            nativeAdLayout = adLayout;
            mvAdMedia = adLayout.findViewById(R.id.native_ad_media);
            tvAdTitle = adLayout.findViewById(R.id.native_ad_title);
            tvAdBody = adLayout.findViewById(R.id.native_ad_body);
            tvAdSocialContext = adLayout.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = adLayout.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = adLayout.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = adLayout.findViewById(R.id.native_ad_icon);
            adChoicesContainer = adLayout.findViewById(R.id.ad_choices_container);
        }
    }

    public interface OnSelectListener {
        void onSelected(Server server);
    }

    public void setOnSelectListener(NativeAdRecyclerAdapter.OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
