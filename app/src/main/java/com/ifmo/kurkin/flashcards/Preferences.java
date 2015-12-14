package com.ifmo.kurkin.flashcards;

import java.util.Locale;

/**
 * Created by kurkin on 22.11.15.
 */
public class Preferences {

    public static final Language NATIVE_LANGUAGE;
    public static Language LEARNING_LANGUAGE = Language.FRA;
    public static String pref_learning_language = "learning language";
    public static final String INTENT_CATEGORY = "category";

    static {
        Language lang = Language.ENG;
        for (Language l : Language.values()) {
            if (Locale.getDefault().getLanguage().equals(l.getName())) {
                lang = l;
            }
        }
        NATIVE_LANGUAGE = lang;
    }
}
