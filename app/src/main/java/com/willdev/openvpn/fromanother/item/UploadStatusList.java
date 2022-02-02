package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class UploadStatusList implements Serializable {

    private String upload_name, upload_type;
    private int upload_image, upload_bg;

    public UploadStatusList(String upload_name, String upload_type, int upload_image, int upload_bg) {
        this.upload_name = upload_name;
        this.upload_type = upload_type;
        this.upload_image = upload_image;
        this.upload_bg = upload_bg;
    }

    public String getUpload_name() {
        return upload_name;
    }

    public void setUpload_name(String upload_name) {
        this.upload_name = upload_name;
    }

    public String getUpload_type() {
        return upload_type;
    }

    public void setUpload_type(String upload_type) {
        this.upload_type = upload_type;
    }

    public int getUpload_image() {
        return upload_image;
    }

    public void setUpload_image(int upload_image) {
        this.upload_image = upload_image;
    }

    public int getUpload_bg() {
        return upload_bg;
    }

    public void setUpload_bg(int upload_bg) {
        this.upload_bg = upload_bg;
    }
}
