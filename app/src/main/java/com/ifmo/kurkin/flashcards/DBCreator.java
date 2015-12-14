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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import javax.net.ssl.HttpsURLConnection;


public class DBCreator extends ProgressTaskActivity {

    @Override
    protected ProgressTask createTask() {
        return new DownloadTask(this);
    }

    class DownloadTask extends ProgressTask {

        DownloadTask(ProgressTaskActivity activity) {
            super(activity);
        }

        private ArrayList<ArrayList<String>> main;
        private SQLiteDatabase db;
        private static final String API_KEY = "81ffa832353a3a6a10c1edc26e9b975f";
        private static final String TAG_SEARCH = "&tags=";
        private static final String TEXT_SEARCH = "&text=";

        @Override
        protected void runTask() throws IOException {
            long start = System.currentTimeMillis();
            String json = jsonImport("main.json");
            JsonFactory factory = new JsonFactory();

            try {
                JsonParser parser = factory.createParser(json);
                readMain(parser);
                db = FlashCardDBHelper.getInstance(appContext, main).getWritableDatabase();
                FlashCardImporter flashCardImporter;
                parser.close();
                for (int i = 0; i < main.size(); i++) {
                    int newProgress = 100 * i / main.size();
                    this.onProgressChanged(newProgress);
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
                        values.add(imageLoad(values.get(1)));
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
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                        try {
                            cursor.close();
                        } catch (Exception ignored) {
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("init time:" + (System.currentTimeMillis() - start));
        }

        public String imageLoad(String tag) {
            try {
                URL url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="
                        + API_KEY + TAG_SEARCH + tag + "&per_page=1&page=1&format=json&nojsoncallback=1");
                System.out.println(url);
                long time = System.currentTimeMillis();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                System.out.println("connect time:" + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                InputStream res = null;
                res = connection.getInputStream();

                System.out.println("get stream time:" + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                JsonFactory factory = new JsonFactory();
                JsonParser parser = factory.createParser(res);
                JsonToken token = parser.nextToken();

                String farmId = "";
                String serverId = "";
                String id = "";
                String secret = "";

                while (!token.equals(JsonToken.END_OBJECT)) {
                    token = parser.nextToken();
                    if (token.equals(JsonToken.FIELD_NAME)) {
                        token = parser.nextToken();
                        switch (parser.getCurrentName()) {
                            case "id":
                                id = parser.getText();
                                break;
                            case "server":
                                serverId = parser.getText();
                                break;
                            case "farm":
                                farmId = parser.getText();
                                break;
                            case "secret":
                                secret = parser.getText();
                                break;
                        }
                        System.out.println(parser.getCurrentName());
                        System.out.println(parser.getText());
                    }
                }
                connection.disconnect();
                url = new URL("https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg");
                InputStream input = url.openStream();

                File folder = new File(Environment.getExternalStorageDirectory() +
                        "/.flashcards/");
                if (!folder.exists()) {
                    System.out.println(folder.mkdir());
                }

                try {
                    File storagePath = Environment.getExternalStorageDirectory();
                    System.out.println(storagePath.toString());
                    OutputStream output = new FileOutputStream(storagePath + "/.flashcards/" + tag + id + ".jpg");
                    try {
                        byte[] buffer = new byte[16384];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        output.close();
                    }
                } finally {
                    input.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
                sub.add(imageLoad(sub.get(1)));
            } catch (IOException e) {

            }
            return sub;
        }

        String jsonImport(String path) {
            try {
                InputStream is = appContext.getAssets().open(path);
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

    }
}
