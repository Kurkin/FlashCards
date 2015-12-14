package com.ifmo.kurkin.flashcards.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.ifmo.kurkin.flashcards.db.util.DatabaseCorruptionHandler;

import java.io.File;
import java.util.ArrayList;

public class FlashCardDBHelper extends SQLiteOpenHelper {

    private static final String DB_FILE_NAME = "cards.db";

    private static final int DB_VERSION_1 = 1;

    private ArrayList<ArrayList<String>> main;

    private static volatile FlashCardDBHelper instance;

    public static FlashCardDBHelper getInstance(Context context, ArrayList<ArrayList<String>> main) {
        if (instance == null) {
            synchronized (FlashCardDBHelper.class) {
                if (instance == null) {
                    instance = new FlashCardDBHelper(context, main);
                }
            }
        }
        return instance;
    }

    private final Context context;

    public FlashCardDBHelper(Context context, ArrayList<ArrayList<String>> main) {
        super(context, DB_FILE_NAME, null /*factory*/, DB_VERSION_1,
                new DatabaseCorruptionHandler(context, DB_FILE_NAME));
        this.main = main;
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FlashCardContract.FlashCard.CREATE_MAIN);
        db.beginTransaction();
        for (int i = 0; i < main.size(); i++) {
            ArrayList<String> line = main.get(i);
            final ContentValues values = new ContentValues();
            values.put(FlashCardContract.FlashCard.ID, i);
            values.put(FlashCardContract.FlashCard.NAME, line.get(0));
            values.put(FlashCardContract.FlashCard.LANGUAGE1, line.get(1));
            values.put(FlashCardContract.FlashCard.LANGUAGE2, line.get(2));
            values.put(FlashCardContract.FlashCard.TYPE, line.get(3));
            values.put(FlashCardContract.FlashCard.IMAGE, line.get(4));
            db.insert("main", null /*nullColumnHack not needed*/, values);
            db.execSQL(FlashCardContract.FlashCard.CREATE_TABLE(line.get(0)));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        System.out.println(context.getDatabasePath(DB_FILE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "onUpgrade: oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }

    private static final String LOG_TAG = "CardsDB";


}
