package com.alex.wordsreminder.models;

public class BaseMeaning {
    private String id;
    private String meaningId;
    private String meaningPart;

    public BaseMeaning() {
    }

    public BaseMeaning(String id, String meaningId, String meaningPart) {
        this.id = id;
        this.meaningId = meaningId;
        this.meaningPart = meaningPart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeaningId() {
        return meaningId;
    }

    public void setMeaningId(String meaningId) {
        this.meaningId = meaningId;
    }

    public String getMeaningPart() {
        return meaningPart;
    }

    public void setMeaningPart(String meaningPart) {
        this.meaningPart = meaningPart;
    }


   /* public static interface IMeaning {

        String getMeaningPart();
    }*/
}
