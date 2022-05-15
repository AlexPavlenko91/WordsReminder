package com.alex.wordsreminder.presentation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
    private Context context;
    private DbHelper dbHelper;

    public PopupClass(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);
    }

    public void showPopupWindow(final View view, WordsFragment callerFragment) {
        this.callerFragment = callerFragment;
        LayoutInflater inflater = (LayoutInflater) view
                .getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopup = inflater.inflate(R.layout.popup_change_word, null);


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(viewPopup, width, height, true);


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);// show the popup window

        // which view you pass in doesn't matter, it is only used for the window token
        etWord = viewPopup.findViewById(R.id.newWordInput);
        etTranslation = viewPopup.findViewById(R.id.translationInput);
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
        // dismiss the popup window when touched

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
        viewPopup = inflater.inflate(R.layout.popup_change_meaning, null);
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
        viewPopup = inflater.inflate(R.layout.popup_change_meaning, null);
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
}
