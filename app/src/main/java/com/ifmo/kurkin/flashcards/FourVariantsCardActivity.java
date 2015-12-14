package com.ifmo.kurkin.flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FourVariantsCardActivity extends Activity {

    private View area;
    private TextView word;
    private ImageView image;
    private int correctVarsPosition;
    private Randomizer randomizer;
    private Category category;
    private Button[] vars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_variants_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        category = (Category) getIntent().getSerializableExtra(Preferences.INTENT_CATEGORY);
        setTitle(category.getTitle(Preferences.LEARNING_LANGUAGE));
        randomizer = new Randomizer(this.getApplicationContext(), category.id);

        area = findViewById(R.id.area);
        word = (TextView) findViewById(R.id.word);
        image = (ImageView) findViewById(R.id.image);

        vars = new Button[]{
                (Button) findViewById(R.id.var1),
                (Button) findViewById(R.id.var2),
                (Button) findViewById(R.id.var3),
                (Button) findViewById(R.id.var4)
        };

        for (int i = 0; i < vars.length; i++) {
            final int finalI = i;
            vars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disableButtons();

                    // todo: set image

                    vars[correctVarsPosition].getBackground().setColorFilter(
                            getResources().getColor(android.R.color.holo_green_dark),
                            PorterDuff.Mode.SRC
                    );

                    if (finalI != correctVarsPosition) {
                        vars[finalI].getBackground().setColorFilter(
                                getResources().getColor(android.R.color.holo_red_dark),
                                PorterDuff.Mode.SRC
                        );
                        randomizer.mistake();
                    }

                    area.setEnabled(true);
                    area.setClickable(true);
                }
            });
        }

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

        onStep();
    }

    abstract Language getTitleLanguage();

    abstract Language getQuestionableLanguage();

    private void disableButtons() {
        for (Button b : vars) {
            b.setEnabled(false);
        }
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

        final Context context = this.getApplicationContext();

        builder.setNegativeButton(R.string.action_try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                randomizer = new Randomizer(context, category.id);
                resetButtons();
                area.setEnabled(true);
                area.setClickable(true);
                onStep();
            }
        });

        builder.show();
    }

    private void onStep() {
        if (!randomizer.hasNext()) {
            endTraining();
            return;
        }

        Pair<Card[], Integer> testSet = randomizer.nextCards();

        correctVarsPosition = testSet.second;
        Card card = testSet.first[testSet.second];

        word.setText(card.lang1);

        //todo: set blured image

        for (int i = 0; i < vars.length; i++) {
            vars[i].setText(testSet.first[i].lang2);
            System.out.println(testSet.first[i].toString());
        }
    }
}
