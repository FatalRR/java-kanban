package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int TASK_LIMIT = 10;
    private final List<Task> taskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.size() == TASK_LIMIT) {
                taskHistory.remove(0);
            }
            taskHistory.add(task);
        } else {
            System.out.println(PrintNot.NOT_TASK);
        }
    }

    @Override
    public List<Task> getTaskHistory() {
        return taskHistory;
    }
}