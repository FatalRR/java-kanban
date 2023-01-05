package ru.yandex.practicum.model.tasks;

import ru.yandex.practicum.manager.TaskType;
import ru.yandex.practicum.model.Status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskList = new ArrayList<>();
    private Instant endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, Status status, Instant startTime, long duration) {
        super(name, description, status, startTime, duration);
        this.taskType = TaskType.EPIC;
        this.endTime = super.getEndTime();
    }

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void addSubTaskList(int id) {
        subTaskList.add(id);
    }

    public void clearSubTaskList() {
        subTaskList.clear();
    }

    public void removeSubTaskList(Integer id) {
        subTaskList.remove(id);
    }

    public void setEndTime (Instant endTime) {
        this.endTime=endTime;
    }

    @Override
    public  Instant getEndTime(){
        return endTime;
    }

    @Override
    public Integer getEpicId() {
        return super.getEpicId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskList, epic.subTaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskList=" + subTaskList +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", taskType=" + getTaskType() +
                ", startTime=" + getStartTime().toEpochMilli() + '\'' +
                ", endTime=" + getEndTime().toEpochMilli() + '\'' +
                ", duration=" + getDuration() +
                '}';
    }
}