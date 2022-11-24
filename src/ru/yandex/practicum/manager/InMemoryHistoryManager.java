package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList taskHistory = new CustomLinkedList();

    @Override
    public void add(Task task) {
        taskHistory.linkLast(task);
    }

    @Override
    public void remove(int id) {
        taskHistory.removeNode(taskHistory.getNode(id));
    }

    @Override
    public List<Task> getTaskHistory() {
        return taskHistory.getTasks();
    }
}