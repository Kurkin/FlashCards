package com.ifmo.kurkin.flashcards;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.ifmo.kurkin.flashcards.db.FlashCardDBHelper;
import com.ifmo.kurkin.flashcards.db.FlashCardImporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;


public class DBCreator extends AsyncTask<Void, Void, Void> {

    private ArrayList<ArrayList<String>> main;
    private SQLiteDatabase db;
    private Context context;

    public DBCreator(Context context) {
        this.context = context;
    }


    void readMain(JsonParser parser) {
        main = new ArrayList<>();
        try {
            parser.nextToken();
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                parser.nextToken();
                main.add(readColumn(parser));
            }
        } catch (IOException e) {

        }

    }

    ArrayList<String> readColumn(JsonParser parser) {
        ArrayList<String> sub = new ArrayList<>();
        try {
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                parser.nextToken();
                sub.add(parser.getText());
            }
        } catch (IOException e) {

        }
        return sub;
    }

    String jsonImport(String path){
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        String json = jsonImport("main.json");
        JsonFactory factory = new JsonFactory();
        try {
            JsonParser parser = factory.createParser(json);
            readMain(parser);
            db = FlashCardDBHelper.getInstance(context, main).getWritableDatabase();
            FlashCardImporter flashCardImporter;
            parser.close();
            for (int i = 0; i < main.size(); i++) {
                String name = main.get(i).get(0);
                json = jsonImport(name + ".json");
                parser = factory.createParser(json);
                flashCardImporter = new FlashCardImporter(db, name);
                db.beginTransaction();
                parser.nextToken();
                while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                    parser.nextToken();
                    ArrayList<String> values = new ArrayList<>();
                    values.add(parser.getCurrentName());
                    while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                        parser.nextToken();
                        values.add(parser.getText());
                    }
                    flashCardImporter.insertCard(values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                parser.close();
            }

            CardList cardList = new CardList(context);
            Cursor cursor = null;
            Cursor subCursor = null;
            try {
                cursor = cardList.getAllCategories();
            } finally {
                if (cursor != null) {
                    cursor.moveToFirst();
                    for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                        try {
                            subCursor = cardList.getCards(cursor);
                        } finally {
                            if (subCursor != null) {
                                subCursor.moveToFirst();
                                for (; !subCursor.isAfterLast(); subCursor.moveToNext()) {
                                    System.out.println(new Card(subCursor));
                                }
                                try {
                                    subCursor.close();
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    try {
                        cursor.close();
                    } catch (Exception ignored) {}
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class PictureLoader  extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

}
