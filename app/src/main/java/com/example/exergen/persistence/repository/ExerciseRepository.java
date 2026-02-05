package com.example.exergen.persistence.repository;

import com.example.exergen.business.model.Exercise;

import java.util.List;

public interface ExerciseRepository {

    Exercise getById(String id);

    List<Exercise> getAll();

    void save(Exercise exercise);
}
