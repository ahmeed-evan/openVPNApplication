package com.willdev.openvpn.fromanother.util.util;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class API {

    @SerializedName("sign")
    private String sign;
    @SerializedName("salt")
    private String salt;
    @SerializedName("package_name")
    private String package_name;

    public API(Activity activity) {
        String apiKey = "will_dev";
        salt = "" + getRandomSalt();
        sign = md5(apiKey + salt);
        package_name = activity.getApplication().getPackageName();
    }

    public API(Context context) {
        String apiKey = "will_dev";
        salt = "" + getRandomSalt();
        sign = md5(apiKey + salt);
        package_name = context.getApplicationContext().getPackageName();
    }

    private int getRandomSalt() {
        Random random = new Random();
        return random.nextInt(900);
    }

    private String md5(String input) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();


            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02x", messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

}
