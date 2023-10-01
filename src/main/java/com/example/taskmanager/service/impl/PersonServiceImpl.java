package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.ImportedPersonDTO;
import com.example.taskmanager.entity.Person;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.PersonMapper;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.PersonRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final TaskRepository taskRepository;
    private final PersonMapper personMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<Person> getSubordinatesByBossId(Long bossId) {
        Person boss = personRepository.findById(bossId).orElseThrow(()->new IllegalArgumentException());  //TODO add correct business exception & handling
        return personRepository.findAllByBoss(boss);
    }

    @Override
    public Long create(ImportedPersonDTO importedPersonDTO) {
        if (importedPersonDTO.getBossId()!=null) {
            personRepository.findById(importedPersonDTO.getBossId()).orElseThrow(()->new IllegalStateException("Can't find boss with id=" + importedPersonDTO.getBossId()));
        }

        Person person = personMapper.mapFromImportedDTO(importedPersonDTO);
        Person finalPerson = person;
        person.getTasks().forEach(task -> task.setPerson(finalPerson));
        person = personRepository.save(person);
        person.setBoss(importedPersonDTO.getBossId()==null ? null : personRepository.findById(importedPersonDTO.getBossId()).get());

        return person.getId();
    }

    @Override
    public void update(ImportedPersonDTO importedPersonDTO) {
        Person person = personRepository.findById(importedPersonDTO.getId()).orElseThrow(() -> new IllegalStateException("Can't find person for update with id=" + importedPersonDTO.getId()));

        if (importedPersonDTO.getBossId()!=null) {
            personRepository.findById(importedPersonDTO.getBossId()).orElseThrow(()->new IllegalStateException("Can't find boss with id=" + importedPersonDTO.getBossId()));
        }

        if (importedPersonDTO.getName()!=null) {
            person.setName(importedPersonDTO.getName());
        }

        if (importedPersonDTO.getBossId()!=null) {
            person.setBoss(personRepository.findById(importedPersonDTO.getBossId()).get());
        }
        personRepository.save(person);

        if (importedPersonDTO.getTasks()!=null) {
            List<Task> tasks = importedPersonDTO.getTasks().stream().map(taskMapper::fromImportedDTO).toList();
            tasks.forEach(task -> {
                task.setPerson(person);
                taskRepository.save(task);
            });
        }
    }
}
