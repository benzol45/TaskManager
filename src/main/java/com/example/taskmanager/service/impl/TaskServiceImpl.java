package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Person;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.PersonService;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final PersonService personService;


    @Override
    public TaskResponseDTO create(TaskRequestDTO taskRequestDTO) {
        Task newTask = taskMapper.fromDTO(taskRequestDTO);
        taskRepository.save(newTask);
        return (new ModelMapper()).map(newTask, TaskResponseDTO.class);
        //return taskMapper.toDTO(newTask);
    }

    @Override
    public List<TaskResponseDTO> getBySubordinates(Long bossId) {
        List<Person> subordinates = personService.getSubordinatesByBossId(bossId);
        return subordinates.stream()
                .flatMap(person -> taskRepository.findAllByPerson(person).stream())
                .distinct()
                .map(taskMapper::toDTO)
                .toList();
    }
}
