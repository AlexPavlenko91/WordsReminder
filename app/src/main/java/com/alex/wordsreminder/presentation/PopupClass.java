package com.alex.wordsreminder.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.adapters.PracticeAdapter;
import com.alex.wordsreminder.fragments.WordsFragment;
import com.alex.wordsreminder.models.AntonymModel;
import com.alex.wordsreminder.models.BaseMeaning;
import com.alex.wordsreminder.models.DefinitionModel;
import com.alex.wordsreminder.models.ExampleModel;
import com.alex.wordsreminder.models.SynonymModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.utils.DbHelper;

import java.util.ArrayList;
import java.util.Locale;

public class PopupClass {
    protected ImageButton ibtnDelTranslation, ibtnDelWord, ibtnGoogle, ibtnClosePopup;
    protected Button btnSave, btnDelete;
    protected View viewPopup;
    protected EditText etWord, etTranslation;
    private WordsFragment callerFragment = null;
    private String idWord = "-1";
    private int position;
    private WordModel wordModel;
    private PopupWindow popupWindow;
    private final Context context;
    private final DbHelper dbHelper;
    private SpeechRecognizer speechRecWord, speechRecTranslation;
    private ImageButton micBtnWord, micBtnTranslation;
    private static final String TAG = "PopUpRec";

    public PopupClass(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view, WordsFragment callerFragment) {

        this.callerFragment = callerFragment;
        LayoutInflater inflater = (LayoutInflater) view
                .getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopup = inflater.inflate(R.layout.popup_change_word, (ViewGroup) view.getParent(), false);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(viewPopup, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);// show the popup window

        // which view you pass in doesn't matter, it is only used for the window token
        etWord = viewPopup.findViewById(R.id.newWordInput);
        etTranslation = viewPopup.findViewById(R.id.translationInput);
        micBtnWord = viewPopup.findViewById(R.id.ib_mic_word);
        micBtnTranslation = viewPopup.findViewById(R.id.ib_mic_translation);
        btnSave = viewPopup.findViewById(R.id.buttonSave);
        ibtnDelWord = viewPopup.findViewById(R.id.imageButtonDelWord);
        ibtnDelTranslation = viewPopup.findViewById(R.id.imageButtonDelTranslation);
        ibtnClosePopup = viewPopup.findViewById(R.id.btn_close_popup);
        ibtnGoogle = viewPopup.findViewById(R.id.imageButtonGoogleTranslator);
        btnDelete = viewPopup.findViewById(R.id.btn_del_word);
        btnDelete.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(v -> {
            onSavePopUpClick(v);
            popupWindow.dismiss();
        });
        ibtnDelWord.setOnClickListener(v -> etWord.setText(""));
        ibtnDelTranslation.setOnClickListener(v -> etTranslation.setText(""));
        ibtnGoogle.setOnClickListener(this::onButtonTranslateClick);
        btnDelete.setOnClickListener(v -> {
            deleteWord();
            deleteAnswer();
            popupWindow.dismiss();
        });
        ibtnClosePopup.setOnClickListener(v -> popupWindow.dismiss());

        speechRecWord = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecTranslation = SpeechRecognizer.createSpeechRecognizer(context);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecWord.setRecognitionListener(new MySpeechListener(etWord));
        micBtnWord.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    micBtnWord.setImageResource(R.drawable.ic_mic_red);
                    speechRecWord.startListening(speechRecognizerIntent);
                    break;
                case MotionEvent.ACTION_UP:
                    micBtnWord.setImageResource(R.drawable.ic_mic_black_off);
                    speechRecWord.stopListening();
                    v.performClick();
                    break;
                default:
                    break;
            }
            return true;
        });

        speechRecTranslation.setRecognitionListener(new MySpeechListener(etTranslation));
        micBtnTranslation.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    micBtnTranslation.setImageResource(R.drawable.ic_mic_red);
                    speechRecTranslation.startListening(speechRecognizerIntent);
                    break;
                case MotionEvent.ACTION_UP:
                    micBtnTranslation.setImageResource(R.drawable.ic_mic_black_off);
                    speechRecTranslation.stopListening();
                    v.performClick();
                    break;
                default:
                    break;
            }
            return true;
        });


    }


    private void deleteWord() {
        boolean isDeleted = dbHelper.delWordByID(idWord);
        if (isDeleted) {
            callerFragment.getAdapter().removeItem(position);
            Toast.makeText(context,
                    "Data Successfully deleted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAnswer() {
        dbHelper.delAnswersByWordID(idWord);
        callerFragment.updateAppBar();
    }

    public void changeWordPopupWindow(final View view, WordsFragment callerFragment, WordModel wordModel, int position) {
        this.wordModel = wordModel;
        this.position = position;
        this.idWord = wordModel.getId();
        showPopupWindow(view, callerFragment);
        etWord.setText(wordModel.getWord());
        etTranslation.setText(wordModel.getTranslation());
        btnDelete.setVisibility(View.VISIBLE);
        btnSave.setText(R.string.save_changes);
        btnSave.setOnClickListener(v -> {
            onSaveChangesPopupClick(v);
            popupWindow.dismiss();
        });
    }

    private void onSaveChangesPopupClick(View v) {
        String word = etWord.getText().toString().trim();
        String translation = etTranslation.getText().toString().trim();
        if (!word.equals("") && !translation.equals("")) {
            //WordItem wordItem = DbHelper.getInstance(v.getContext()).wordByID(idWord);
            wordModel.setWord(word);
            wordModel.setTranslation(translation);
            boolean isInserted = DbHelper.getInstance(v.getContext()).updateWord(wordModel);
            if (isInserted) {
                //callerFragment.refreshWordsList();
                callerFragment.getAdapter().updateItem(position, wordModel);
                Toast.makeText(v.getContext(),
                        "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(v.getContext(), "Something went wrong :(.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(v.getContext(),
                    "You must put something in the text field!", Toast.LENGTH_LONG).show();
        }
    }


    public void onSavePopUpClick(View view) {

        String newWord = "", newTranslation = "";
        if (etWord != null && etTranslation != null) {
            newWord = etWord.getText().toString().trim();

            newTranslation = etTranslation.getText().toString().trim();
        }
        assert etWord != null;
        if (etWord.length() != 0 && etTranslation.length() != 0) {
            addData(newWord, newTranslation);
            etWord.setText("");
            etTranslation.setText("");
        } else {
            Toast.makeText(view.getContext(),
                    "You must put something in the text field!", Toast.LENGTH_LONG).show();
        }
    }


    public void addData(String newWord, String newTranslation) {
        int isInserted = dbHelper.addWordToDB(newWord, newTranslation);
        if (isInserted != -1) {
            WordModel wordModel = dbHelper.wordByID(String.valueOf(isInserted));
            callerFragment.getAdapter().addItem(wordModel);
            Toast.makeText(context,
                    "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }


    public void onButtonTranslateClick(View view) {
        Toast.makeText(view.getContext(), "This function is not available yet", Toast.LENGTH_SHORT).show();
    }

    public void changeMeaningPart(PracticeAdapter.MeaningPartAdapter adapter, ListView listView, ArrayList<BaseMeaning> list, int id,
                                  View view, String meaningPart) {
        LayoutInflater inflater = (LayoutInflater) view
                .getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopup = inflater.inflate(R.layout.popup_change_word, (ViewGroup) view.getParent(), false);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(viewPopup, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);// show the popup window

        EditText etMeaning = viewPopup.findViewById(R.id.etMeaning);
        etMeaning.setText(list.get(id).getMeaningPart());
        btnSave = viewPopup.findViewById(R.id.btnSaveMeaning);
        btnDelete = viewPopup.findViewById(R.id.btn_del_meaning);
        etMeaning.setHint(meaningPart);

        btnSave.setOnClickListener(v -> {
            String item = etMeaning.getText().toString().trim();
            if (!item.equals("")) {
                BaseMeaning baseMeaning = list.get(id);
                baseMeaning.setMeaningPart(item);
                switch (meaningPart) {
                    case "definition": {
                        dbHelper.updateDefinition((DefinitionModel) baseMeaning);
                        break;
                    }
                    case "example": {
                        dbHelper.updateExample((ExampleModel) baseMeaning);
                        break;
                    }
                    case "synonym": {
                        dbHelper.updateSynonym((SynonymModel) baseMeaning);
                        break;
                    }
                    case "antonym": {
                        dbHelper.updateAntonym((AntonymModel) baseMeaning);
                        break;
                    }
                }
                list.set(id, baseMeaning);
                adapter.notifyDataSetChanged();
                adapter.setDynamicHeight(listView);
                popupWindow.dismiss();
            }
        });

        btnDelete.setOnClickListener(view1 -> {
            String _id = list.get(id).getId();
            switch (meaningPart) {
                case "definition": {
                    dbHelper.delDefinitionById(_id);
                    break;
                }
                case "example": {
                    dbHelper.delExampleById(_id);
                    break;
                }
                case "synonym": {
                    dbHelper.delSynonymById(_id);
                    break;
                }
                case "antonym": {
                    dbHelper.delAntonymById(_id);
                    break;
                }
            }
            list.remove(id);
            adapter.notifyDataSetChanged();
            adapter.setDynamicHeight(listView);
            popupWindow.dismiss();
        });

        ibtnClosePopup = viewPopup.findViewById(R.id.btn_close_popup_meaning);
        ibtnClosePopup.setOnClickListener(v -> popupWindow.dismiss());
        ImageButton ibClear = viewPopup.findViewById(R.id.ibClearMeaning);
        ibClear.setOnClickListener(v -> etMeaning.setText(""));
    }

    public void addMeaningPart(View view, PracticeAdapter.MeaningPartAdapter adapter,
                               ListView listView, ArrayList<BaseMeaning> list, String idMeaning, String meaningPart) {
        LayoutInflater inflater = (LayoutInflater) view
                .getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopup = inflater.inflate(R.layout.popup_change_word, (ViewGroup) view.getParent(), false);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(viewPopup, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);// show the popup window

        EditText etMeaning = viewPopup.findViewById(R.id.etMeaning);
        etMeaning.setHint("New " + meaningPart);
        ibtnClosePopup = viewPopup.findViewById(R.id.btn_close_popup_meaning);
        ibtnClosePopup.setOnClickListener(v -> popupWindow.dismiss());
        ImageButton ibClear = viewPopup.findViewById(R.id.ibClearMeaning);
        ibClear.setOnClickListener(v -> etMeaning.setText(""));

        btnDelete = viewPopup.findViewById(R.id.btn_del_meaning);
        btnDelete.setVisibility(View.INVISIBLE);
        btnSave = viewPopup.findViewById(R.id.btnSaveMeaning);
        btnSave.setOnClickListener(v -> {
            String res = etMeaning.getText().toString().trim();
            if (!res.equals("")) {
                BaseMeaning item = new BaseMeaning();
                switch (meaningPart) {
                    case "definition": {
                        item = new DefinitionModel();
                        item.setId(dbHelper.addDefinitionToDB2(idMeaning, res));
                        break;
                    }
                    case "example": {
                        item = new ExampleModel();
                        item.setId(dbHelper.addExample(idMeaning, res));
                        break;
                    }
                    case "synonym": {
                        item = new SynonymModel();
                        item.setId(dbHelper.addSynonym(idMeaning, res));
                        break;
                    }
                    case "antonym": {
                        item = new AntonymModel();
                        item.setId(dbHelper.addAntonym(idMeaning, res));
                        break;
                    }
                }
                item.setMeaningPart(res);
                item.setMeaningId(idMeaning);
                list.add(item);
                adapter.notifyDataSetChanged();
                adapter.setDynamicHeight(listView);
                popupWindow.dismiss();
            } else
                Toast.makeText(context, R.string.empty_field_warning, Toast.LENGTH_SHORT).show();
        });
    }

    static class MySpeechListener implements RecognitionListener {

        EditText editText;

        public MySpeechListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            editText.setText("");
            editText.setHint("Listening...");
            Log.d(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            Log.d(TAG,  "error " +  i);
            editText.setText(R.string.try_again);
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            editText.setText(data.get(0));
            editText.setHint(R.string.translation);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    }
}
