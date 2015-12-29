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

import java.io.File;
import java.util.EnumMap;

/**
 * Created by kurkin on 22.11.15.
 */
public class CategoriesCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public CategoriesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.categories_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String name1 = cursor.getString(2);
        String name2 = cursor.getString(3);
        int id = cursor.getInt(0);

        final Category category = new Category(id, name1);

        ((TextView) view.findViewById(R.id.learning_title)).setText(name1);
        ((TextView) view.findViewById(R.id.native_title)).setText(name2);

        ImageView icon = (ImageView) view.findViewById(R.id.category_icon);

        try {
            File pic = new File(cursor.getString(6));
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(pic.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
            icon.setImageBitmap(bitmap);
        } catch (Exception e) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
        }


        int total = cursor.getInt(5);
        int learned = new CardList(context).getCategoryRating(id);
        TextView wordsCount = (TextView) view.findViewById(R.id.words_count);
        wordsCount.setText(learned + "/" + total);

        ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgress(total == 0 ? 100 : learned * 100 / total);
    }
}

