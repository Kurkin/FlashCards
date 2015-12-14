package com.ifmo.kurkin.flashcards;

public class TranslationOneCardActivity extends OneCardActivity {
    @Override
    void onBeginState(Card card) {
        getWord().setText(card.lang1);
        getTranslation().setText("");
//        setImage(card.lang2);
    }

    @Override
    void onEndState(Card card) {
        getTranslation().setText(card.lang1);
    }
}
