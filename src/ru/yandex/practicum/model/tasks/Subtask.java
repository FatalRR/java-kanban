package ru.yandex.practicum.model.tasks;

import ru.yandex.practicum.manager.TaskType;
import ru.yandex.practicum.model.Status;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                ", id=" + getId() +
                ", epicId=" + getEpicId() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", taskType=" + getTaskType() +
                '}';
    }
}