package com.alex.wordsreminder.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.AntonymModel;
import com.alex.wordsreminder.models.DefinitionModel;
import com.alex.wordsreminder.models.ExampleModel;
import com.alex.wordsreminder.models.MeaningModel;
import com.alex.wordsreminder.models.SynonymModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    public static final int SCHEMA = 1;
    public static final String DATABASE_NAME = "wordsDb002";
    public static final String TBL_WORDS = "words";
    public static final String TBL2_USERS = "users";
    public static final String TBL3_ANSWERS = "answers";
    public static final String TBL4_MEANINGS = "meanings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_RIGHT_ANSWERS_NUM = "right_answers_num";
    public static final String COLUMN_DATE_CREATION = "date_creation";
    public static final String COLUMN_PHONETIC = "phonetic";
    public static final String COLUMN_AUDIO = "audio";
    public static final String COLUMN_SOURCE_URL = "source_url";
    public static final String COLUMN_ORIGIN = "origin";
    public static final String COLUMN2_USER_NAME = "user_name";
    public static final String COLUMN2_EMAIL = "email";
    public static final String COLUMN2_INTERFACE_LANGUAGE = "interface_language";
    public static final String COLUMN2_MAIN_LANGUAGE = "main_language";
    public static final String COLUMN2_LEARNING_LANGUAGE = "learning_language";
    public static final String COLUMN2_DAILY_LOAD = "daily_load";
    public static final String COLUMN2_DATE_CREATED = "date_created";
    public static final String COLUMN2_IS_CURRENT = "is_current";
    public static final String COLUMN2_NOTIFICATIONS_TIME = "notifications_time";
    public static final String COLUMN3_USER_ID = "_user_id";
    public static final String COLUMN3_WORD_ID = "_word_id";
    public static final String COLUMN3_DATE_TIME = "date_time";
    public static final String COLUMN3_RESULT = "result";
    public static final String COLUMN3_IS_RIGHT = "is_right";
    public static final String COLUMN4_WORD_ID = "_word_id";
    public static final String COLUMN4_PART_OF_SPEECH = "part_of_speech";
    public static final String TBL7_SYNONYMS = "synonyms";
    public static final String TBL8_ANTONYMS = "antonyms";
    public static final String COLUMN_MEANING_ID = "_meaning_id";
    public static final String COLUMN5_DEFINITION = "definition";
    public static final String COLUMN6_EXAMPLE = "example";
    public static final String COLUMN7_SYNONYM = "synonym";
    public static final String COLUMN8_ANTONYM = "antonym";
    public static final String TBL5_DEFINITIONS = "definitions";
    public static final String TBL6_EXAMPLES = "examples";
    private static DbHelper mInstance = null;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static DbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table IF NOT EXISTS " + TBL_WORDS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN_WORD + " text, " +
                    COLUMN_TRANSLATION + " text, " +
                    COLUMN_DATE_CREATION + " text, " +
                    COLUMN_RIGHT_ANSWERS_NUM + " integer, " +
                    COLUMN_PHONETIC + " text, " +
                    COLUMN_AUDIO + " text, " +
                    COLUMN_SOURCE_URL + " text, " +
                    COLUMN_ORIGIN + " text "

                    + " )");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL2_USERS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN2_USER_NAME + " text, " +
                    COLUMN2_EMAIL + " text, " +
                    COLUMN2_INTERFACE_LANGUAGE + " text, " +
                    COLUMN2_MAIN_LANGUAGE + " text, " +
                    COLUMN2_LEARNING_LANGUAGE + " text, " +
                    COLUMN2_DAILY_LOAD + " integer, " +
                    COLUMN2_DATE_CREATED + " text, " +
                    COLUMN2_IS_CURRENT + " integer, " +
                    COLUMN2_NOTIFICATIONS_TIME + " text " +

                    " )");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL3_ANSWERS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN3_USER_ID + " integer, " +
                    COLUMN3_WORD_ID + " integer, " +
                    COLUMN3_DATE_TIME + " text, " +
                    COLUMN3_RESULT + " text, " +
                    COLUMN3_IS_RIGHT + " integer, " +
                    " FOREIGN KEY(" + COLUMN3_USER_ID + ") " +
                    "REFERENCES " + TBL2_USERS + "(" + COLUMN_ID + ")," +
                    " FOREIGN KEY(" + COLUMN3_WORD_ID + ") " +
                    "REFERENCES " + TBL_WORDS + "(" + COLUMN_ID + ")" +
                    " )");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL4_MEANINGS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN4_WORD_ID + " integer, " +
                    COLUMN4_PART_OF_SPEECH + " text, " +
                    TBL5_DEFINITIONS + " text, " +
                    TBL6_EXAMPLES + " text, " +
                    TBL7_SYNONYMS + " text, " +
                    TBL8_ANTONYMS + " text, " +
                    " FOREIGN KEY(" + COLUMN4_WORD_ID + ") " +
                    "REFERENCES " + TBL_WORDS + "(" + COLUMN_ID + ")" +
                    " )");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL5_DEFINITIONS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN_MEANING_ID + " integer, " +
                    COLUMN5_DEFINITION + " text, " +
                    " FOREIGN KEY(" + COLUMN_MEANING_ID + ") " +
                    "REFERENCES " + TBL4_MEANINGS + "(" + COLUMN_ID + ")" +
                    " )");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL6_EXAMPLES +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN_MEANING_ID + " integer, " +
                    COLUMN6_EXAMPLE + " text, " +
                    " FOREIGN KEY(" + COLUMN_MEANING_ID + ") " +
                    "REFERENCES " + TBL4_MEANINGS + "(" + COLUMN_ID + ")" +
                    " )");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL7_SYNONYMS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN_MEANING_ID + " integer, " +
                    COLUMN7_SYNONYM + " text, " +
                    " FOREIGN KEY(" + COLUMN_MEANING_ID + ") " +
                    "REFERENCES " + TBL4_MEANINGS + "(" + COLUMN_ID + ")" +
                    " )");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL8_ANTONYMS +
                    "(" + COLUMN_ID + " integer primary key AUTOINCREMENT," +
                    COLUMN_MEANING_ID + " integer, " +
                    COLUMN8_ANTONYM + " text, " +
                    " FOREIGN KEY(" + COLUMN_MEANING_ID + ") " +
                    "REFERENCES " + TBL4_MEANINGS + "(" + COLUMN_ID + ")" +
                    " )");

        } catch (SQLException E) {
            Log.e("onCreate() ", E.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public int addWordToDB(String word, String translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int right_answers = 0;
        String date = String.valueOf(Calendar.getInstance().getTime());

        contentValues.put(COLUMN_WORD, word);
        contentValues.put(DbHelper.COLUMN_TRANSLATION, translation);
        contentValues.put(DbHelper.COLUMN_DATE_CREATION, date);
        contentValues.put(DbHelper.COLUMN_RIGHT_ANSWERS_NUM, right_answers);

        long result = db.insert(TBL_WORDS, null, contentValues);

        //if date as inserted incorrectly it will return -1
        return (int) result;
    }

    public boolean updateWord(WordModel word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WORD, word.getWord());
        contentValues.put(COLUMN_TRANSLATION, word.getTranslation());
        contentValues.put(COLUMN_DATE_CREATION, word.getDateCreation());
        contentValues.put(COLUMN_RIGHT_ANSWERS_NUM, word.getRightAnswers());
        long result = db.update(TBL_WORDS, contentValues, "_id = ?", new String[]{word.getId()});
        return result != -1;
    }

    public boolean updateWordFromDictionary(WordModel word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PHONETIC, word.getPhonetic());
        contentValues.put(COLUMN_AUDIO, word.getAudio());
        contentValues.put(COLUMN_SOURCE_URL, word.getSourceUrl());
        contentValues.put(COLUMN_ORIGIN, word.getOrigin());
        long result = db.update(TBL_WORDS, contentValues, "_id = ?", new String[]{word.getId()});
        return result != -1;
    }

    public boolean updateMeaning(MeaningModel meaningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN4_WORD_ID, meaningModel.getWordId());
        contentValues.put(COLUMN4_PART_OF_SPEECH, meaningModel.getPartOfSpeech());
        contentValues.put(TBL5_DEFINITIONS, meaningModel.getDefinitions().toString());
      /*  contentValues.put(TBL_EXAMPLES, meaningModel.getExamples().toString());
        contentValues.put(TBL_SYNONYMS, meaningModel.getSynonyms().toString());
        contentValues.put(TBL_ANTONYMS, meaningModel.getAntonyms().toString());*/
        long result = db.update(TBL4_MEANINGS, contentValues, "_id = ?",
                new String[]{meaningModel.getId()});
        return result != -1;
    }

    public boolean updateDefinition(DefinitionModel definitionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEANING_ID, definitionModel.getMeaningId());
        contentValues.put(COLUMN5_DEFINITION, definitionModel.getMeaningPart());
        long result = db.update(TBL5_DEFINITIONS, contentValues, "_id = ?",
                new String[]{definitionModel.getId()});
        return result != -1;
    }

    public boolean addUserToDB(UserModel userModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN2_USER_NAME, userModel.getUser_name());
        contentValues.put(COLUMN2_EMAIL, userModel.getEmail());
        contentValues.put(COLUMN2_INTERFACE_LANGUAGE, userModel.getInterfaceLanguage());
        contentValues.put(COLUMN2_MAIN_LANGUAGE, userModel.getMain_language());
        contentValues.put(COLUMN2_LEARNING_LANGUAGE, userModel.getLearning_language());
        contentValues.put(COLUMN2_DAILY_LOAD, userModel.getDaily_load());
        contentValues.put(COLUMN2_DATE_CREATED, userModel.getDateCreated());
        contentValues.put(COLUMN2_IS_CURRENT, 1);
        contentValues.put(COLUMN2_NOTIFICATIONS_TIME, userModel.getNotificationsTime());

        long result = db.insert(TBL2_USERS, null, contentValues);

        //if data was inserted incorrectly it will return -1
        return result != -1;
    }


    public String addMeaningToDB2(String wordId, String partOfSpeech) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN4_WORD_ID, wordId);
        contentValues.put(COLUMN4_PART_OF_SPEECH, partOfSpeech);

        return String.valueOf(db.insert(TBL4_MEANINGS, null, contentValues));
    }

    public String addDefinitionToDB2(String meaningId, String definition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEANING_ID, meaningId);
        contentValues.put(COLUMN5_DEFINITION, definition);
        return String.valueOf(db.insert(TBL5_DEFINITIONS, null, contentValues));
    }

    public boolean updateUser(UserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN2_USER_NAME, user.getUser_name());
        contentValues.put(COLUMN2_EMAIL, user.getEmail());
        contentValues.put(COLUMN2_INTERFACE_LANGUAGE, user.getInterfaceLanguage());
        contentValues.put(COLUMN2_MAIN_LANGUAGE, user.getMain_language());
        contentValues.put(COLUMN2_LEARNING_LANGUAGE, user.getLearning_language());
        contentValues.put(COLUMN2_DAILY_LOAD, user.getDaily_load());
        contentValues.put(COLUMN2_DATE_CREATED, user.getDateCreated());
        contentValues.put(COLUMN2_IS_CURRENT, user.getIs_current());
        contentValues.put(COLUMN2_NOTIFICATIONS_TIME, user.getNotificationsTime());
        long result = db.update(TBL2_USERS, contentValues, "_id = ?", new String[]{user.getId()});
        return result != -1;
    }

    public Cursor getListContents(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + table_name, null);
    }

    public ArrayList<WordModel> getAllWords() {
        ArrayList<WordModel> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL_WORDS, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") WordModel word = new WordModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATION)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_RIGHT_ANSWERS_NUM))
                );
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return words;
    }

    public ArrayList<UserModel> getAllUsers() {
        ArrayList<UserModel> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL2_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") UserModel user = new UserModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_INTERFACE_LANGUAGE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_MAIN_LANGUAGE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_LEARNING_LANGUAGE)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN2_DAILY_LOAD))),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_DATE_CREATED)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_IS_CURRENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN2_NOTIFICATIONS_TIME))
                );
                // Adding user record to list
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    @SuppressLint("Range")
    public ArrayList<AnswerModel> getAllAnswers() {
        ArrayList<AnswerModel> answers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TBL3_ANSWERS, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zz yyyy", Locale.ENGLISH);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") AnswerModel answer = null;
                try {
                    answer = new AnswerModel(
                            cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_USER_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_WORD_ID)),
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy", Locale.ENGLISH)
                                    .parse(cursor.getString(cursor.getColumnIndex(COLUMN3_DATE_TIME))),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_RESULT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_IS_RIGHT))
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return answers;
    }

    public boolean addAnswerToDB(AnswerModel answerModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zz yyyy", Locale.ENGLISH);
        String dateTime = dateFormat.format(answerModel.getDateTime());
        contentValues.put(COLUMN3_USER_ID, answerModel.getUserId());
        contentValues.put(COLUMN3_WORD_ID, answerModel.getWordId());
        contentValues.put(COLUMN3_DATE_TIME, dateTime);
        contentValues.put(COLUMN3_RESULT, answerModel.getResult());
        contentValues.put(COLUMN3_IS_RIGHT, answerModel.getIsRight());
        long result = db.insert(TBL3_ANSWERS, null, contentValues);
        return result != -1;
    }

    public boolean delWordByID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{idWord};
        long result = db.delete(TBL_WORDS, whereClause, whereArgs);
        return result != -1;
    }

    public boolean delAnswersByWordID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_word_id=?";
        String[] whereArgs = new String[]{idWord};
        long result = db.delete(TBL3_ANSWERS, whereClauseAns, whereArgs);
        return result != -1;
    }

    public boolean delMeaningsByWordID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_word_id=?";
        String[] whereArgs = new String[]{idWord};
        long result = db.delete(TBL4_MEANINGS, whereClauseAns, whereArgs);
        return result != -1;
    }

    @SuppressLint("Range")
    public WordModel wordByID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL_WORDS + " where _id = " + idWord, null);
        WordModel word = null;
        if (cursor.moveToFirst()) {
            do {
                word = new WordModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATION)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_RIGHT_ANSWERS_NUM))
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return word;
    }

    @SuppressLint("Range")
    public ArrayList<MeaningModel> meaningsByWordID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL4_MEANINGS + " where " + COLUMN4_WORD_ID + " = " + idWord, null);
        ArrayList<MeaningModel> meanings = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                MeaningModel meaning = new MeaningModel();
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                meaning.setId(id);
                meaning.setWordId(cursor.getString(cursor.getColumnIndex(COLUMN4_WORD_ID)));
                meaning.setPartOfSpeech(cursor.getString(cursor.getColumnIndex(COLUMN4_PART_OF_SPEECH)));
                meaning.setDefinitionModels(definitionsByMeaningID2(id));
                meaning.setExampleModels(examplesByMeaningId(id));
                meaning.setSynonymModels(synonymsByMeaningId(id));
                meaning.setAntonymModels(antonymsByMeaningId(id));
                meanings.add(meaning);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meanings;
    }

    @SuppressLint("Range")
    private ArrayList<AntonymModel> antonymsByMeaningId(String meaningId) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL8_ANTONYMS + " where " + COLUMN_MEANING_ID + " = " + meaningId, null);
        ArrayList<AntonymModel> antonyms = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                AntonymModel antonym = new AntonymModel();
                antonym.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                antonym.setMeaningId(meaningId);
                antonym.setMeaningPart(cursor.getString(cursor.getColumnIndex(COLUMN8_ANTONYM)));
                antonyms.add(antonym);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return antonyms;
    }

    @SuppressLint("Range")
    private ArrayList<SynonymModel> synonymsByMeaningId(String meaningId) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL7_SYNONYMS + " where " + COLUMN_MEANING_ID + " = " + meaningId, null);
        ArrayList<SynonymModel> synonyms = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SynonymModel synonym = new SynonymModel();
                synonym.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                synonym.setMeaningId(meaningId);
                synonym.setMeaningPart(cursor.getString(cursor.getColumnIndex(COLUMN7_SYNONYM)));
                synonyms.add(synonym);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return synonyms;

    }

    @SuppressLint("Range")
    private ArrayList<ExampleModel> examplesByMeaningId(String meaningId) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL6_EXAMPLES + " where " + COLUMN_MEANING_ID + " = " + meaningId, null);
        ArrayList<ExampleModel> examples = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ExampleModel example = new ExampleModel();
                example.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                example.setMeaningId(meaningId);
                example.setMeaningPart(cursor.getString(cursor.getColumnIndex(COLUMN6_EXAMPLE)));
                examples.add(example);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return examples;
    }

    @SuppressLint("Range")
    private ArrayList<DefinitionModel> definitionsByMeaningID2(String meaningId) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL5_DEFINITIONS + " where " + COLUMN_MEANING_ID + " = " + meaningId, null);
        ArrayList<DefinitionModel> definitions = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                DefinitionModel def = new DefinitionModel();
                def.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                def.setMeaningId(meaningId);
                def.setMeaningPart(cursor.getString(cursor.getColumnIndex(COLUMN5_DEFINITION)));
                definitions.add(def);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return definitions;
    }


    public boolean delDefinitionById(String idDef) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_id=?";
        String[] whereArgs = new String[]{idDef};
        long result = db.delete(TBL5_DEFINITIONS, whereClauseAns, whereArgs);
        return result != -1;
    }

    public void delDefinitionsByMeaningId(String idMeaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_meaning_id=?";
        String[] whereArgs = new String[]{idMeaning};
        db.delete(TBL5_DEFINITIONS, whereClauseAns, whereArgs);
        db.close();
    }


    public void updateExample(ExampleModel exampleModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, exampleModel.getId());
        contentValues.put(COLUMN_MEANING_ID, exampleModel.getMeaningId());
        contentValues.put(COLUMN6_EXAMPLE, exampleModel.getMeaningPart());
        db.update(TBL6_EXAMPLES, contentValues, "_id = ?", new String[]{exampleModel.getId()});
        db.close();
    }

    public void delExampleById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_id=?";
        String[] whereArgs = new String[]{id};
        db.delete(TBL6_EXAMPLES, whereClauseAns, whereArgs);
        db.close();
    }

    public String addExample(String meaningId, String example) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEANING_ID, meaningId);
        contentValues.put(COLUMN6_EXAMPLE, example);
        return String.valueOf(db.insert(TBL6_EXAMPLES, null, contentValues));
    }

    public void updateSynonym(SynonymModel synonym) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, synonym.getId());
        contentValues.put(COLUMN_MEANING_ID, synonym.getMeaningId());
        contentValues.put(COLUMN7_SYNONYM, synonym.getMeaningPart());
        db.update(TBL7_SYNONYMS, contentValues, "_id = ?", new String[]{synonym.getId()});
        db.close();
    }

    public void updateAntonym(AntonymModel antonym) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, antonym.getId());
        contentValues.put(COLUMN_MEANING_ID, antonym.getMeaningId());
        contentValues.put(COLUMN8_ANTONYM, antonym.getMeaningPart());
        db.update(TBL8_ANTONYMS, contentValues, "_id = ?", new String[]{antonym.getId()});
        db.close();
    }

    public void delSynonymById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_id=?";
        String[] whereArgs = new String[]{id};
        db.delete(TBL7_SYNONYMS, whereClauseAns, whereArgs);
        db.close();
    }

    public void delAntonymById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClauseAns = "_id=?";
        String[] whereArgs = new String[]{id};
        db.delete(TBL8_ANTONYMS, whereClauseAns, whereArgs);
        db.close();
    }

    public String addSynonym(String idMeaning, String res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEANING_ID, idMeaning);
        contentValues.put(COLUMN7_SYNONYM, res);
        return String.valueOf(db.insert(TBL7_SYNONYMS, null, contentValues));
    }

    public String addAntonym(String idMeaning, String res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEANING_ID, idMeaning);
        contentValues.put(COLUMN8_ANTONYM, res);
        return String.valueOf(db.insert(TBL8_ANTONYMS, null, contentValues));
    }

    @SuppressLint("Range")
    public ArrayList<AnswerModel> getAnswersByWordID(String idWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TBL3_ANSWERS + " where " + COLUMN3_WORD_ID + " = " + idWord, null);
        ArrayList<AnswerModel> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do { AnswerModel answer= null;
                try {
                    answer = new AnswerModel(
                            cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_USER_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_WORD_ID)),
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy", Locale.ENGLISH)
                                    .parse(cursor.getString(cursor.getColumnIndex(COLUMN3_DATE_TIME))),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_RESULT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN3_IS_RIGHT))
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                answers.add(answer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return answers;
    }
}
