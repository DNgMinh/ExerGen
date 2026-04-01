package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.Exercise;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExercisePickerFragment extends BottomSheetDialogFragment {

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(Exercise exercise);
    }

    private ExerciseUseCase exerciseUseCase;
    private OnExerciseSelectedListener selectionListener;
    private ExerciseAdapter adapter;
    private List<Exercise> allExercises;

    public void setDependencies(ExerciseUseCase exerciseUseCase) {
        this.exerciseUseCase = exerciseUseCase;
    }

    public void setOnExerciseSelectedListener(OnExerciseSelectedListener listener) {
        this.selectionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        EditText etSearch = view.findViewById(R.id.et_picker_search);
        RecyclerView rvExercises = view.findViewById(R.id.rv_picker_exercises);

        allExercises = exerciseUseCase.getFilteredExercises(); // Using existing filtering logic if any
        
        adapter = new ExerciseAdapter(buildItems(allExercises), exercise -> {
            if (selectionListener != null) {
                selectionListener.onExerciseSelected(exercise);
            }
            dismiss();
        });

        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        List<Exercise> filtered = allExercises.stream()
                .filter(ex -> ex.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        adapter.setItems(buildItems(filtered));
    }

    private List<ExerciseListItem> buildItems(List<Exercise> exercises) {
        List<ExerciseListItem> items = new ArrayList<>();
        for (Exercise ex : exercises) {
            items.add(new ExerciseListItem(ex, ex.getName(), ""));
        }
        return items;
    }
}
