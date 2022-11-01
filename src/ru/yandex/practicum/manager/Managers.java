package ru.yandex.practicum.manager;

public class Managers {

    public static TasksManager getDefault() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}