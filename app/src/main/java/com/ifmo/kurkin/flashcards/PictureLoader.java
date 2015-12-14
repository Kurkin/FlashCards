package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PictureLoader extends AsyncTask<String, Void, Void> {

    private static final String API_KEY = "81ffa832353a3a6a10c1edc26e9b975f";
    private static final String TAG_SEARCH = "&tags=";
    private static final String TEXT_SEARCH = "&text=";
    private Context context;

    public PictureLoader(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String tag = params[0];

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
            String pictureUrl = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg";
            System.out.println(pictureUrl);
            connection.disconnect();



        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


//String tag = params[0];
//final String TAG_SEARCH = "&tags=";
//final String TEXT_SEARCH = "&text=";
//String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="
//        + API_KEY +TAG_SEARCH+ tag +"&per_page=1&page=1&format=json&nojsoncallback=1";
//System.out.println(url);
//        String farmId ="6";
//        String serverId = "5696";
//        String id = "23448104430";
//        String secret  = "ecf104f723";
//        String pictureUrl = "https://farm"+farmId+".staticflickr.com/"+serverId+"/"+id+"_"+secret+".jpg";
//        System.out.println(pictureUrl);
//{"photos":{"page":1,"pages":252234,"perpage":1,"total":"252234","photo":[{"id":"23123161434","owner":"89296905@N02","secret":"f5251a2b22","server":"5830","farm":6,"title":"In the nature","ispublic":1,"isfriend":0,"isfamily":0}]},"stat":"ok"}