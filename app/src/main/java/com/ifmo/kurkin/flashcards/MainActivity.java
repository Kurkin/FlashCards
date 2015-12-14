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

                String name = cursor.getString(1);
                int id = cursor.getInt(0);

                EnumMap<Language, String> title = new EnumMap<>(Language.class);
                title.put(Language.ENG, name);
                title.put(Language.RUS, name);
                title.put(Language.FRA, name);

                final Category category = new Category(id, title);

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

    private String[] createLanguageValues() {
        String[] res = new String[Language.values().length];

        for (int i = 0; i < Language.values().length; i++) {
            switch (Language.values()[i]) {
                case ENG:
                    res[i] = getString(R.string.eng);
                    break;
                case FRA:
                    res[i] = getString(R.string.fra);
                    break;
                case RUS:
                    res[i] = getString(R.string.rus);
                    break;
            }
        }

        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.actionbar_spinner_item,
                createLanguageValues()
        );
        spinnerAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(Preferences.LEARNING_LANGUAGE.ordinal());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    final String TAG = "Main";
}