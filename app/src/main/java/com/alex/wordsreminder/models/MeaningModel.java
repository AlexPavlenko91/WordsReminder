package com.alex.wordsreminder.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MeaningModel {
    private String id;
    private String wordId;
    private String partOfSpeech;
    private ArrayList<String> definitions;
    private ArrayList<DefinitionModel> definitionModels = new ArrayList<>();
    private ArrayList<ExampleModel> exampleModels = new ArrayList<>();
    private ArrayList<SynonymModel> synonymModels = new ArrayList<>();
    private ArrayList<AntonymModel> antonymModels = new ArrayList<>();

    public MeaningModel() {

    }

    @NonNull
    @Override
    public String toString() {
        return " definitions=" + definitions +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public ArrayList<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<String> definitions) {
        this.definitions = definitions;
    }

    public void setDefinitionsFromString(String definitionStr) {
        this.definitions = (ArrayList<String>) Arrays.stream(definitionStr.substring(1, definitionStr.length() - 1).split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public ArrayList<DefinitionModel> getDefinitionModels() {
        return definitionModels;
    }

    public void setDefinitionModels(ArrayList<DefinitionModel> definitionModels) {
        this.definitionModels = definitionModels;
    }

    public ArrayList<ExampleModel> getExampleModels() {
        return exampleModels;
    }

    public void setExampleModels(ArrayList<ExampleModel> exampleModels) {
        this.exampleModels = exampleModels;
    }

    public ArrayList<SynonymModel> getSynonymModels() {
        return synonymModels;
    }

    public void setSynonymModels(ArrayList<SynonymModel> synonymModels) {
        this.synonymModels = synonymModels;
    }

    public ArrayList<AntonymModel> getAntonymModels() {
        return antonymModels;
    }

    public void setAntonymModels(ArrayList<AntonymModel> antonymModels) {
        this.antonymModels = antonymModels;
    }
}
