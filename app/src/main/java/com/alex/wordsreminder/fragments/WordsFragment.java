package com.alex.wordsreminder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.adapters.WordAdapter;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.presentation.PopupClass;
import com.alex.wordsreminder.services.AnswerDataService;
import com.alex.wordsreminder.utils.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class WordsFragment extends Fragment {

    private static ArrayList<UserModel> users = new ArrayList<>();
    protected ArrayList<WordModel> words = new ArrayList<>();
    protected FloatingActionButton fab;
    protected WordAdapter adapter;
    AnswerDataService answerDataService;
    private UserModel currentUser = UserModel.newInstance();
    private ProgressBar progressBarDailyGoal;
    private TextView tvDailyProgress;
    private RecyclerView recyclerView;
    private TextView tvActiveDays;

    public WordsFragment() {
    }

    public WordAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerDataService = new AnswerDataService(requireContext());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.words_list_upper_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_words: {
                Toast.makeText(requireActivity(), "all words", Toast.LENGTH_LONG).show();
                return true;
            }
            case R.id.learned_words: {
                // save profile changes
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setInitialData();

        View view = inflater.inflate(R.layout.fragment_words_list, container, false);
        progressBarDailyGoal = requireActivity().findViewById(R.id.progress_bar_daily_goal);
        tvDailyProgress = requireActivity().findViewById(R.id.tv_daily_goal);
        tvActiveDays = requireActivity().findViewById(R.id.tv_active_days);

        recyclerView = view.findViewById(R.id.word_list);
        PopupClass popUp = new PopupClass(requireContext());
        WordAdapter.OnWordClickListener onWordClickListener = (word, position) ->
                popUp.changeWordPopupWindow(this.requireView(), this, word, position);
        adapter = new WordAdapter(getContext(), words, onWordClickListener);
        recyclerView.setAdapter(adapter);

        fab = (requireActivity()).findViewById(R.id.floatingAddButton);
        fab.setVisibility(View.VISIBLE);

        return view;
    }

    public void updateAppBar() {
        answerDataService.updateProgress(tvDailyProgress, tvActiveDays, progressBarDailyGoal);
    }

    private void setInitialData() {
        words = DbHelper.getInstance(getContext()).getAllWords();
        users = DbHelper.getInstance(requireActivity()).getAllUsers();
        if (users.size() > 0) {
            currentUser = users.get(users.size() - 1);
        }
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String wordsSelection = sharedPref.getString("WORDS_SELECTION", "all_words");
        if (wordsSelection.equals("being_studied_words")) {
            words.removeIf(wordItem -> (wordItem.getRightAnswers() >= 5));
        } else if (wordsSelection.equals("learned_words")) {
            words.removeIf(wordItem -> (wordItem.getRightAnswers() < 5));
        }
    }

    public void onAddNewWordClick(View view) {
        PopupClass popUp = new PopupClass(requireContext());
        popUp.showPopupWindow(view, this);
    }


}