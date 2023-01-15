package ru.yandex.practicum.manager;

import ru.yandex.practicum.server.HttpTaskManager;
import ru.yandex.practicum.server.KVServer;

import java.io.IOException;

public class Managers {

    public static TasksManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}