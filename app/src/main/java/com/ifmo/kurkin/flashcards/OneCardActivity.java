package com.ifmo.kurkin.flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class OneCardActivity extends Activity {

    private View area;
    private TextView word;
    private TextView translation;
    private ImageView image;
    private View knowButton;
    private View dontKnowButton;
    private List<Card> cards;
    private int currentPosition;
    private static final Random RANDOM = new Random();
    private Map<Integer, Integer> rating;
//    private DataStore store;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_word_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

//        store = new DataStore(this);

        area = findViewById(R.id.area);
        word = (TextView) findViewById(R.id.word);
        translation = (TextView) findViewById(R.id.translation);
        image = (ImageView) findViewById(R.id.image);
        knowButton = findViewById(R.id.action_know);
        dontKnowButton = findViewById(R.id.action_dont_know);

        category = (Category) getIntent().getSerializableExtra(Preferences.INTENT_CATEGORY);
        setTitle(category.getTitle(Preferences.LEARNING_LANGUAGE));

        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knowButton.setEnabled(true);
                dontKnowButton.setEnabled(true);
                area.setEnabled(false);
                area.setClickable(false);
//                onEndState(cards.get(currentPosition));
            }
        });

        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int id = cards.get(currentPosition).getId();
//                rating.put(id, rating.get(id) - 1);
//                store.decRating(id);
//                if (rating.get(id) == 0) {
//                    cards.remove(currentPosition);
//                }
//                onStep();
            }
        });
        dontKnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int id = cards.get(currentPosition).getId();
//                rating.put(id, rating.get(id) + 1);
//                store.incRating(id);
//                onStep();
            }
        });

//        resetRating();

//        onStep();
    }

    private void resetRating() {
//        cards = store.getAllCardsAsList(category.getId());
//        rating = new HashMap<>();
//        for (Card c : cards) {
//            rating.put(c.getId(), store.getRating(c.getId()) + 1);
//        }
    }

    private void endTraining() {
        knowButton.setEnabled(false);
        dontKnowButton.setEnabled(false);
        area.setEnabled(false);
        area.setClickable(false);

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
        if (cards.isEmpty()) {
            endTraining();
            return;
        }

        currentPosition = RANDOM.nextInt(cards.size());
        onBeginState(cards.get(currentPosition));

        knowButton.setEnabled(false);
        dontKnowButton.setEnabled(false);
        area.setEnabled(true);
        area.setClickable(true);
    }

    abstract void onBeginState(Card card);

    abstract void onEndState(Card card);

    protected void setImage(String name) {
        AssetManager am = getResources().getAssets();

//        String iconPath = Constants.IMAGE_ASSETS + "/" + name + ".jpg";
//        try {
//            InputStream is = am.open(iconPath);
//            Drawable d = Drawable.createFromStream(is, null);
//            image.setImageDrawable(d);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public TextView getWord() {
        return word;
    }

    public TextView getTranslation() {
        return translation;
    }
}
