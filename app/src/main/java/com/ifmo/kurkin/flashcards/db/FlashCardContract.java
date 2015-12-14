package com.ifmo.kurkin.flashcards.db;

public final class FlashCardContract {

    public interface FlashCardColumns {

        String ID = "_id";

        String LANGUAGE1 = "lang1";

        String LANGUAGE2 = "lang2";

        String IMAGE = "image";

        String RATING = "rating";

        String NAME = "name";

        String TYPE = "type";
    }

    public static final class FlashCard implements FlashCardColumns {

        static final String CREATE_MAIN = "CREATE TABLE " + "main"
                + " ("
                + ID + " INTEGER PRIMARY KEY, "
                + NAME + " TEXT, "
                + LANGUAGE1 + " TEXT, "
                + LANGUAGE2 + " TEXT, "
                + TYPE + " TEXT, "
                + IMAGE + " TEXT)";

        public static String CREATE_TABLE(String name) {
            return "CREATE TABLE " + name
                    + " ("
                    + ID + " INTEGER PRIMARY KEY, "
                    + LANGUAGE1 + " TEXT, "
                    + LANGUAGE2 + " TEXT, "
                    + IMAGE + " TEXT, "
                    + RATING + " INTEGER)";
        }

    }

    private FlashCardContract() {
    }
}
