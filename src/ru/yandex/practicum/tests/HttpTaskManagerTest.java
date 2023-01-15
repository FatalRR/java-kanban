package ru.yandex.practicum.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.server.HttpTaskManager;
import ru.yandex.practicum.server.KVServer;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest<T extends TasksManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TasksManager tasksManager;

    @BeforeEach
    void createManager() {
        try {
            server = new KVServer();
            server.start();
            tasksManager = Managers.getDefault();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка создания менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("новая задача 1", "описание задачи 1", Status.NEW, LocalDateTime.now(), 0);
        Task task2 = new Task("новая задача 2", "описание задачи 2", Status.NEW, LocalDateTime.now(), 0);
        tasksManager.createTask(task1);
        tasksManager.createTask(task2);
        tasksManager.getTaskById(1);
        tasksManager.getTaskById(2);
        List<Task> history = tasksManager.getHistory();
        assertEquals(history, tasksManager.getAllTasks());
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("новый эпик 1", "описание эпика 1", Status.NEW, LocalDateTime.now(), 0);
        Epic epic2 = new Epic("новый эпик 2", "описание эпика 2", Status.NEW, LocalDateTime.now(), 0);
        tasksManager.createEpic(epic1);
        tasksManager.createEpic(epic2);
        tasksManager.getEpicById(1);
        tasksManager.getEpicById(2);
        List<Task> history = tasksManager.getHistory();
        assertEquals(history, tasksManager.getAllEpic());
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic("новый эпик 1", "описание эпика 1", Status.NEW, LocalDateTime.now(), 0);
        Subtask subtask1 = new Subtask("новая подзадача 1", "описание подзадачи 1", Status.NEW, 1, LocalDateTime.now(), 0);
        Subtask subtask2 = new Subtask("новая подзадача 2", "описание подзадачи 2", Status.NEW, 1, LocalDateTime.now(), 0);
        tasksManager.createEpic(epic1);
        tasksManager.createSubtask(subtask1);
        tasksManager.createSubtask(subtask2);
        tasksManager.getSubtaskById(2);
        tasksManager.getSubtaskById(3);
        List<Task> history = tasksManager.getHistory();
        assertEquals(history, tasksManager.getAllSubtasks());
    }
}