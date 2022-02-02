package com.willdev.openvpn.fromanother.util.util;

import android.os.Environment;

import com.willdev.openvpn.fromanother.item.AboutUsList;
import java.io.File;
import java.util.List;

public class Constant {

    public static final String My_api = "https://project.willdev.in/WILL_VPN/";


    public static String url = My_api + "api.php";

    public static String STATUS = "status";

    public static String video_upload_url = My_api + "api_video_upload.php";

    public static String tag = "ANDROID_REWARDS_APP";

    public static String status_path = "WhatsApp/Media/.Statuses";


    public static String mainFolderName = "/Video_Status/";

    public static String video_path = Environment.getExternalStorageDirectory() + mainFolderName + "Video/";

    public static String image_path = Environment.getExternalStorageDirectory() + mainFolderName + "Status_Image/";

    public static String download_status_path = Environment.getExternalStorageDirectory() + mainFolderName + "/status_saver/";

    public static String webTextLight = "#8b8b8b;";
    public static String webTextDark = "#FFFFFF;";

    public static String webLinkLight = "#0782C1;";
    public static String webLinkDark = "#0782C1;";

    public static String lightGallery = "#f20056";
    public static String darkGallery = "#000000";
    public static String progressBarLightGallery = "#f20056";
    public static String progressBarDarkGallery = "#FFFFFF";

    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW = 0;

    public static int REWARD_VIDEO_AD_COUNT = 0;
    public static int REWARD_VIDEO_AD_COUNT_SHOW = 0;

    public static AboutUsList aboutUsList;

    public static List<File> imageFilesList;
    public static List<File> videoFilesList;

    public static List<File> downloadImageFilesList;
    public static List<File> downloadVideoFilesList;
}
