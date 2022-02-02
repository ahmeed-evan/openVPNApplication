package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class UploadControlList implements Serializable {

    private String video_ad, gif_ad, quote_ad, image_ad, video_file_size, video_file_duration, image_file_size, gif_file_size, video_msg,video_size_msg, video_duration_msg, img_size_msg, gif_size_msg;

    public UploadControlList(String video_ad, String gif_ad, String quote_ad, String image_ad, String video_file_size, String video_file_duration, String image_file_size, String gif_file_size, String video_msg,String video_size_msg, String video_duration_msg, String img_size_msg, String gif_size_msg) {
        this.video_ad = video_ad;
        this.gif_ad = gif_ad;
        this.quote_ad = quote_ad;
        this.image_ad = image_ad;
        this.video_file_size = video_file_size;
        this.video_file_duration = video_file_duration;
        this.image_file_size = image_file_size;
        this.gif_file_size = gif_file_size;
        this.video_msg = video_msg;
        this.video_size_msg = video_size_msg;
        this.video_duration_msg = video_duration_msg;
        this.img_size_msg = img_size_msg;
        this.gif_size_msg = gif_size_msg;
    }

    public String getVideo_ad() {
        return video_ad;
    }

    public void setVideo_ad(String video_ad) {
        this.video_ad = video_ad;
    }

    public String getGif_ad() {
        return gif_ad;
    }

    public void setGif_ad(String gif_ad) {
        this.gif_ad = gif_ad;
    }

    public String getQuote_ad() {
        return quote_ad;
    }

    public void setQuote_ad(String quote_ad) {
        this.quote_ad = quote_ad;
    }

    public String getImage_ad() {
        return image_ad;
    }

    public void setImage_ad(String image_ad) {
        this.image_ad = image_ad;
    }

    public String getVideo_file_size() {
        return video_file_size;
    }

    public void setVideo_file_size(String video_file_size) {
        this.video_file_size = video_file_size;
    }

    public String getVideo_file_duration() {
        return video_file_duration;
    }

    public void setVideo_file_duration(String video_file_duration) {
        this.video_file_duration = video_file_duration;
    }

    public String getImage_file_size() {
        return image_file_size;
    }

    public void setImage_file_size(String image_file_size) {
        this.image_file_size = image_file_size;
    }

    public String getGif_file_size() {
        return gif_file_size;
    }

    public void setGif_file_size(String gif_file_size) {
        this.gif_file_size = gif_file_size;
    }

    public String getVideo_msg() {
        return video_msg;
    }

    public void setVideo_msg(String video_msg) {
        this.video_msg = video_msg;
    }

    public String getVideo_size_msg() {
        return video_size_msg;
    }

    public void setVideo_size_msg(String video_size_msg) {
        this.video_size_msg = video_size_msg;
    }

    public String getVideo_duration_msg() {
        return video_duration_msg;
    }

    public void setVideo_duration_msg(String video_duration_msg) {
        this.video_duration_msg = video_duration_msg;
    }

    public String getImg_size_msg() {
        return img_size_msg;
    }

    public void setImg_size_msg(String img_size_msg) {
        this.img_size_msg = img_size_msg;
    }

    public String getGif_size_msg() {
        return gif_size_msg;
    }

    public void setGif_size_msg(String gif_size_msg) {
        this.gif_size_msg = gif_size_msg;
    }
}
