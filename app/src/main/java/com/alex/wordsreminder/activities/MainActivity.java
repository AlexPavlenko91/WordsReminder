package com.alex.wordsreminder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.fragments.PracticeFragment;
import com.alex.wordsreminder.fragments.TrainingFragment;
import com.alex.wordsreminder.fragments.UserFragment;
import com.alex.wordsreminder.fragments.WordsFragment;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.services.AnswerDataService;
import com.alex.wordsreminder.services.WordDataService;
import com.alex.wordsreminder.utils.DbHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final UserFragment userFragment = UserFragment.newInstance();
    private final WordsFragment wordListFragment = new WordsFragment();
    private final TrainingFragment trainingFragment = new TrainingFragment();
    private final PracticeFragment practiceFragment = new PracticeFragment();
    private UserModel currentUser = UserModel.newInstance();
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ArrayList<UserModel> users = DbHelper.getInstance(this).getAllUsers();
        ArrayList<AnswerModel> answers = DbHelper.getInstance(this).getAllAnswers();
        ArrayList<WordModel> words = DbHelper.getInstance(this).getAllWords();
        WordDataService wordDataService = new WordDataService();
        if (!users.isEmpty()) {
            currentUser = users.get(users.size() - 1);
            setLocale(currentUser.getInterfaceLanguage());
        }
        ProgressBar progressBarDailyGoal = findViewById(R.id.progress_bar_daily_goal);
        TextView tvDailyProgress = findViewById(R.id.tv_daily_goal);
        TextView tvActiveDays = findViewById(R.id.tv_active_days);

        int rightAns = AnswerDataService.countRightAnswers(answers);
        String activeDays = String.valueOf(AnswerDataService.countActiveDays(answers));

        tvActiveDays.setText(activeDays);


        float dailyLoad = (float) currentUser.getDaily_load();
        int dailyProgress = (int) (((float) rightAns / dailyLoad) * 100);

        progressBarDailyGoal.setProgress(dailyProgress);
        String tvDailyProgText = (rightAns) + "/" + currentUser.getDaily_load();

        tvDailyProgress.setText(tvDailyProgText);

        if (rightAns >= dailyLoad) {
            tvDailyProgress.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else tvDailyProgress.setTextColor(ContextCompat.getColor(this, R.color.black));
        if (rightAns > 0) {
            tvActiveDays.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else tvActiveDays.setTextColor(ContextCompat.getColor(this, R.color.black));

        Bundle bundle = new Bundle();
        bundle.putString("userId", currentUser.getId());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.page1_profile:
                    getSupportFragmentManager().beginTransaction().replace
                            (R.id.fragment_container_view, userFragment).commit();
                    break;
                case R.id.page2_word_list:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container_view, wordListFragment).commit();
                    break;
                case R.id.page3_training:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container_view, trainingFragment).commit();
                    break;
                case R.id.page4_practice:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container_view, practiceFragment).commit();
                    break;
            }
            return true;
        });
        if (words.size() > 0) {
            bottomNavigationView.setSelectedItemId(R.id.page3_training);
        } else bottomNavigationView.setSelectedItemId(R.id.page2_word_list);
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    public void onButtonShowPopupWindowClick(View view) {
        if (wordListFragment != null) {
            wordListFragment.onAddNewWordClick(view);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container_view, wordListFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.words_list_upper_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.apply();
        switch (item.getItemId()) {
            case R.id.all_words:
                Toast.makeText(this, R.string.all_words, Toast.LENGTH_SHORT).show();
                sharedPrefEditor.putString("WORDS_SELECTION", "all_words");
                sharedPrefEditor.apply();
                updateFragment();
                return true;

            case R.id.being_studied_words:
                Toast.makeText(this, R.string.being_studied_words, Toast.LENGTH_SHORT).show();
                sharedPrefEditor.putString("WORDS_SELECTION", "being_studied_words");
                sharedPrefEditor.apply();
                updateFragment();
                return true;
            case R.id.learned_words:
                Toast.makeText(this, R.string.learned_words, Toast.LENGTH_SHORT).show();
                sharedPrefEditor.putString("WORDS_SELECTION", "learned_words");
                sharedPrefEditor.apply();
                updateFragment();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
    }

    private void updateFragment() {
        if (bottomNavigationView.getSelectedItemId() == R.id.page2_word_list) {
            getSupportFragmentManager().beginTransaction().detach(wordListFragment).commit();
            getSupportFragmentManager().beginTransaction().attach(wordListFragment).commit();

        } else if (bottomNavigationView.getSelectedItemId() == R.id.page3_training) {
            getSupportFragmentManager().beginTransaction().detach(trainingFragment).commit();
            getSupportFragmentManager().beginTransaction().attach(trainingFragment).commit();
        }
    }

    public void setLocale(String langInterface) {
        String languageCode = "en";
        switch (langInterface) {
            case "English":
                languageCode = "en";
                break;
            case "Українська":
                languageCode = "uk";
                break;
        }
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


}