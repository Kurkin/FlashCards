package com.ifmo.kurkin.flashcards;

public class TranslationToWordActivity extends FourVariantsCardActivity {

    @Override
    Language getTitleLanguage() {
        return Preferences.NATIVE_LANGUAGE;
    }

    @Override
    Language getQuestionableLanguage() {
        return Preferences.LEARNING_LANGUAGE;
    }

    @Override
    String getImageSubFolder() {
        return "";
    }
}
