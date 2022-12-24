package com.alex.wordsreminder.services;

import com.alex.wordsreminder.models.WordModel;

import java.util.ArrayList;

public class WordDataService {

    public WordDataService() {
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
