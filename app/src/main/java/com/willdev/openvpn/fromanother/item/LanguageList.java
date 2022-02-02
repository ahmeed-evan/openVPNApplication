package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class LanguageList implements Serializable {

    private String language_id,language_name,language_image,language_image_thumb,is_selected;

    public LanguageList(String language_id, String language_name, String language_image, String language_image_thumb, String is_selected) {
        this.language_id = language_id;
        this.language_name = language_name;
        this.language_image = language_image;
        this.language_image_thumb = language_image_thumb;
        this.is_selected = is_selected;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public String getLanguage_image() {
        return language_image;
    }

    public void setLanguage_image(String language_image) {
        this.language_image = language_image;
    }

    public String getLanguage_image_thumb() {
        return language_image_thumb;
    }

    public void setLanguage_image_thumb(String language_image_thumb) {
        this.language_image_thumb = language_image_thumb;
    }

    public String getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(String is_selected) {
        this.is_selected = is_selected;
    }
}
