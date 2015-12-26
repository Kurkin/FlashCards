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

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import javax.net.ssl.HttpsURLConnection;


public class DBCreator extends ProgressTaskActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsEnabled(false);
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
                    while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                        it++;
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

        public String imageLoad(String tag) {
            String path = "";
            try {
                HttpsURLConnection connection = null;
                JsonParser parser = null;
                String farmId = "";
                String serverId = "";
                String id = "";
                String secret = "";
                try {
                    URL url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="
                            + API_KEY + TAG_SEARCH + tag.replace(" ", "_") + EXTRA + "&per_page=1&page=1&format=json&nojsoncallback=1");
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.connect();
                    InputStream res = null;
                    res = connection.getInputStream();
                    JsonFactory factory = new JsonFactory();
                    parser = factory.createParser(res);
                    JsonToken token = parser.nextToken();
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (parser != null) {
                        parser.close();
                    }
                }
                URL url = new URL("https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg");
                InputStream input = null;
                try {
                    input = url.openStream();
                    File storagePath = Environment.getExternalStorageDirectory();
                    path = storagePath + "/.flashcards/" + tag.replace(" ", "_") + id + ".jpg";
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(storagePath + "/.flashcards/" + tag.replace(" ", "_") + id + ".jpg");
                        byte[] buffer = new byte[16000];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        if (output != null) {
                            output.close();
                        }
                    }
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        }

        void readMain(JsonParser parser) throws IOException {
            main = new ArrayList<>();
            parser.nextToken();
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
//                if (parser.getCurrentName().equals("tables")){
//                    parser.nextToken();
//                    parser.nextToken();
//                }
                System.out.println("table number is " + parser.getCurrentName());
                parser.nextToken();
                main.add(readColumn(parser));
            }
        }

        ArrayList<String> readColumn(JsonParser parser) throws IOException {
            ArrayList<String> sub = new ArrayList<>();
            while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                parser.nextToken();
                sub.add(parser.getText());
            }
            sub.add(imageLoad(sub.get(1)));
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
