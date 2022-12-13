package ru.yandex.practicum;

import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;


import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Запись в файл");

        TasksManager fileBackedTasksManager = Managers.getDefault();

        fileBackedTasksManager.createTask(new Task("новая задача 1", "описание задачи 1", Status.NEW));
        fileBackedTasksManager.createTask(new Task("новая задача 2", "описание задачи 2", Status.NEW));

        fileBackedTasksManager.createEpic(new Epic("новый эпик 1", "описания эпик 1", Status.NEW));
        fileBackedTasksManager.createEpic(new Epic("новый эпик 2", "описания эпик 2", Status.NEW));

        fileBackedTasksManager.createSubtask(new Subtask("подзадача 1-1", "описание подзадачи 1-1", 3, Status.IN_PROGRESS));
        fileBackedTasksManager.createSubtask(new Subtask("подзадача 1-2", "описание подзадачи 1-2", 3, Status.IN_PROGRESS));
        fileBackedTasksManager.createSubtask(new Subtask("подзадача 1-3", "описание подзадачи 1-3", 3, Status.IN_PROGRESS));
        //---------------------------------------------
        System.out.println("Получение подзадач по эпику");
        System.out.println("Эпик по id " + fileBackedTasksManager.getEpicById(3) + "\n");
        System.out.println("Эпик по id " + fileBackedTasksManager.getEpicById(4) + "\n");
        System.out.println("Эпик по id " + fileBackedTasksManager.getSubtaskById(5) + "\n");
        List<Task> history = fileBackedTasksManager.getHistory();
        System.out.println("История");
        System.out.println(history);

        System.out.println("Все задачи: " + fileBackedTasksManager.getAllTasks() + "\n" +
                "Все эпик задачи: " + fileBackedTasksManager.getAllEpic() + "\n" +
                "Все подзадачи эпиков: " + fileBackedTasksManager.getAllSubtasks() + "\n");
        //---------------------------------------------
        System.out.println("Чтение из файла");

        Path path2 = Path.of("save.csv");
        File file2 = new File(String.valueOf(path2));

        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file2);
        List<Task> history2 = fileBackedTasksManager2.getHistory();

        System.out.println("История");
        System.out.println(history2);
        System.out.println("Все задачи: " + fileBackedTasksManager2.getAllTasks() + "\n" +
                "Все эпик задачи: " + fileBackedTasksManager2.getAllEpic() + "\n" +
                "Все подзадачи эпиков: " + fileBackedTasksManager2.getAllSubtasks() + "\n");
    }
}