package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ifmo.kurkin.flashcards.db.FlashCardContract;

public class CardList {

    private SQLiteDatabase db;
    private String ID = FlashCardContract.FlashCard.ID;

    public CardList(Context context) {
        db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("cards.db"),null /*factory*/);
    }

    public Cursor getCards(Cursor cursor) {
        return db.query(cursor.getString(1), new String[]{"*"}, null, null, null, null, null);
    }

    public Card getCard(int categoryId, int cardId){
        Cursor auxCursor = db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
        auxCursor.moveToFirst();
        Cursor cardCursor =  db.query(auxCursor.getString(1), new String[]{"*"}, ID + "=?", new String[]{Integer.toString(cardId)}, null, null, null);
        auxCursor.close();
        cardCursor.moveToFirst();
        Card result = new Card(cardCursor);
        cardCursor.close();
        return result;
    }

    public String getCategoryName(int categoryId){
        Cursor cursor = null;
        try{
            cursor = getCategoryCursor(categoryId);
            cursor.moveToFirst();
            return cursor.getString(1);
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public Cursor getAllCategories() {
        return db.query("main", new String[]{"*"}, null, null, null, null, null);
    }

    public Cursor getCategoryCursor(int categoryId) {
        return db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
    }

    public Cursor getCategory(String name){
        return db.query(name, new String[]{"*"}, null, null, null, null, null);
    }

    public Cursor getCategory(int categoryId){
        Cursor auxCursor = db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
        auxCursor.moveToFirst();
        Cursor result = db.query(auxCursor.getString(1), new String[]{"*"}, null, null, null, null, null);
        auxCursor.close();
        return result;
    }

//    TODO: Throw exception when ID is wrong

    private void updateRating(String table, int id, int newValue) {
        Cursor cursor = null;
        try {
            cursor = db.query(table, new String[]{"*"}, ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
        } finally {
            if (cursor != null) {
                cursor.moveToFirst();
                int update = cursor.getInt(4) + newValue;
                if (newValue == 0) {
                    update = 0;
                }
                String strSQL = "UPDATE " + table + " SET rating = " + (Integer.toString(update)) + " WHERE " + ID + " = " + id;
                db.execSQL(strSQL);
                try {
                    cursor.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void incRating(int categoryID, int cardId){
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        incRating(categoryCursor.getString(1), cardId);
        categoryCursor.close();
    }
//    TODO: catch exception when some ID is wrong

    public void incRating(String table, int cardId) {
        updateRating(table, cardId, 1);
    }

    public void decRating(int categoryID, int cardId){
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        decRating(categoryCursor.getString(1), cardId);
        categoryCursor.close();
    }

    public void decRating(String table, int cardId) {
        updateRating(table, cardId, -1);
    }

    public void clearRating(int categoryID, int cardId){
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        clearRating(categoryCursor.getString(1), cardId);
        categoryCursor.close();
    }

    public void clearRating(String table, int cardId) {
        updateRating(table, cardId, 0);
    }
}