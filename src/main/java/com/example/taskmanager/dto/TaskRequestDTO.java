package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

public record TaskRequestDTO(@NotBlank String description, @NonNull Long personId) {
    @Builder
    public TaskRequestDTO {}
}
