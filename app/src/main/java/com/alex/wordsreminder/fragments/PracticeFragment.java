package com.alex.wordsreminder.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.adapters.PracticeAdapter;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.DefinitionModel;
import com.alex.wordsreminder.models.ExampleModel;
import com.alex.wordsreminder.models.MeaningModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.services.AnswerDataService;
import com.alex.wordsreminder.services.DictionaryDataService;
import com.alex.wordsreminder.services.WordDataService;
import com.alex.wordsreminder.utils.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PracticeFragment extends Fragment {

    private static final String ARG_PARAM_WORD = "wordId";
    ArrayList<MeaningModel> meaningModels;
    DbHelper dbHelper;
    WordDataService wordDataService;
    String description = "";
    private WordModel currentWord;
    private AnswerDataService answerDataService;
    private DictionaryDataService dictionaryDataService;
    private PracticeAdapter.PracticeClickListener practiceClickListener;
    private RecyclerView recyclerView;
    private TextView tvDefinition;
    private ProgressBar progressBarPreLoading;
    private Button btnReload, btnCheckAnswer;
    private EditText etAnswer;
    private final TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!etAnswer.getText().toString().trim().equals(""))
                btnCheckAnswer.setVisibility(View.VISIBLE);
            else btnCheckAnswer.setVisibility(View.INVISIBLE);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };
    private PracticeAdapter practiceAdapter;
    private ProgressBar progressBarDailyGoal;
    private TextView tvDailyProgress, tvActiveDays;
    private ImageButton ibReload;
    private ArrayList<WordModel> words;
    private ArrayList<AnswerModel> answers;

    public PracticeFragment() {
        // Required empty public constructor
    }

    public static PracticeFragment newInstance(String theWord) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_WORD, theWord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dictionaryDataService = new DictionaryDataService(requireContext());
        dbHelper = DbHelper.getInstance(requireContext());
        words = dbHelper.getAllWords();
        wordDataService = new WordDataService();
        currentWord = wordDataService.leastLearnedWord(words);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.practice_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            case R.id.reload_dictionary: {
                getDictionaryFromAPI();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FloatingActionButton fab = requireActivity().findViewById(R.id.floatingAddButton);
        fab.setVisibility(View.INVISIBLE);

        View view = inflater.inflate(R.layout.fragment_practice, container, false);
        btnReload = view.findViewById(R.id.btn_reload);
        tvDefinition = view.findViewById(R.id.tv_definition);
        progressBarPreLoading = view.findViewById(R.id.progress_bar_pre_loading);
        btnCheckAnswer = view.findViewById(R.id.btn_check_ans_practice);
        etAnswer = view.findViewById(R.id.et_practice_ans);

        recyclerView = view.findViewById(R.id.recycler_view_practice_list);
        recyclerView.setHasFixedSize(false);
        progressBarDailyGoal = requireActivity().findViewById(R.id.progress_bar_daily_goal);
        tvDailyProgress = requireActivity().findViewById(R.id.tv_daily_goal);
        tvActiveDays = requireActivity().findViewById(R.id.tv_active_days);
        ibReload = view.findViewById(R.id.ibtn_reload);
        ibReload.setOnClickListener(view1 -> {
            setTvDescription();
        });

        btnCheckAnswer.setOnClickListener(v -> {
            String answerStr = etAnswer.getText().toString().trim();
            answerDataService = new AnswerDataService(requireContext());
            answerDataService.onCheckAnsClick(currentWord, answerStr, tvDailyProgress, tvActiveDays, progressBarDailyGoal);
            etAnswer.setText("");
            btnCheckAnswer.setVisibility(View.INVISIBLE);
            words = dbHelper.getAllWords();
            currentWord = wordDataService.leastLearnedWord(words);
            answers = dbHelper.getAnswersByWordID(currentWord.getId());
            AnswerDataService.isAnsweredFor5Hours(answers, currentWord.getId());
            description = "";
            if (!AnswerDataService.isAnsweredFor5Hours(answers, currentWord.getId()))
                getDictionaryByWord();
        });

        etAnswer.addTextChangedListener(textWatcher);
        practiceClickListener = (meaningModel, position) -> {
            Toast.makeText(getContext(), position + " Position was selected ", Toast.LENGTH_SHORT).show();
        };

        if (currentWord != null) {
            getDictionaryByWord();
        } else {
            Toast.makeText(requireContext(), "Add some words at first!", Toast.LENGTH_LONG).show();
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void getDictionaryFromAPI() {
        DbHelper.getInstance(requireContext()).delMeaningsByWordID(currentWord.getId());
        for (MeaningModel mean : meaningModels) {
            DbHelper.getInstance(requireContext()).delDefinitionsByMeaningId(mean.getId());
        }
        dictionaryDataService.getDictionaryByWordFromAPI(currentWord, new DictionaryDataService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                progressBarPreLoading.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                btnReload.setVisibility(View.VISIBLE);
                btnReload.setOnClickListener(view1 -> {
                            progressBarPreLoading.setVisibility(View.VISIBLE);
                            getDictionaryByWord();
                            btnReload.setVisibility(View.INVISIBLE);
                        }
                );
            }

            @Override
            public void onResponse(List<MeaningModel> meaningModels, WordModel wordModel) {
                progressBarPreLoading.setVisibility(View.GONE);
                btnReload.setVisibility(View.INVISIBLE);
                etAnswer.setVisibility(View.VISIBLE);
                ibReload.setVisibility(View.VISIBLE);
                practiceAdapter = new PracticeAdapter(practiceClickListener, meaningModels);
                recyclerView.setAdapter(practiceAdapter);
                setTvDescription();
                //tvDefinition.setText(meaningModels.get(0).getDefinitionModels().get(0).getMeaningPart());
            }
        });
    }

    public void getDictionaryByWord() {
        meaningModels = DbHelper.getInstance(requireContext()).meaningsByWordID(currentWord.getId());
        if (meaningModels.isEmpty()) {
            getDictionaryFromAPI();
        } else {
            progressBarPreLoading.setVisibility(View.GONE);
            btnCheckAnswer.setVisibility(View.INVISIBLE);
            etAnswer.setVisibility(View.VISIBLE);
            ibReload.setVisibility(View.VISIBLE);
            practiceAdapter = new PracticeAdapter(practiceClickListener, meaningModels);
            recyclerView.setAdapter(practiceAdapter);
            setTvDescription();
        }
    }

    public void setTvDescription() {
        for (int i = 0; i < meaningModels.size(); i++) {
            MeaningModel mean = meaningModels.get(i);
            ArrayList<ExampleModel> examples = mean.getExampleModels();
            if (examples.isEmpty()) continue;
            Random ran = new Random();
            int x = ran.nextInt(examples.size());
            if (!Objects.equals(description, examples.get(x).getMeaningPart())) {
                description = examples.get(x).getMeaningPart();
                break;
            }
        }

        if (Objects.equals(description, "")) {
            for (MeaningModel mean : meaningModels) {
                ArrayList<DefinitionModel> definitions = mean.getDefinitionModels();
                if (definitions.isEmpty()) continue;
                for (DefinitionModel def : definitions) {
                    if (!Objects.equals(description, def.getMeaningPart())) {
                        description = def.getMeaningPart();
                        break;
                    }
                }
            }
        }
        Spannable resSpan = new SpannableString(description);
        if (description.toLowerCase().contains(currentWord.getWord().toLowerCase())) {
            int i = description.toLowerCase().indexOf(currentWord.getWord().toLowerCase());
            int j = i + currentWord.getWord().length();
            resSpan.setSpan(new ForegroundColorSpan(Color.BLUE), i,
                    j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvDefinition.setText(resSpan);
    }


}