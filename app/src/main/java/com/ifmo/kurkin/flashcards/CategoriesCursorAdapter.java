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
        String name = cursor.getString(1);
        int id = cursor.getInt(0);

        final Category category = new Category(id, name);

        ((TextView) view.findViewById(R.id.learning_title)).setText(category.getTitle());
        ((TextView) view.findViewById(R.id.native_title)).setText(category.getTitle());

        ImageView icon = (ImageView) view.findViewById(R.id.category_icon);
        File pic = new File(cursor.getString(6));
        System.out.println(id+" "+name+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "+cursor.getString(6));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(pic.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        icon.setImageBitmap(bitmap);

        int total = cursor.getInt(5);
        int learned = new CardList(context).getCategoryRating(id);
        TextView wordsCount = (TextView) view.findViewById(R.id.words_count);
        wordsCount.setText(learned + "/" + total);

        ((ProgressBar) view.findViewById(R.id.progress_bar)).setProgress(total == 0 ? 100 : learned * 100 / total);
    }
}

