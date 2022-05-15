package com.alex.wordsreminder.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.models.WordModel;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    public final OnWordClickListener onClickListener;
    private final LayoutInflater inflater;
    private final ArrayList<WordModel> words;


    public WordAdapter(Context context, ArrayList<WordModel> words, OnWordClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.words = words;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.word_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        WordModel word = words.get(position);
        holder.wordView.setText(word.getWord());
        holder.translationView.setText(word.getTranslation());
        int mProgressStatus = word.getRightAnswers() * 20;

        holder.itemView.setOnClickListener(v -> onClickListener.onWordClick(word, position));

        holder.progressBar.setProgress(mProgressStatus);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void addItem(WordModel wordModel) {
        words.add(wordModel);
        notifyItemInserted(words.size() - 1);
    }

    public void updateItem(int position, WordModel wordModel) {
        words.set(position, wordModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        words.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnWordClickListener {
        void onWordClick(WordModel state, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView wordView, translationView;
        private final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            wordView = view.findViewById(R.id.item_word);
            translationView = view.findViewById(R.id.text_view_translation);
            progressBar = view.findViewById(R.id.progress_bar_word);
        }
    }


}