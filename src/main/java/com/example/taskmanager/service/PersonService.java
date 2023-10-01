package com.example.taskmanager.service;

import com.example.taskmanager.dto.ImportedPersonDTO;
import com.example.taskmanager.entity.Person;

import java.util.List;

public interface PersonService {
    List<Person> getSubordinatesByBossId(Long bossId);

    Long create(ImportedPersonDTO importedPersonDTO);

    void update(ImportedPersonDTO employer);
}
