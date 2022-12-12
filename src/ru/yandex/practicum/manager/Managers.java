package ru.yandex.practicum.manager;

import java.io.File;

public class Managers {

    public static TasksManager getDefault(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}