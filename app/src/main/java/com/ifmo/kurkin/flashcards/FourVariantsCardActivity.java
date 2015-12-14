package com.ifmo.kurkin.flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FourVariantsCardActivity extends Activity {

    private View area;
    private TextView word;
    private ImageView image;
    private List<Card> cards;
    private List<Card> cardsForVars;
    private int currentPosition;
    private int correctVarsPosition;
    private static final Random RANDOM = new Random();
    private Map<Integer, Integer> rating;
//    private DataStore store;
    private Category category;
    private Button[] vars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_variants_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

//        store = new DataStore(this);

        area = findViewById(R.id.area);
        word = (TextView) findViewById(R.id.word);
        image = (ImageView) findViewById(R.id.image);
        //Init buttons

        vars = new Button[]{
                (Button) findViewById(R.id.var1),
                (Button) findViewById(R.id.var2),
                (Button) findViewById(R.id.var3),
                (Button) findViewById(R.id.var4)
        };
//        for (int i = 0; i < vars.length; i++) {
//            final int finalI = i;
//            vars[i].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    disableButtons();
//                    if (!getImageSubFolder().isEmpty()) {
//                        setImage(cards.get(currentPosition).getTitle(Language.ENG).toLowerCase());
//                    }
//
//                    vars[correctVarsPosition].getBackground().setColorFilter(
//                            getResources().getColor(android.R.color.holo_green_dark),
//                            PorterDuff.Mode.SRC
//                    );
//                    int currentId = cards.get(currentPosition).getId();
//                    int selectedId = cardsForVars.get(finalI).getId();
//                    if (finalI != correctVarsPosition) {
//                        vars[finalI].getBackground().setColorFilter(
//                                getResources().getColor(android.R.color.holo_red_dark),
//                                PorterDuff.Mode.SRC
//                        );
//                        store.incRating(currentId);
//                        store.incRating(selectedId);
//                        rating.put(currentId, rating.get(currentId) + 1);
//                        rating.put(selectedId, rating.get(selectedId) + 1);
//                    } else {
//                        store.decRating(cards.get(currentPosition).getId());
//                        rating.put(currentId, rating.get(currentId) - 1);
//                        if (rating.get(currentId) == 0) {
//                            cards.remove(currentPosition);
//                        }
//                    }
//
//                    area.setEnabled(true);
//                    area.setClickable(true);
//                }
//            });
//        }
//
//        category = (Category) getIntent().getSerializableExtra(Constants.INTENT_CATEGORY);
//        setTitle(category.getTitle(Constants.LEARNING_LANGUAGE));

        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                area.setEnabled(false);
                area.setClickable(false);
                resetButtons();
                onStep();
            }
        });

        area.setEnabled(false);
        area.setClickable(false);

        resetRating();
        onStep();
    }

    abstract Language getTitleLanguage();

    abstract Language getQuestionableLanguage();

    abstract String getImageSubFolder();

    private void disableButtons() {
        for (Button b : vars) {
            b.setEnabled(false);
        }
    }

    private void resetRating() {
//        cards = store.getAllCardsAsList(category.getId());
//        rating = new HashMap<>();
//        for (Card c : cards) {
//            rating.put(c.getId(), store.getRating(c.getId()) + 1);
//        }
    }

    private void resetButtons() {
        for (Button b : vars) {
            b.getBackground().clearColorFilter();
            b.setEnabled(true);
        }
    }

    private void endTraining() {
        area.setEnabled(false);
        area.setClickable(false);
        disableButtons();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message_congratulations);
        builder.setMessage(R.string.message_finish_training);

        builder.setPositiveButton(R.string.action_finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });

        builder.setNegativeButton(R.string.action_try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetRating();
                onStep();
            }
        });

        builder.show();
    }

    private void onStep() {
//        if (cards.isEmpty()) {
//            endTraining();
//            return;
//        }

//        cardsForVars = store.getAllCardsAsList(category.getId());
//
//        currentPosition = RANDOM.nextInt(cards.size());
//        correctVarsPosition = RANDOM.nextInt(vars.length);
//        Card card = cards.get(currentPosition);
//
//        word.setText(card.getTitle(getTitleLanguage()));
//        //set blured image
//        setImage(
//                (getImageSubFolder().isEmpty() ? "" : getImageSubFolder() + "/")
//                        + card.getTitle(Language.ENG).toLowerCase()
//        );
//
//        Collections.shuffle(cardsForVars);
//        for (int i = 0; i < vars.length && i < cardsForVars.size(); i++) {
//            if (cardsForVars.get(i).getId() == card.getId()) {
//                cardsForVars.remove(i);
//            }
//            if (i == correctVarsPosition) {
//                vars[i].setText(card.getTitle(getQuestionableLanguage()));
//            } else {
//                vars[i].setText(cardsForVars.get(i).getTitle(getQuestionableLanguage()));
//            }
//        }
    }

    protected void setImage(String name) {
//        AssetManager am = getResources().getAssets();
//
//        String iconPath = Preferences.IMAGE_ASSETS + "/" + name + ".jpg";
//        try {
//            InputStream is = am.open(iconPath);
//            Drawable d = Drawable.createFromStream(is, null);
//            image.setImageDrawable(d);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
