package com.ifmo.kurkin.flashcards;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Randomizer {

    private ArrayList<Integer> indexes, mistakeIndexes, places;
    private int current, categoryId, count;
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
        count = (int) numRows;
        Collections.shuffle(indexes, new Random(System.nanoTime()));
        System.out.println(numRows + " " + indexes);
        current = -1;
        places = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            places.add(i);
        }
    }

    public Pair<Card[], Integer> nextCards() {
        if (hasNext()) {
            current++;
            System.out.println(categoryId + " " + indexes.get(current));
            Card[] result = new Card[4];
            Collections.shuffle(places, new Random(System.nanoTime()));
            result[places.get(0)] = cardList.getCard(categoryId, indexes.get(current));
            int[] currentIndexes = randomIndexes(indexes.get(current));
            result[places.get(1)] = cardList.getCard(categoryId, currentIndexes[0]);
            result[places.get(2)] = cardList.getCard(categoryId, currentIndexes[1]);
            result[places.get(3)] = cardList.getCard(categoryId, currentIndexes[2]);
            return new Pair<>(result, places.get(0));
        }
        return null;
    }

    private int[] randomIndexes(int except) {
        Random rn = new Random(System.nanoTime());
        int first = rn.nextInt(count);
        int second = rn.nextInt(count);
        int third = rn.nextInt(count);
        if (first != except && second != except && third != except && first != second && first != third && third != second) {
            return new int[]{first, second, third};
        }
        return randomIndexes(except);
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

