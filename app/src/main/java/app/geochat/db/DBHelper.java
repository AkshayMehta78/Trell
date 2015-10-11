package app.geochat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.geochat.DesidimeApplication;

/**
 * Created by akshay on 28/7/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "desidime.db";
    private static final int DATABASE_VERSION = 9;
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
