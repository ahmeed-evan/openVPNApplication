package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class AboutUsList implements Serializable {

    private String app_name;
    private String privacy_policy_url;
    private String publisher_id;
    private String banner_ad_type;
    private String banner_ad_id;
    private String interstitial_ad_type;
    private String interstitial_ad_id;
    private String interstitial_ad_click;
    private String rewarded_video_ads_id;
    private String rewarded_video_click;
    private String spinner_opt;
    private String app_update_status;
    private String app_update_desc;
    private String app_redirect_url;
    private String cancel_update_status;

    private int app_new_version;
    private boolean banner_ad;
    private boolean interstitial_ad;
    private boolean rewarded_video_ads;

    public AboutUsList(String app_name, String privacy_policy_url, String publisher_id, String banner_ad_type, String banner_ad_id, String interstitial_ad_type, String interstitial_ad_id, String interstitial_ad_click, String rewarded_video_ads_id, String rewarded_video_click, String spinner_opt, String app_update_status, String app_update_desc, String app_redirect_url, String cancel_update_status, int app_new_version, boolean banner_ad, boolean interstitial_ad, boolean rewarded_video_ads) {
        this.app_name = app_name;
        this.privacy_policy_url = privacy_policy_url;
        this.publisher_id = publisher_id;
        this.banner_ad_type = banner_ad_type;
        this.banner_ad_id = banner_ad_id;
        this.interstitial_ad_type = interstitial_ad_type;
        this.interstitial_ad_id = interstitial_ad_id;
        this.interstitial_ad_click = interstitial_ad_click;
        this.rewarded_video_ads_id = rewarded_video_ads_id;
        this.rewarded_video_click = rewarded_video_click;
        this.spinner_opt = spinner_opt;
        this.app_update_status = app_update_status;
        this.app_update_desc = app_update_desc;
        this.app_redirect_url = app_redirect_url;
        this.cancel_update_status = cancel_update_status;
        this.app_new_version = app_new_version;
        this.banner_ad = banner_ad;
        this.interstitial_ad = interstitial_ad;
        this.rewarded_video_ads = rewarded_video_ads;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getPrivacy_policy_url() {
        return privacy_policy_url;
    }

    public void setPrivacy_policy_url(String privacy_policy_url) {
        this.privacy_policy_url = privacy_policy_url;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getBanner_ad_type() {
        return banner_ad_type;
    }

    public void setBanner_ad_type(String banner_ad_type) {
        this.banner_ad_type = banner_ad_type;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public void setBanner_ad_id(String banner_ad_id) {
        this.banner_ad_id = banner_ad_id;
    }

    public String getInterstitial_ad_type() {
        return interstitial_ad_type;
    }

    public void setInterstitial_ad_type(String interstitial_ad_type) {
        this.interstitial_ad_type = interstitial_ad_type;
    }

    public String getInterstitial_ad_id() {
        return interstitial_ad_id;
    }

    public void setInterstitial_ad_id(String interstitial_ad_id) {
        this.interstitial_ad_id = interstitial_ad_id;
    }

    public String getInterstitial_ad_click() {
        return interstitial_ad_click;
    }

    public void setInterstitial_ad_click(String interstitial_ad_click) {
        this.interstitial_ad_click = interstitial_ad_click;
    }

    public String getRewarded_video_ads_id() {
        return rewarded_video_ads_id;
    }

    public void setRewarded_video_ads_id(String rewarded_video_ads_id) {
        this.rewarded_video_ads_id = rewarded_video_ads_id;
    }

    public String getRewarded_video_click() {
        return rewarded_video_click;
    }

    public void setRewarded_video_click(String rewarded_video_click) {
        this.rewarded_video_click = rewarded_video_click;
    }

    public String getSpinner_opt() {
        return spinner_opt;
    }

    public void setSpinner_opt(String spinner_opt) {
        this.spinner_opt = spinner_opt;
    }

    public String getApp_update_status() {
        return app_update_status;
    }

    public void setApp_update_status(String app_update_status) {
        this.app_update_status = app_update_status;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public void setApp_update_desc(String app_update_desc) {
        this.app_update_desc = app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public void setApp_redirect_url(String app_redirect_url) {
        this.app_redirect_url = app_redirect_url;
    }

    public String getCancel_update_status() {
        return cancel_update_status;
    }

    public void setCancel_update_status(String cancel_update_status) {
        this.cancel_update_status = cancel_update_status;
    }

    public int getApp_new_version() {
        return app_new_version;
    }

    public void setApp_new_version(int app_new_version) {
        this.app_new_version = app_new_version;
    }

    public boolean isBanner_ad() {
        return banner_ad;
    }

    public void setBanner_ad(boolean banner_ad) {
        this.banner_ad = banner_ad;
    }

    public boolean isInterstitial_ad() {
        return interstitial_ad;
    }

    public void setInterstitial_ad(boolean interstitial_ad) {
        this.interstitial_ad = interstitial_ad;
    }

    public boolean isRewarded_video_ads() {
        return rewarded_video_ads;
    }

    public void setRewarded_video_ads(boolean rewarded_video_ads) {
        this.rewarded_video_ads = rewarded_video_ads;
    }
}
