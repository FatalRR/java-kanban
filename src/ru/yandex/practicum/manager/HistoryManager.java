package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getTaskHistory();
}