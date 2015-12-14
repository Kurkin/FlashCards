package com.ifmo.kurkin.flashcards;

import java.io.Serializable;
import java.util.EnumMap;

public class Category implements Serializable {

    int id;
    EnumMap<Language, String> title;

    public Category(int id, EnumMap<Language, String> title) {
        this.id = id;
        this.title = title;
    }

    public Category(EnumMap<Language, String> title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle(Language language) {
        return title.get(language);
    }

    public void setTitle(Language language, String title) {
        this.title.put(language, title);
    }
}
