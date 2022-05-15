package com.alex.wordsreminder.services;

import android.content.Context;

import com.alex.wordsreminder.models.AntonymModel;
import com.alex.wordsreminder.models.DefinitionModel;
import com.alex.wordsreminder.models.ExampleModel;
import com.alex.wordsreminder.models.MeaningModel;
import com.alex.wordsreminder.models.SynonymModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.network.RequestSingleton;
import com.alex.wordsreminder.utils.DbHelper;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DictionaryDataService {

    public static final String QUERY_DICTIONARY_BY_WORD = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final DbHelper dbHelper;
    Context context;

    public DictionaryDataService(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);
    }

    public void getDictionaryByWordFromAPI(WordModel wordModel, VolleyResponseListener volleyResponseListener) {
        List<MeaningModel> meaningModels = new ArrayList<>();

        String url = QUERY_DICTIONARY_BY_WORD + wordModel.getWord();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject oneDefFromAPI = (JSONObject) response.get(i);
                    String origin = "", sourceUrl = "", audio = "", phonetic = "";
                    try {
                        origin = oneDefFromAPI.getString("origin");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        phonetic = oneDefFromAPI.getString("phonetic");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray phonetics = oneDefFromAPI.getJSONArray("phonetics");
                        for (int j = 0; j < phonetics.length(); j++) {
                            JSONObject phoneticObj = phonetics.getJSONObject(j);
                            try {
                                audio = phoneticObj.getString("audio");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                sourceUrl = phoneticObj.getString("sourceUrl");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (!audio.equals("") && !sourceUrl.equals("")) break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    wordModel.setOrigin(origin);
                    wordModel.setAudio(audio);
                    wordModel.setPhonetic(phonetic);
                    wordModel.setSourceUrl(sourceUrl);
                    meaningModels.addAll(getMeaningsFromJSONArray(oneDefFromAPI.getJSONArray("meanings"), wordModel.getId()));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DbHelper.getInstance(context).updateWordFromDictionary(wordModel);
            volleyResponseListener.onResponse(meaningModels, wordModel);

        }, error -> volleyResponseListener.onError("Something wrong"));
        RequestSingleton.getInstance(context).addToRequestQueue(request);

    }

    public List<MeaningModel> getMeaningsFromJSONArray(JSONArray meanings, String wordId) {
        List<MeaningModel> meaningModels = new ArrayList<>();
        for (int i = 0; i < meanings.length() && i < 5; i++) {
            MeaningModel meaningModel = new MeaningModel();
            String partOfSpeech = "";
            try {
                JSONObject oneMeaning = meanings.getJSONObject(i);
                partOfSpeech = oneMeaning.getString("partOfSpeech");
                meaningModel.setWordId(wordId);
                meaningModel.setPartOfSpeech(partOfSpeech);
                meaningModel.setId(dbHelper.addMeaningToDB2(wordId, partOfSpeech));
                String meaningId = meaningModel.getId();

                JSONArray definitionsArr = oneMeaning.getJSONArray("definitions");
                ArrayList<DefinitionModel> definitionModels = new ArrayList<>();
                ArrayList<ExampleModel> exampleModels = new ArrayList<>();
                ArrayList<SynonymModel> synonymModels = new ArrayList<>();
                ArrayList<AntonymModel> antonymModels = new ArrayList<>();
                for (int j = 0; j < definitionsArr.length() && j < 5; j++) {
                    JSONObject oneDef = definitionsArr.getJSONObject(j);
                    String definition = "";
                    String example = "";
                    try {
                        definition = oneDef.getString("definition");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        example = oneDef.getString("example");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!definition.equals("")) {
                        DefinitionModel def = new DefinitionModel();
                        def.setMeaningPart(definition);
                        def.setMeaningId(meaningId);
                        def.setId(dbHelper.addDefinitionToDB2(meaningId, definition));
                        definitionModels.add(def);
                    }
                    if (!example.equals("")) {
                        ExampleModel exampleModel = new ExampleModel();
                        exampleModel.setMeaningPart(example);
                        exampleModel.setMeaningId(meaningId);
                        exampleModel.setId(dbHelper.addExample(meaningId, example));
                        exampleModels.add(exampleModel);
                    }
                    try {
                        JSONArray synonymsArr = oneDef.getJSONArray("synonyms");
                        for (int k = 0; k < synonymsArr.length(); k++) {
                            String synonym = synonymsArr.getString(k);
                            if (!synonym.equals("")) {
                                SynonymModel synonymModel = new SynonymModel();
                                synonymModel.setId(dbHelper.addSynonym(meaningId, synonym));
                                synonymModel.setMeaningId(meaningId);
                                synonymModel.setMeaningPart(synonym);
                                synonymModels.add(synonymModel);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray antonymsArr = oneDef.getJSONArray("antonyms");
                        for (int k = 0; k < antonymsArr.length(); k++) {
                            String antonym = antonymsArr.getString(k);
                            if (!antonym.equals("")) {
                                AntonymModel antonymModel = new AntonymModel();
                                antonymModel.setId(dbHelper.addAntonym(meaningId, antonym));
                                antonymModel.setMeaningId(meaningId);
                                antonymModel.setMeaningPart(antonym);
                                antonymModels.add(antonymModel);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                meaningModel.setDefinitionModels(definitionModels);
                meaningModel.setExampleModels(exampleModels);
                meaningModel.setSynonymModels(synonymModels);
                meaningModel.setAntonymModels(antonymModels);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            meaningModels.add(meaningModel);
        }

        return meaningModels;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(List<MeaningModel> meaningModels, WordModel wordModel);
    }


}
