package ru.yandex.practicum.manager;

import java.io.File;

public class Managers {

    public static TasksManager getDefault() {
        return new FileBackedTasksManager(new File("save.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}