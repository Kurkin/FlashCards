package com.ifmo.kurkin.flashcards;

import android.widget.Button;
import android.widget.TextView;

public class WordToTranslationActivity extends FourVariantsCardActivity {

    @Override
    void setVar(Card card, Button btn) {
        btn.setText(card.lang2);
    }

    @Override
    void setTitle(Card card, TextView word) {
        word.setText(card.lang1);
    }
}
