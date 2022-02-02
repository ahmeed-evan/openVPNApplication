package com.willdev.openvpn.fromanother.util.util;

//import com.onesignal.NotificationExtenderService;
//import com.onesignal.OSNotificationReceivedResult;


public class NotificationExtenderExample  { /*

    private String NOTIFICATION_CHANNEL_ID;
    private String message, bigPicture, title, url, id, type, status_type, title_name;

    @Override
    protected boolean onNotificationProcessing(@NotNull OSNotificationReceivedResult receivedResult) {

        NOTIFICATION_CHANNEL_ID = getResources().getString(R.string.app_name);

        title = receivedResult.payload.title;
        message = receivedResult.payload.body;
        bigPicture = receivedResult.payload.bigPicture;

        try {
            url = receivedResult.payload.additionalData.getString("external_link");
            type = receivedResult.payload.additionalData.getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            switch (type) {
                case "single_status":
                    id = receivedResult.payload.additionalData.getString("id");
                    status_type = receivedResult.payload.additionalData.getString("status_type");
                    title_name = receivedResult.payload.additionalData.getString("title");
                    break;
                case "category":
                    id = receivedResult.payload.additionalData.getString("id");
                    title_name = receivedResult.payload.additionalData.getString("title");
                    break;
                case "account_status":
                    id = receivedResult.payload.additionalData.getString("id");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification();
        return true;
    }

    @SuppressLint("WrongConstant")
    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (!url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", type);
            if (type.equals("account_status") || type.equals("single_status") || type.equals("category")) {
                intent.putExtra("id", id);
            }
            if (type.equals("single_status")) {
                intent.putExtra("status_type", status_type);
                intent.putExtra("title", title_name);
            }
            if (type.equals("category")) {
                intent.putExtra("title", title_name);
            }
        }

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Random random_code = new Random();
        int code = random_code.nextInt(9999 - 1000) + 1000;

        PendingIntent contentIntent = PendingIntent.getActivity(this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSound(uri)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(Color.RED, 800, 800);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigPicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigPicture)).setSummaryText(message));
        } else {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(m, mBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
        }
        return R.drawable.ic_stat_onesignal_default;
    }

    private int getColour() {
        return getResources().getColor(R.color.icon_nf);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {

            return null;
        }
    }*/
}