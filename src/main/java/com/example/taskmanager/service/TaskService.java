package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {
    TaskResponseDTO create(TaskRequestDTO taskRequestDTO);

    List<TaskResponseDTO> getBySubordinates(Long bossId);
}
