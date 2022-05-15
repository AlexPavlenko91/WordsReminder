package com.alex.wordsreminder.models;

import java.util.Date;

public class AnswerModel {
    private String id;
    private String userId;
    private String wordId;
    private Date dateTime;
    private String result;
     String isRight;



    public AnswerModel(String id, String userId, String wordId, Date dateTime, String result, String isRight) {
        this.id = id;
        this.userId = userId;
        this.wordId = wordId;
        this.dateTime = dateTime;
        this.result = result;
        this.isRight = isRight;
    }

    public AnswerModel() {

    }


    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getResult() {
        return result;
    }

    public int getIntIs_right() {

        return Integer.parseInt(isRight);
    }

    public void setResult(String result) {
        this.result = result;
    }


}
