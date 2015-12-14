package com.ifmo.kurkin.flashcards;

/**
 * Created by kurkin on 22.11.15.
 */
public enum Language {
    ENG {
        @Override
        public String getName() {
            return "en";
        }
    },
    FRA {
        @Override
        public String getName() {
            return "fr";
        }
    },
    RUS {
        @Override
        public String getName() {
            return "ru";
        }
    };

    public abstract String getName();
}
