package com.ifmo.kurkin.flashcards;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.ifmo.kurkin.flashcards.db.FlashCardDBHelper;
import com.ifmo.kurkin.flashcards.db.FlashCardImporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import javax.net.ssl.HttpsURLConnection;


public class DBCreator extends ProgressTaskActivity {

    private Activity activity;
    private  ImageLoader il = new ImageLoader(null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsEnabled(false);
        this.activity = this;
    }

    public void setDisplayHomeAsEnabled(boolean enabled) {
        getActionBar().setDisplayHomeAsUpEnabled(enabled);
    }

    public void die() {
        onBackPressed();
    }

    @Override
    protected ProgressTask createTask() {
        return new DownloadTask(this);
    }

    private class DownloadTask extends ProgressTask {

        DownloadTask(ProgressTaskActivity activity) {
            super(activity);
        }

        private ArrayList<ArrayList<String>> main;
        private SQLiteDatabase db;
        private static final String API_KEY = "81ffa832353a3a6a10c1edc26e9b975f";
        private static final String TAG_SEARCH = "&tags=";
        private static final String TEXT_SEARCH = "&text=";
        private static final String EXTRA_CARDS = "&extras=url_q";
        private String EXTRA = "";
        private int totalCount = 0;

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            die();
        }

        @Override
        protected void runTask() throws IOException {
            long start = System.currentTimeMillis();
            String json = jsonImport(appContext, "main.json");
            JsonFactory factory = new JsonFactory();
            JsonParser parser = null;
            try {
                parser = factory.createParser(json);
                readMain(parser);
                try {
                    appContext.deleteDatabase(FlashCardDBHelper.DB_FILE_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File folder = new File(Environment.getExternalStorageDirectory() +
                        "/.flashcards/");
                folder.delete();
                db = FlashCardDBHelper.getInstance(appContext, main).getWritableDatabase();
                FlashCardImporter flashCardImporter;
                EXTRA = EXTRA_CARDS;
                parser.close();
                int it = -1;
                for (int i = 0; i < main.size(); i++) {
                    String name = main.get(i).get(0);
                    json = jsonImport(appContext, name + ".json");
                    parser = factory.createParser(json);
                    flashCardImporter = new FlashCardImporter(db, name);
                    db.beginTransaction();
                    parser.nextToken();
                    int cardNumber = 0;
                    while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                        it++;
                        cardNumber++;
                        int newProgress = 100 * it / totalCount;
                        System.out.println("initializing, " + newProgress + " %");
                        this.onProgressChanged(newProgress);
                        parser.nextToken();
                        ArrayList<String> values = new ArrayList<>();
                        values.add(parser.getCurrentName());
                        while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                            parser.nextToken();
                            values.add(parser.getText());
                        }
                        values.add(il.imageLoad(values.get(1)));
                        flashCardImporter.insertCard(values);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    parser.close();
                }

                CardList cardList = new CardList(appContext);
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
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        try {
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (parser != null) {
                    parser.close();
                }
            }
            System.out.println("init time:" + (System.currentTimeMillis() - start));
        }

        void readMain(JsonParser parser) throws IOException {
            main = new ArrayList<>();
            parser.nextToken();
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                System.out.println("table number is " + parser.getCurrentName());
                parser.nextToken();
                main.add(readColumn(parser));
            }
        }

        ArrayList<String> readColumn(JsonParser parser) throws IOException {
            ArrayList<String> sub = new ArrayList<>();
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
//                System.out.println(parser.getCurrentName());
                parser.nextToken();
                sub.add(parser.getText());
            }
            sub.add(il.imageLoad(sub.get(1)));
            totalCount += Integer.parseInt(sub.get(4));
            return sub;

        }
    }
    protected static String jsonImport(Context appContext, String path) {
        InputStream is = null;
        try {
            is = appContext.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
