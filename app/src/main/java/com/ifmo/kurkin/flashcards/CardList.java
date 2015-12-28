package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.ifmo.kurkin.flashcards.db.FlashCardContract;

import java.io.IOException;
import java.util.List;

public class CardList {

    private SQLiteDatabase db;
    private String ID = FlashCardContract.FlashCard.ID;
    private Context context;

    public CardList(Context context) {
        db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("cards.db"), null /*factory*/);
        this.context = context;
    }

    public Cursor getCards(Cursor cursor) {
        return db.query(cursor.getString(1), new String[]{"*"}, null, null, null, null, null);
    }

    public Card getCard(int categoryId, int cardId) {
        Cursor auxCursor = db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
        auxCursor.moveToFirst();
        Cursor cardCursor = db.query(auxCursor.getString(1), new String[]{"*"}, ID + "=?", new String[]{Integer.toString(cardId)}, null, null, null);
        auxCursor.close();
        cardCursor.moveToFirst();
        Card result = new Card(cardCursor);
        cardCursor.close();
        return result;
    }

    public String getCategoryName(int categoryId) {
        Cursor cursor = null;
        try {
            cursor = getCategoryCursor(categoryId);
            cursor.moveToFirst();
            return cursor.getString(1);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
    return true, if database is valid, otherwise return false
    */
   public boolean isValid() {
        Cursor cursorMain = null;
        boolean ans = true;
        try {
            cursorMain = getAllCategories();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursorMain != null) {
                cursorMain.moveToFirst();
                for (; !cursorMain.isAfterLast(); cursorMain.moveToNext()) {
                    int expectedCount = cursorMain.getInt(5);
                    int realCount = 0;
                    Cursor tableCursor = null;
                    try {
                        tableCursor = getCards(cursorMain);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ans = false;
                    } finally {
                        if (tableCursor != null) {
                            tableCursor.moveToFirst();
                            for (; !tableCursor.isAfterLast(); tableCursor.moveToNext()) {
                                realCount++;
                            }
                            tableCursor.close();
                        }
                    }
                    System.out.println("expected count: " + expectedCount + " and real cont is: " + realCount);
                    if (expectedCount != realCount) {
                        ans = false;
                        break;
                    }
                }
                cursorMain.close();
            } else {
                ans = false;
            }
        }
        return ans;
    }

    public Cursor getAllCategories() {
        return db.query("main", new String[]{"*"}, null, null, null, null, null);
    }

    public Cursor getCategoryCursor(int categoryId) {
        return db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
    }

    public Cursor getCategoryTypeCursor(String categoryType) {
        return db.query("main", new String[]{"*"}, FlashCardContract.FlashCard.TYPE + "=?", new String[]{categoryType}, null, null, null);
    }

    public Cursor getCategory(String name) {
        return db.query(name, new String[]{"*"}, null, null, null, null, null);
    }

    public Cursor getCategory(int categoryId) {
        Cursor auxCursor = db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
        auxCursor.moveToFirst();
        Cursor result = db.query(auxCursor.getString(1), new String[]{"*"}, null, null, null, null, null);
        auxCursor.close();
        return result;
    }

//    TODO: Throw exception when ID is wrong

    public int getCategoryRating(int categoryId) {
        Cursor subCursor = db.query("main", new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryId)}, null, null, null);
        subCursor.moveToFirst();
        Cursor auxCursor = db.query(subCursor.getString(1), new String[]{"*"}, null, null, null, null, null);
        auxCursor.moveToFirst();
        int result = 0;
        for (; !auxCursor.isAfterLast(); auxCursor.moveToNext()) {
            result += new Card(auxCursor).rating;
        }
//        System.out.println("result is:" + result);
        auxCursor.close();
        return result;
    }

    private void updateRating(String table, int id, int newValue) {
        Cursor cursor = null;
        try {
            cursor = db.query(table, new String[]{"*"}, ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
        } finally {
            if (cursor != null) {
                cursor.moveToFirst();
                String strSQL = "UPDATE " + table + " SET rating = " + (Integer.toString(newValue)) + " WHERE " + ID + " = " + id;
                db.execSQL(strSQL);
                try {
                    cursor.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
    public void updatePicture(int idTable, int id, String newPath) {
        String table = getCategoryName(idTable);
        Cursor cursor = null;
        try {
            cursor = db.query(table, new String[]{"*"}, ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
        } finally {
            if (cursor != null) {
                cursor.moveToFirst();
                String strSQL = "UPDATE " + table + " SET image = " + (newPath) + " WHERE " + ID + " = " + id;
                db.execSQL(strSQL);
                try {
                    cursor.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void incRating(int categoryID, int cardId) {
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        incRating(categoryCursor.getString(1), cardId);
        categoryCursor.close();
    }
//    TODO: catch exception when some ID is wrong

    public void incRating(String table, int cardId) {
        updateRating(table, cardId, 1);
    }

    public void decRating(int categoryID, int cardId) {
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        decRating(categoryCursor.getString(1), cardId);
        categoryCursor.close();
    }

    public void decRating(String table, int cardId) {
        updateRating(table, cardId, 0);
    }

    public void resetCategoryRating(int categoryID) {
        Cursor categoryCursor = getCategoryCursor(categoryID);
        categoryCursor.moveToFirst();
        String table = categoryCursor.getString(1);
        categoryCursor.close();
        Cursor cursor = null;
        try {
            cursor = db.query(table, new String[]{"*"}, ID + "=?", new String[]{Integer.toString(categoryID)}, null, null, null);
        } finally {
            if (cursor != null) {
                cursor.moveToFirst();
                String strSQL = "UPDATE " + table + " SET rating = " + (Integer.toString(0));
                db.execSQL(strSQL);
                try {
                    cursor.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


}