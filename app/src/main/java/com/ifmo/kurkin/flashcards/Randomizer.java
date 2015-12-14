package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Randomizer {

    private ArrayList<Integer> indexes, mistakeIndexes;
    private int current, categoryId;
    private CardList cardList;

    public Randomizer(Context context, int categoryId) {
        cardList = new CardList(context);
        this.categoryId = categoryId;
        long numRows = DatabaseUtils.queryNumEntries(SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("cards.db"), null /*factory*/), cardList.getCategoryName(categoryId));
        indexes = new ArrayList<>();
        mistakeIndexes = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes, new Random(System.nanoTime()));
        System.out.println(numRows+" "+indexes);
        current = -1;
    }

    public Card nextCard() {
        if (hasNext()) {
            current++;
            System.out.println(categoryId+" "+indexes.get(current));
            return cardList.getCard(categoryId,indexes.get(current));
        }
        return null;
    }

    public void mistake() {
        mistakeIndexes.add(indexes.get(current));
    }

    public boolean hasNext() {
        if (current + 1 < indexes.size()) {
            return true;
        }
        update();
        return (current + 1 < indexes.size());
    }

    private void update() {
        current = -1;
        indexes = new ArrayList<>(mistakeIndexes);
        Collections.shuffle(indexes, new Random(System.nanoTime()));
        mistakeIndexes.clear();
    }

}

