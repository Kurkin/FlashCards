package com.ifmo.kurkin.flashcards;

import android.os.AsyncTask;
import android.os.Environment;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Borys on 29-12-15.
 */
public class ImageLoader extends AsyncTask<String, Void, Void> {

    private static final String API_KEY = "81ffa832353a3a6a10c1edc26e9b975f";
    private static final String TAG_SEARCH = "&tags=";
    private static final String TEXT_SEARCH = "&text=";
    private static final String EXTRA_CARDS = "&extras=url_q";
    private String EXTRA = "";
    private UIActivity activity;

    ImageLoader(UIActivity activity){
        this.activity = activity;
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
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(3000);
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
            System.out.println(tag+"'s picute; "+" "+url);
            InputStream input = null;
            try {
                input = url.openStream();
                File storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                path = storagePath +  tag.replace(" ", "_") + id + ".jpg";
                OutputStream output = null;
                try {
                    output = new FileOutputStream(path);
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
        System.out.print("finished load picture; ");
        return path;
    }

    @Override
    protected Void doInBackground(String[] params) {
        imageLoad((String)params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        activity.updateUI();
    }
}
