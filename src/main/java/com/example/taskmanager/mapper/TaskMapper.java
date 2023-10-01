package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.ImportedTaskDTO;
import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.TaskState;
import com.example.taskmanager.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final PersonRepository personRepository;
    //TODO добавить по файлу XML - дату просрочки и имя задачи

    public Task fromDTO(TaskRequestDTO taskRequestDTO) {
        return Task.builder()
                .description(taskRequestDTO.description())
                .person(personRepository.findById(taskRequestDTO.personId()).orElseThrow(IllegalArgumentException::new))    //TODO add correct business exception & handling
                .taskState(TaskState.TODO)
                .build();
    }

    public Task fromImportedDTO(ImportedTaskDTO importedTaskDTO) {
        return Task.builder()
                .description(importedTaskDTO.getDescription())
                .taskState(TaskState.TODO)
                .build();
    }

    public TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .personId(task.getPerson().getId())
                .description(task.getDescription())
                .status(task.getTaskState().getDescription())
                .build();
    }
}
