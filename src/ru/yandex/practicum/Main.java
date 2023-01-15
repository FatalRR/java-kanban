package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.server.KVServer;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        KVServer server;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

            server = new KVServer();
            server.start();
            TasksManager httpTaskManager = Managers.getDefault();

            Task task1 = new Task("новая задача 1", "описание задачи 1", Status.NEW, LocalDateTime.now(), 0);
            httpTaskManager.createTask(task1);

            Epic epic1 = new Epic("новый эпик 1", "описание эпика 1", Status.NEW, LocalDateTime.now(), 0);
            httpTaskManager.createEpic(epic1);

            Subtask subtask1 = new Subtask("новая подзадача 1", "описание подзадачи 1", Status.NEW, epic1.getId(), LocalDateTime.now(), 0);
            httpTaskManager.createSubtask(subtask1);


            httpTaskManager.getTaskById(task1.getId());
            httpTaskManager.getEpicById(epic1.getId());
            httpTaskManager.getSubtaskById(subtask1.getId());

            System.out.println("Печать всех задач");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Печать всех эпиков");
            System.out.println(gson.toJson(httpTaskManager.getAllEpic()));
            System.out.println("Печать всех подзадач");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}