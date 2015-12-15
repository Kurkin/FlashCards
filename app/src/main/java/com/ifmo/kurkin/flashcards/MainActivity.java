package com.ifmo.kurkin.flashcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.ifmo.kurkin.flashcards.db.FlashCardDBHelper;
import com.ifmo.kurkin.flashcards.db.FlashCardImporter;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Properties;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Context context;
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActionBar().setTitle("Categories");

        context = this.getApplicationContext();

        CardList cardList = null;

        try {
            cardList = new CardList(context);
        } catch (SQLiteException e) {
            Intent intent = new Intent(context, DBCreator.class);
            startActivity(intent);
            return;
        }

        listInit(cardList);
    }

    void listInit(CardList cardList) {
        ListView categories = (ListView) findViewById(R.id.categories_layout);
        registerForContextMenu(categories);
        adapter = new CategoriesCursorAdapter(this, cardList.getAllCategories());
        categories.setAdapter(adapter);

        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapter.getItem(i);

                String name1 = cursor.getString(2);
                int id = cursor.getInt(0);

                cursor.close();

                final Category category = new Category(id, name1);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.mode);
                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.mode_choose_layout, null);
                builder.setView(v);
                final AlertDialog dialog = builder.show();

                v.findViewById(R.id.mode_word).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), WordOneCardActivity.class);
                        intent.putExtra(Preferences.INTENT_CATEGORY, category);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

                v.findViewById(R.id.mode_translation).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), TranslationOneCardActivity.class);
                        intent.putExtra(Preferences.INTENT_CATEGORY, category);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

                v.findViewById(R.id.mode_word_to_translation).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), WordToTranslationActivity.class);
                        intent.putExtra(Preferences.INTENT_CATEGORY, category);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

                v.findViewById(R.id.mode_translation_to_word).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), TranslationToWordActivity.class);
                        intent.putExtra(Preferences.INTENT_CATEGORY, category);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        CardList cardList = new CardList(context);
        listInit(cardList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    final String TAG = "Main";
}