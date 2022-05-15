package com.alex.wordsreminder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.adapters.TrainingAdapter;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.services.AnswerDataService;
import com.alex.wordsreminder.utils.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TrainingFragment extends Fragment {

    private static ArrayList<AnswerModel> answers;
    protected UserModel currentUser = UserModel.newInstance();
    protected TrainingAdapter trainingAdapter;
    protected FloatingActionButton fab;
    ProgressBar progressBarDailyGoal;
    TextView tvDailyProgress, tvActiveDays;
    private AnswerDataService answerDataService;
    private ArrayList<WordModel> words = new ArrayList<>();

    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment newInstance(String param1, String param2) {

        return new TrainingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            currentUser = getArguments().getParcelable("user");
        }*/
        answerDataService = new AnswerDataService(requireContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_training_list, container, false);
        setInitialData();
        progressBarDailyGoal = requireActivity().findViewById(R.id.progress_bar_daily_goal);
        tvDailyProgress = requireActivity().findViewById(R.id.tv_daily_goal);
        tvActiveDays = requireActivity().findViewById(R.id.tv_active_days);

        RecyclerView recyclerView = view.findViewById(R.id.training_list);

        TrainingAdapter.TrainingClickListener stateClickListener =
                (word, position) -> Toast.makeText(getContext(), position + " Position was selected" +
                        word.getWord(), Toast.LENGTH_SHORT).show();
        TrainingAdapter.CheckAnswerListener checkAnswerClickListener =
                (word, position, current_str, user, answer, is_right) -> {
                    answerDataService.onCheckAnsClick(word, current_str, tvDailyProgress, tvActiveDays, progressBarDailyGoal);
                    /*String user_id = currentUser.getId();
                    int dailyLoad = currentUser.getDaily_load();

                    Date date = new Date();
                    AnswerModel answerModel = new AnswerModel(
                            "-1", user_id,
                            word.getId(), date, current_str, String.valueOf(is_right)
                    );

                    DbHelper.getInstance(getContext()).addAnswerToDB(answerModel);
                    int right_answers = word.getRightAnswers();
                    right_answers += is_right;
                    word.setRightAnswers(right_answers);
                    DbHelper.getInstance(getContext()).updateWord(word);
                    answers = DbHelper.getInstance(getContext()).getAllAnswers();
                    int rightAns = AnswerDataService.countRightAnswers(answers);
                    int dailyProgress = (int) (((float) rightAns / (float) dailyLoad * 100));

                    progressBarDailyGoal.setProgress(dailyProgress);
                    String tvDailyProgText = (rightAns) + "/" + dailyLoad;
                    tvDailyProgress.setText(tvDailyProgText);*/
                };

        trainingAdapter = new TrainingAdapter(
                words, answers, stateClickListener,
                checkAnswerClickListener, requireContext());
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(trainingAdapter);

        fab = (requireActivity()).findViewById(R.id.floatingAddButton);
        fab.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setInitialData() {
        words = DbHelper.getInstance(getContext()).getAllWords();
        answers = DbHelper.getInstance(getContext()).getAllAnswers();

        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String wordsSelection = sharedPref.getString("WORDS_SELECTION", "all_words");
        if (wordsSelection.equals("being_studied_words")) {
            words.removeIf(wordItem -> (wordItem.getRightAnswers() >= 5));
        } else if (wordsSelection.equals("learned_words")) {
            words.removeIf(wordItem -> (wordItem.getRightAnswers() < 5));
        }

    }
}