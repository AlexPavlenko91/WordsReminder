package com.alex.wordsreminder.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.models.AnswerModel;
import com.alex.wordsreminder.models.UserModel;
import com.alex.wordsreminder.models.WordModel;
import com.alex.wordsreminder.services.AnswerDataService;

import java.util.ArrayList;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    public final TrainingClickListener onClickListener;
    public final CheckAnswerListener onCheckAnswerListener;
    private final ArrayList<WordModel> words;
    private final ArrayList<AnswerModel> answers;
    private final AnswerDataService answerDataService;
    protected UserModel currentUser;
    protected AnswerModel answer;
    protected String currentAnswerStr = "";
    private int isRightAns = 0;

    public TrainingAdapter(
            ArrayList<WordModel> words,
            ArrayList<AnswerModel> answers,
            TrainingClickListener onClickListener,
            CheckAnswerListener onCheckAnswerListener,
            Context context) {
        this.onClickListener = onClickListener;
        this.onCheckAnswerListener = onCheckAnswerListener;
        this.words = words;
        this.answers = answers;
        this.answerDataService = new AnswerDataService(context);
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.training_item, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainingViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        WordModel word = words.get(position);
        holder.word_training.setText(word.getWord());

        String wordId = word.getId();
        boolean answeredFor5Hours = answerDataService.isAnsweredFor5Hours(answers, wordId);

        if (answeredFor5Hours) {
            boolean ansWasRight = ifLastAnswerWasRight(wordId);
            String lastAnswerRes = answerDataService.lastAnswerRes(answers, wordId);
            holder.translation_answer.setText(lastAnswerRes);
            holder.translation_answer.setKeyListener(null);
            holder.i_btn_check_answer.setEnabled(false);
            if (ansWasRight) {
                holder.i_btn_check_answer.setImageResource(R.drawable.ic_correct);

            } else {
                holder.i_btn_check_answer.setImageResource(R.drawable.ic_wrong);
                holder.translation_answer.setEnabled(false);
            }
        } else {
            holder.i_btn_check_answer.setVisibility(View.INVISIBLE);
            holder.translation_answer.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    //current_str = s.toString();
                    if (!holder.translation_answer.getText().toString().trim().equals("")) {
                        holder.i_btn_check_answer.setVisibility(View.VISIBLE);
                    } else {
                        holder.i_btn_check_answer.setVisibility(View.INVISIBLE);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }
            });
            holder.i_btn_check_answer.setOnClickListener(v -> {
                currentAnswerStr = holder.translation_answer.getText().toString().trim();
                if (word.getTranslation().equals(currentAnswerStr)) {
                    holder.i_btn_check_answer.setImageResource(R.drawable.ic_correct);
                    holder.translation_answer.setKeyListener(null);
                    isRightAns = 1;

                } else {
                    holder.i_btn_check_answer.setImageResource(R.drawable.ic_wrong);
                    holder.translation_answer.setEnabled(false);
                    isRightAns = 0;
                }

                onCheckAnswerListener.onCheckAnswerClick(word, position,
                        currentAnswerStr, currentUser, answer, isRightAns);
            });
        }
        holder.itemView.setOnClickListener(v -> onClickListener.onWordClick(word, position));
    }


    private boolean ifLastAnswerWasRight(String id) {
        AnswerModel lastAnswerModel = answerDataService.lastAnswerItem(answers, id);
        int res = 0;
        if (lastAnswerModel != null) {
            res = lastAnswerModel.getIntIs_right();
        }
        return res == 1;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public interface TrainingClickListener {
        void onWordClick(WordModel word, int position);
    }

    public interface CheckAnswerListener {
        void onCheckAnswerClick(WordModel word, int position, String current_str,
                                UserModel current_user, AnswerModel answer, int is_right);
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {

        final TextView word_training;
        final EditText translation_answer;
        private final ImageButton i_btn_check_answer;

        TrainingViewHolder(View view) {
            super(view);
            word_training = view.findViewById(R.id.training_item_word);
            translation_answer = view.findViewById(R.id.et_translation_answer);
            i_btn_check_answer = view.findViewById(R.id.image_btn_check_answer);
        }
    }


}