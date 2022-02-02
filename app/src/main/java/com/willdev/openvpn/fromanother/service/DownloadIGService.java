package com.willdev.openvpn.fromanother.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.willdev.openvpn.R;
import com.willdev.openvpn.database.DatabaseHandler;
import com.willdev.openvpn.fromanother.util.util.Method;

import java.io.File;
import java.io.IOException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DownloadIGService extends Service {

    private DatabaseHandler db;
    private RemoteViews rv;
    private Thread thread;
    private Handler handler;
    private OkHttpClient client;
    private int NOTIFICATION_ID = 111;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private static final String CANCEL_TAG = "c_dig";
    private String NOTIFICATION_CHANNEL_ID = "download_ig";
    public static final String ACTION_STOP = "com.dig.action.STOP";
    public static final String ACTION_START = "com.dig.action.START";
    private String id, downloadUrl, file_path, file_name, status_type;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = new DatabaseHandler(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.logo)
                .setTicker(getResources().getString(R.string.downloading))
                .setWhen(System.currentTimeMillis())
                .setOnlyAlertOnce(true);

        rv = new RemoteViews(getPackageName(), R.layout.my_custom_notification);
        rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
        rv.setProgressBar(R.id.progress, 100, 0, false);
        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.downloading) + " " + "(0%)");

        Intent closeIntent = new Intent(this, DownloadIGService.class);
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
            public boolean handleMessage(@NonNull Message msg) {
                int progress = Integer.parseInt(msg.obj.toString());
                switch (msg.what) {
                    case 1:
                        rv.setProgressBar(R.id.progress, 100, progress, false);
                        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.downloading) + " " + "(" + progress + " %)");
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

            id = intent.getStringExtra("id");
            downloadUrl = intent.getStringExtra("downloadUrl");
            file_path = intent.getStringExtra("file_path");
            file_name = intent.getStringExtra("file_name");
            status_type = intent.getStringExtra("status_type");

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
                if (db != null) {
                    if (!db.checkIdStatusDownload(id, status_type)) {
                        db.deleteStatusDownload(id, status_type);
                    }
                }
                if (file_path != null || file_name != null) {
                    File file = new File(file_path, file_name);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                stopForeground(false);
                stopSelf();
                Method.isDownload = true;
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
                        .url(downloadUrl)
                        .addHeader("Accept-Encoding", "identity")
                        .get()
                        .tag(CANCEL_TAG);

                Call call = client.newCall(builder.build());

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("TAG", "=============onFailure===============");
                        e.printStackTrace();
                        Log.d("error_downloading", e.toString());
                        Method.isDownload = true;
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e("TAG", "=============onResponse===============");
                        Log.e("TAG", "request headers:" + response.request().headers());
                        Log.e("TAG", "response headers:" + response.headers());
                        assert response.body() != null;
                        final ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                            @Override
                            public void onUIProgressStart(long totalBytes) {
                                super.onUIProgressStart(totalBytes);
                                Log.e("TAG", "onUIProgressStart:" + totalBytes);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
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
                                Method.isDownload = true;
                                showMedia(file_path, file_name);

                            }
                        });


                        try {

                            BufferedSource source = responseBody.source();
                            File outFile = new File(file_path + "/" + file_name);
                            BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                            source.readAll(sink);
                            sink.flush();
                            source.close();

                        } catch (Exception e) {
                            Log.d("show_data", e.toString());
                        }

                    }
                });
            }
        });
        thread.start();
    }

    public void showMedia(String file_path, String file_name) {
        try {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file_path + "/" + file_name},
                    null,
                    (path, uri) -> {

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
