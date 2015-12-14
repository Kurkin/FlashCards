package com.ifmo.kurkin.flashcards.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;


public class FlashCardImporter {

    private SQLiteDatabase db;
    final String NAME;

    public FlashCardImporter(SQLiteDatabase db, String name) {
        this.db = db;
        this.NAME = name;
    }

    public void insertCard (ArrayList<String> params){
        final ContentValues values = new ContentValues();
        values.put(FlashCardContract.FlashCardColumns.ID, Integer.parseInt(params.get(0)));
        values.put(FlashCardContract.FlashCardColumns.LANGUAGE1, params.get(1));
        values.put(FlashCardContract.FlashCardColumns.LANGUAGE2, params.get(2));
        values.put(FlashCardContract.FlashCardColumns.IMAGE, params.get(3));
        values.put(FlashCardContract.FlashCardColumns.RATING, 0);
        db.insert(NAME, null /*nullColumnHack not needed*/, values);
    }
}
