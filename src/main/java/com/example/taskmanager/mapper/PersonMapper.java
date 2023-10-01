package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.ImportedPersonDTO;
import com.example.taskmanager.entity.Person;
import com.example.taskmanager.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonMapper {
    private final PersonRepository personRepository;
    private final TaskMapper taskMapper;

    public Person mapFromImportedDTO(ImportedPersonDTO importedPersonDTO) {
        return Person.builder()
                .name(importedPersonDTO.getName())
                .tasks(importedPersonDTO.getTasks()==null ? null : importedPersonDTO.getTasks().stream().map(taskMapper::fromImportedDTO).toList())
                .build();
    }
}
