package com.nagendra.audios;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="atozmp3";
    private static final String TABLE_ALBUMBS="telugusongs";

    public static  final String KEY_ID="_id";
    public static  final String KEY_NAME="name";
    public static  final String KEY_LINK="link";

    ContextWrapper cw;
    // path to /data/data/yourapp/app_data/imageDir
    File directory;


    public DatabaseHelper(Context context){

        super(context,context.getDir("albums", Context.MODE_PRIVATE).getAbsolutePath()+"/"+DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALBUMS_TABLE=" CREATE TABLE "+TABLE_ALBUMBS+"("
                +KEY_ID+" INTEGER PRIMARY KEY, "
                +KEY_NAME+" TEXT, "
                +KEY_LINK+" TEXT)";
        db.execSQL(CREATE_ALBUMS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ TABLE_ALBUMBS);
        onCreate(db);
    }
    // Add an album to the database that can used while getting the album link when user selects an album
    public void addAlbum(String name,String link) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_LINK, link);

        db.insert(TABLE_ALBUMBS, null, values);


    }

    // Get album link when an user selects an album
    public Cursor fetchAlbum(String name) {
        SQLiteDatabase database=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+ TABLE_ALBUMBS + " WHERE " + KEY_NAME + " = '" + name + "'";
        Cursor mCursor=database.rawQuery(selectQuery,null);
        if(mCursor!=null)
            mCursor.moveToFirst();
        return mCursor;
    }

}
