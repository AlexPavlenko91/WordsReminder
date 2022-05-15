package com.alex.wordsreminder.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * A placeholder item representing a piece of content.
 */
public class WordModel extends ViewModel implements Parcelable {
    private String id;
    private String word;
    private String translation;
    private String dateCreation;
    private int rightAnswers;
    private String phonetic;
    private String audio;
    private String sourceUrl;
    private String origin;



    public WordModel(String id, String word, String translation, String dateCreation, int rightAnswers) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.dateCreation = dateCreation;
        this.rightAnswers = rightAnswers;
    }
    public WordModel(){}

    protected WordModel(Parcel in) {
        id = in.readString();
        word = in.readString();
        translation = in.readString();
        dateCreation = in.readString();
        rightAnswers = in.readInt();
        phonetic = in.readString();
        audio = in.readString();
        sourceUrl = in.readString();
        origin = in.readString();
    }

    public static final Creator<WordModel> CREATOR = new Creator<WordModel>() {
        @Override
        public WordModel createFromParcel(Parcel in) {
            return new WordModel(in);
        }

        @Override
        public WordModel[] newArray(int size) {
            return new WordModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(int rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public void setPosition(int num) {
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }


    @NonNull
    @Override
    public String toString() {
        return word;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(word);
        parcel.writeString(translation);
        parcel.writeString(dateCreation);
        parcel.writeInt(rightAnswers);
        parcel.writeString(phonetic);
        parcel.writeString(audio);
        parcel.writeString(sourceUrl);
        parcel.writeString(origin);
    }
}
