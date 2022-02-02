package com.willdev.openvpn.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "status";


    private static final String TABLE_NAME = "download";


    private static final String ID = "auto_id";
    private static final String KEY_STATUS_ID = "status_id";
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_STATUS_NAME = "status_name";
    private static final String KEY_STATUS_IMAGE_S = "status_image_s";
    private static final String KEY_STATUS_IMAGE_B = "status_image_b";
    private static final String KEY_VIDEO_URI = "video_uri";
    private static final String KEY_GIF_URI = "gif_uri";
    private static final String KEY_STATUS_TYPE = "status_type";
    private static final String KEY_STATUS_TYPE_LAYOUT = "status_type_layout";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DOWNLOAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STATUS_ID + " TEXT,"
                + KEY_STATUS_NAME + " TEXT," + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_STATUS_IMAGE_S + " TEXT," + KEY_STATUS_IMAGE_B + " TEXT,"
                + KEY_VIDEO_URI + " TEXT," + KEY_GIF_URI + " TEXT,"
                + KEY_STATUS_TYPE + " TEXT," + KEY_STATUS_TYPE_LAYOUT + " TEXT"
                + ")";
        db.execSQL(CREATE_DOWNLOAD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }






    public boolean checkIdStatusDownload(String id, String type) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_STATUS_ID + "=" + id + " AND " + KEY_STATUS_TYPE + " = " + "'" + type + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 0;
    }


    public boolean deleteStatusDownload(String id, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_STATUS_ID + "=" + id + " AND " + KEY_STATUS_TYPE + " = " + "'" + type + "'", null) > 0;
    }



}
