package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.EnumMap;

/**
 * Created by kurkin on 22.11.15.
 */
public class CategoriesCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
//    private DataStore store;

    public CategoriesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mInflater = LayoutInflater.from(context);
//        store = new DataStore(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.categories_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String name = cursor.getString(1);
        int id = cursor.getInt(0);

        EnumMap<Language, String> title = new EnumMap<>(Language.class);
        title.put(Language.ENG, name);
        title.put(Language.RUS, name);
        title.put(Language.FRA, name);

        final Category category = new Category(id, title);

        ((TextView) view.findViewById(R.id.learning_title)).setText(category.getTitle(Preferences.LEARNING_LANGUAGE));
        ((TextView) view.findViewById(R.id.native_title)).setText(category.getTitle(Preferences.NATIVE_LANGUAGE));

//        ImageView icon = (ImageView) view.findViewById(R.id.category_icon);
//        AssetManager am = context.getResources().getAssets();

//        String iconPath = Preferences.IMAGE_ASSETS + "/" + Preferences.SMALL_IMAGE_SUBFOLDER + "/" + eng.toLowerCase() + ".jpg";
//        TODO: load image

//        int total = store.getCardCount(id);
//        int learned = store.getLearnedCardCount(id);
        TextView wordsCount = (TextView) view.findViewById(R.id.words_count);
//        wordsCount.setText(learned + "/" + total);

//        ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgress(total == 0 ? 100 : learned * 100 / total);
    }
}

