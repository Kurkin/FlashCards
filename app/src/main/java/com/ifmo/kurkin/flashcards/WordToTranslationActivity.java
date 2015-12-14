package com.ifmo.kurkin.flashcards;

public class WordToTranslationActivity extends FourVariantsCardActivity {
    @Override
    Language getTitleLanguage() {
        return Preferences.LEARNING_LANGUAGE;
    }

    @Override
    Language getQuestionableLanguage() {
        return Preferences.NATIVE_LANGUAGE;
    }

    @Override
    String getImageSubFolder() {
        return "";
    }
}
