package com.ifmo.kurkin.flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public abstract class OneCardActivity extends Activity {

    private View area;
    private TextView word;
    private TextView translation;
    private ImageView image;
    private View knowButton;
    private View dontKnowButton;
    private Randomizer randomizer;
    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_word_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Context context = this.getApplicationContext();
        Category category = (Category) getIntent().getSerializableExtra(Preferences.INTENT_CATEGORY);
        randomizer = new Randomizer(context, category.id);

        area = findViewById(R.id.area);
        word = (TextView) findViewById(R.id.word);
        translation = (TextView) findViewById(R.id.translation);
        image = (ImageView) findViewById(R.id.image);
        knowButton = findViewById(R.id.action_know);
        dontKnowButton = findViewById(R.id.action_dont_know);

        setTitle(category.getTitle(Preferences.LEARNING_LANGUAGE));

        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knowButton.setEnabled(true);
                dontKnowButton.setEnabled(true);
                area.setEnabled(false);
                area.setClickable(false);
                getTranslation().setText(card.lang2);
            }
        });

        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStep();
            }
        });

        dontKnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomizer.mistake();
                onStep();
            }
        });

        onStep();
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

        card = randomizer.nextCard();
        getWord().setText(card.lang1);
        getTranslation().setText("");

        File pic = new File(card.picture);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(pic.getAbsolutePath(),bmOptions);
        image.setImageBitmap(bitmap);

        knowButton.setEnabled(false);
        dontKnowButton.setEnabled(false);
        area.setEnabled(true);
        area.setClickable(true);
    }

    abstract void onBeginState(Card card);

    abstract void onEndState(Card card);

    public TextView getWord() {
        return word;
    }

    public TextView getTranslation() {
        return translation;
    }
}
