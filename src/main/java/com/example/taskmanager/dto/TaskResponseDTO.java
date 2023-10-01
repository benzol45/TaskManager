package com.example.taskmanager.dto;

import lombok.Builder;

public record TaskResponseDTO(Long id, String description, Long personId, String status) {
    @Builder
    public TaskResponseDTO {}
}
