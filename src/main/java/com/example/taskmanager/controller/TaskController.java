package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public TaskResponseDTO createTask(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {
        return taskService.create(taskRequestDTO);
    }

    @GetMapping
    public List<TaskResponseDTO> getBySubordinates(@RequestParam("bossId") Long bossId) {
        return taskService.getBySubordinates(bossId);
    }
}
