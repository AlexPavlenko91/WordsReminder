package com.alex.wordsreminder.services;

import android.content.Context;

import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.utils.DbHelper;

import java.net.ContentHandler;
import java.util.ArrayList;

public class WordDataService {
    private final DbHelper dbHelper;
    public WordDataService(Context context) {
        dbHelper = DbHelper.getInstance(context);
    }
    public WordModel leastLearnedWord(){
        return leastLearnedWord(dbHelper.getAllWords());
    }
    public WordModel leastLearnedWord(ArrayList<WordModel> words) {
        WordModel word = new WordModel();
        if (words.size() > 0) {
            word = words.get(0);
            for (int i = 1; i < words.size(); i++) {
                if (words.get(i).getRightAnswers() < word.getRightAnswers()) {
                    word = words.get(i);
                }
            }
        }
        return word;
    }
}
