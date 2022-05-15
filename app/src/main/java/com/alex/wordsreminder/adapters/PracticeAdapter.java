package com.alex.wordsreminder.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.wordsreminder.R;
import com.alex.wordsreminder.models.BaseMeaning;
import com.alex.wordsreminder.models.MeaningModel;
import com.alex.wordsreminder.presentation.PopupClass;

import java.util.ArrayList;
import java.util.List;

public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.PracticeViewHolder> {
    public final PracticeClickListener practiceClickListener;
    private final List<MeaningModel> meaningModels;


    public PracticeAdapter(PracticeClickListener practiceClickListener, List<MeaningModel> meaningModels) {
        this.practiceClickListener = practiceClickListener;
        this.meaningModels = meaningModels;
    }

    @NonNull
    @Override
    public PracticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.practice_item, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MeaningModel meaningModel = meaningModels.get(position);

        PopupClass popupClass = new PopupClass(holder.linearLayoutMeaning.getContext());

        ArrayList<BaseMeaning> definitions = new ArrayList<>(meaningModel.getDefinitionModels());
        MeaningPartAdapter definitionsAdapter = new MeaningPartAdapter(holder.itemView.getContext(),
                0, definitions);
        holder.lvDefinitions.setAdapter(definitionsAdapter);
        definitionsAdapter.setDynamicHeight(holder.lvDefinitions);
        holder.lvDefinitions.setOnItemClickListener((adapterView, view, i, l) ->
                popupClass.changeMeaningPart(definitionsAdapter, holder.lvDefinitions, definitions, i, adapterView, "definition"));
        holder.btnAddDefinition.setOnClickListener(view -> popupClass.addMeaningPart(
                view, definitionsAdapter, holder.lvDefinitions, definitions, meaningModel.getId(), "definition"));

        ArrayList<BaseMeaning> examples = new ArrayList<>(meaningModel.getExampleModels());
        MeaningPartAdapter examplesAdapter = new MeaningPartAdapter(holder.itemView.getContext(),
                0, examples);
        examplesAdapter.setDynamicHeight(holder.lvExamples);
        holder.lvExamples.setAdapter(examplesAdapter);
        holder.lvExamples.setOnItemClickListener((adapterView, view, i, l) ->
                popupClass.changeMeaningPart(examplesAdapter, holder.lvExamples, examples, i, adapterView, "example"));
        holder.btnAddExample.setOnClickListener(view -> popupClass.addMeaningPart(
                view, examplesAdapter, holder.lvExamples, examples, meaningModel.getId(), "example"));

        ArrayList<BaseMeaning> synonyms = new ArrayList<>(meaningModel.getSynonymModels());
        MeaningPartAdapter synonymsAdapter = new MeaningPartAdapter(holder.itemView.getContext(),
                0, synonyms);
        synonymsAdapter.setDynamicHeight(holder.lvSynonyms);
        holder.lvSynonyms.setAdapter(synonymsAdapter);
        holder.lvSynonyms.setOnItemClickListener((adapterView, view, i, l) ->
                popupClass.changeMeaningPart(synonymsAdapter, holder.lvSynonyms, synonyms, i, adapterView, "synonym"));
        holder.btnAddSynonym.setOnClickListener(view -> popupClass.addMeaningPart(
                view, synonymsAdapter, holder.lvSynonyms, synonyms, meaningModel.getId(), "synonym"));

        ArrayList<BaseMeaning> antonyms = new ArrayList<>(meaningModel.getAntonymModels());
        MeaningPartAdapter antonymsAdapter = new MeaningPartAdapter(holder.itemView.getContext(),
                0, antonyms);
        antonymsAdapter.setDynamicHeight(holder.lvAntonyms);
        holder.lvAntonyms.setAdapter(antonymsAdapter);
        holder.lvAntonyms.setOnItemClickListener((adapterView, view, i, l) ->
                popupClass.changeMeaningPart(antonymsAdapter, holder.lvAntonyms, antonyms, i, adapterView, "antonym"));
        holder.btnAddAntonym.setOnClickListener(view -> popupClass.addMeaningPart(
                view, antonymsAdapter, holder.lvAntonyms, antonyms, meaningModel.getId(), "antonym"));

        holder.tvPartOfSpeech.setText(meaningModel.getPartOfSpeech());
        holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.linearLayoutMeaning.setVisibility(View.VISIBLE);
            } else holder.linearLayoutMeaning.setVisibility(View.GONE);
        });
        holder.itemView.setOnClickListener(v ->
                practiceClickListener.onMeaningClick(meaningModel, position));

    }

    @Override
    public int getItemCount() {
        return meaningModels.size();
    }

    public void updateItem(int position, MeaningModel meaningModel) {
        meaningModels.set(position, meaningModel);
        notifyItemChanged(position);
    }

    public interface PracticeClickListener {
        void onMeaningClick(MeaningModel meaningModel, int position);
    }

    public static class PracticeViewHolder extends RecyclerView.ViewHolder {

        final TextView tvPartOfSpeech;
        final SwitchCompat switchCompat;
        final LinearLayout linearLayoutMeaning;
        final ListView lvDefinitions, lvExamples, lvSynonyms, lvAntonyms;
        final Button btnAddDefinition, btnAddExample, btnAddSynonym, btnAddAntonym;

        PracticeViewHolder(View view) {
            super(view);
            tvPartOfSpeech = view.findViewById(R.id.tv_part_of_speech);
            switchCompat = view.findViewById(R.id.switch1);
            linearLayoutMeaning = view.findViewById(R.id.linear_layout_meaning);
            lvDefinitions = view.findViewById(R.id.list_view_definitions);
            lvExamples = view.findViewById(R.id.list_view_examples);
            lvSynonyms = view.findViewById(R.id.list_view_synonyms);
            lvAntonyms = view.findViewById(R.id.list_view_antonyms);
            btnAddDefinition = view.findViewById(R.id.btnAddDefinition);
            btnAddExample = view.findViewById(R.id.btnAddExample);
            btnAddSynonym = view.findViewById(R.id.btnAddSynonym);
            btnAddAntonym = view.findViewById(R.id.btnAddAntonym);
        }
    }

    public static class MeaningPartAdapter extends ArrayAdapter<BaseMeaning> {
        private final Context mContext;
        private final List<BaseMeaning> meaningParts;

        public MeaningPartAdapter(@NonNull Context context, int resource, @NonNull List<BaseMeaning> objects) {
            super(context, resource, objects);
            mContext = context;
            meaningParts = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.meaning_part_item, parent, false);
            BaseMeaning definition = meaningParts.get(position);
            TextView name = (TextView) listItem.findViewById(R.id.tvMeaningPartItem);
            name.setText(definition.getMeaningPart());
            return listItem;
        }

        public void setDynamicHeight(ListView listView) {
            int totalHeight = 0;
            int adapterCount = getCount();
            for (int size = 0; size < adapterCount; size++) {
                View listItem = getView(size, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = (totalHeight
                    + (listView.getDividerHeight() * (adapterCount + 20)));
            listView.setLayoutParams(params);
        }
    }

}
