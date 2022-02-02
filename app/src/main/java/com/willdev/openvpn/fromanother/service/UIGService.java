package com.willdev.openvpn.fromanother.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Events;
import com.willdev.openvpn.fromanother.util.util.GlobalBus;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UIGService extends Service {

    private RemoteViews rv;
    private OkHttpClient client;
    private Thread thread;
    private Handler handler;
    private int NOTIFICATION_ID = 115;
    NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private static final String CANCEL_TAG = "c_uig";
    private String NOTIFICATION_CHANNEL_ID = "upload_ig";
    public static final String ACTION_START = "com.uig.action.START";
    public static final String ACTION_STOP = "com.uig.action.STOP";
    private String status_type, image_file, user_id, cat_id, lang_ids, image_tags, image_layout, image_title;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.logo)
                .setTicker(getResources().getString(R.string.ready_to_upload))
                .setWhen(System.currentTimeMillis())
                .setOnlyAlertOnce(true);

        rv = new RemoteViews(getPackageName(), R.layout.my_custom_notification);
        rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
        rv.setProgressBar(R.id.progress, 100, 0, false);
        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.upload_image) + " " + "(0%)");

        Intent closeIntent = new Intent(this, UIGService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
        rv.setOnClickPendingIntent(R.id.relativeLayout_nf, pcloseIntent);

        builder.setCustomContentView(rv);
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name);
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        startForeground(NOTIFICATION_ID, builder.build());

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                int progress = Integer.parseInt(message.obj.toString());
                switch (message.what) {
                    case 1:
                        rv.setProgressBar(R.id.progress, 100, progress, false);
                        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.upload_image) + " " + "(" + progress + " %)");
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                        break;
                    case 2:
                        stopForeground(false);
                        stopSelf();
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent.getAction() != null && intent.getAction().equals(ACTION_START)) {

            user_id = intent.getStringExtra("user_id");
            cat_id = intent.getStringExtra("cat_id");
            lang_ids = intent.getStringExtra("lang_ids");
            image_tags = intent.getStringExtra("image_tags");
            image_title = intent.getStringExtra("image_title");
            image_layout = intent.getStringExtra("image_layout");
            status_type = intent.getStringExtra("status_type");
            image_file = intent.getStringExtra("image_file");

            init();
        }
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            try {
                if (client != null) {
                    for (Call call : client.dispatcher().queuedCalls()) {
                        if (call.request().tag().equals(CANCEL_TAG))
                            call.cancel();
                    }
                    for (Call call : client.dispatcher().runningCalls()) {
                        if (call.request().tag().equals(CANCEL_TAG))
                            call.cancel();
                    }
                }
                if (handler != null) {
                    handler.removeCallbacks(thread);
                }
                if (thread != null) {
                    thread.interrupt();
                    thread = null;
                }
                Events.UploadFinish uploadFinish = new Events.UploadFinish("");
                GlobalBus.getBus().post(uploadFinish);
                stopForeground(false);
                stopSelf();
                Method.isUpload = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
        stopSelf();
    }

    public void init() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                Request.Builder builder = new Request.Builder()
                        .url(Constant.url)
                        .tag(CANCEL_TAG);

                File imageFile = new File(image_file);

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                bodyBuilder.setType(MultipartBody.FORM);
                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getApplicationContext()));
                jsObj.addProperty("method_name", "upload_img_gif_status");
                jsObj.addProperty("user_id", user_id);
                jsObj.addProperty("cat_id", cat_id);
                jsObj.addProperty("lang_ids", lang_ids);
                jsObj.addProperty("image_tags", image_tags);
                jsObj.addProperty("image_title", image_title);
                jsObj.addProperty("image_layout", image_layout);
                jsObj.addProperty("status_type", status_type);
                bodyBuilder.addFormDataPart("data", API.toBase64(jsObj.toString()));
                bodyBuilder.addFormDataPart("image_file", imageFile.getName(), RequestBody.create(null, imageFile));
                MultipartBody build = bodyBuilder.build();

                RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.e("TAG", "=============start===============");
                        Log.e("TAG", "numBytes:" + numBytes);
                        Log.e("TAG", "totalBytes:" + totalBytes);
                        Log.e("TAG", "percent:" + percent);
                        Log.e("TAG", "speed:" + speed);
                        Log.e("TAG", "============= end ===============");

                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = (int) (100 * percent) + "";
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
                        Log.e("TAG", "onUIProgressFinish:");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        msg.obj = 0 + "";
                        handler.sendMessage(msg);
                        Method.isUpload = true;
                        Events.UploadFinish uploadFinish = new Events.UploadFinish("");
                        GlobalBus.getBus().post(uploadFinish);
                    }
                });
                builder.post(requestBody);

                Call call = client.newCall(builder.build());

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "=============onFailure===============");
                        e.printStackTrace();
                        Method.isUpload = true;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("TAG", "=============onResponse===============");
                        Log.e("TAG", "request headers:" + response.request().headers());
                        Log.e("TAG", "response headers:" + response.headers());
                    }
                });
            }
        });
        thread.start();
    }

}
