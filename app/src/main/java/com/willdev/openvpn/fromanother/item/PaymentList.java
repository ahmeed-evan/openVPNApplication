package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class PaymentList implements Serializable {

private String id,mode_title;

    public PaymentList(String id, String mode_title) {
        this.id = id;
        this.mode_title = mode_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode_title() {
        return mode_title;
    }

    public void setMode_title(String mode_title) {
        this.mode_title = mode_title;
    }

}
