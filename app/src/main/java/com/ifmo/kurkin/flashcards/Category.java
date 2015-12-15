package com.ifmo.kurkin.flashcards;

import java.io.Serializable;
import java.util.EnumMap;

public class Category implements Serializable {

    int id;
    String title;

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
