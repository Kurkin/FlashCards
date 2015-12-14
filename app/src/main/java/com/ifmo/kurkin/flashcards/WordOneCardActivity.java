package com.ifmo.kurkin.flashcards;

public class WordOneCardActivity extends OneCardActivity {
    @Override
    void onBeginState(Card card) {
        getWord().setText(card.lang1);
        getTranslation().setText("");
//        setImage(Constants.BLURED_IMAGE_SUBFOLDER + "/" + card.getTitle(Language.ENG).toLowerCase());
    }

    @Override
    void onEndState(Card card) {
        getTranslation().setText(card.lang1);
//        setImage(card.lang2);
    }
}
