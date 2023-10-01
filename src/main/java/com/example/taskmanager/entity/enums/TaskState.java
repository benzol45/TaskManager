package com.example.taskmanager.entity.enums;

public enum TaskState {
    TODO("To do"),
    COMPLETE("Complete"),
    READY_FOR_REVIEW("Ready for review");

    private String description;

    TaskState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
