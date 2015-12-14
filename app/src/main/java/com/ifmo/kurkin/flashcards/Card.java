package com.ifmo.kurkin.flashcards;

import android.database.Cursor;

public class Card {

    public int id, rating;
    public String lang1, lang2, picture;

    public Card(int id, String lang1, String lang2, String picture, int rating) {
        this.id = id;
        this.lang1 = lang1;
        this.lang2 = lang2;
        this.picture = picture;
        this.rating = rating;
    }

    public Card(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.lang1 = cursor.getString(1);
        this.lang2 = cursor.getString(2);
        this.picture = cursor.getString(3);
        this.rating = cursor.getInt(4);
    }

    public Card(int id, String lang1, String lang2, String picture) {
        new Card(id, lang1, lang2, picture, 0);
    }

    public String toString() {
        return id + " " + lang1 + "-" + lang2+" "+picture+" "+rating;
    }
}
