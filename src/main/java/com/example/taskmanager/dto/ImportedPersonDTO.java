package com.example.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportedPersonDTO {
    private Long id;
    private String name;
    private Long bossId;
    private List<ImportedTaskDTO> tasks;
}
