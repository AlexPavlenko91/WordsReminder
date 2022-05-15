package com.alex.wordsreminder.models;

public class ExampleModel extends BaseMeaning {
    public String getId() {
        return super.getId();
    }

    public void setId(String id) {
        super.setId(id);
    }

    public String getMeaningId() {
        return super.getMeaningId();
    }

    public void setMeaningId(String meaningId) {
        super.setMeaningId(meaningId);
    }

    @Override
    public String getMeaningPart() {
        return super.getMeaningPart();
    }

    public void setMeaningPart(String example) {
        super.setMeaningPart(example);
    }
}
