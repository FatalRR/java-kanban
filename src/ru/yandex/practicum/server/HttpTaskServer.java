package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        TasksManager tasksManager = Managers.getDefault();
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler(tasksManager));
        httpServer.createContext("/tasks/epic", new EpicHandler(tasksManager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(tasksManager));
        httpServer.createContext("/tasks/subtask/epic", new SubtaskByEpicHandler(tasksManager));
        httpServer.createContext("/tasks/history", new HistoryHandler(tasksManager));
        httpServer.createContext("/tasks/", new TasksHandler(tasksManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}