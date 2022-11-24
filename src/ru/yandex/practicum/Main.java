package ru.yandex.practicum;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.manager.TasksManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TasksManager tasksManager = Managers.getDefault();

//---------------------------------------------
        System.out.println("Тесты создание");
        tasksManager.createEpic(new Epic("новый эпик 1", "описания эпик 1", Status.NEW));
        tasksManager.createEpic(new Epic("новый эпик 2", "описания эпик 2", Status.NEW));
        tasksManager.createEpic(new Epic("новый эпик 3", "описания эпик 3", Status.NEW));
        tasksManager.createSubtask(new Subtask("подзадача 1-1", "описание подзадачи 1-1", 1, Status.IN_PROGRESS));
        tasksManager.createSubtask(new Subtask("подзадача 1-2", "описание подзадачи 1-2", 1, Status.NEW));
        tasksManager.createSubtask(new Subtask("подзадача 1-3", "описание подзадачи 1-3", 1, Status.IN_PROGRESS));
        tasksManager.createSubtask(new Subtask("подзадача 2-1", "описание подзадачи 1-1", 2, Status.IN_PROGRESS));
        tasksManager.createSubtask(new Subtask("подзадача 2-2", "описание подзадачи 1-2", 2, Status.NEW));
        tasksManager.createSubtask(new Subtask("подзадача 2-3", "описание подзадачи 1-2", 2, Status.NEW));

        System.out.println("Все задачи: " + tasksManager.getAllTasks() + "\n" +
                "Все эпик задачи: " + tasksManager.getAllEpic() + "\n" +
                "Все подзадачи эпиков: " + tasksManager.getAllSubtasks() + "\n");
//---------------------------------------------
        System.out.println("Получение подзадач по эпику");
        System.out.println("Подзадачи по эпику " + tasksManager.getAllSubtasksByEpic(1) + "\n");
        System.out.println("Подзадачи по эпику " + tasksManager.getAllSubtasksByEpic(1) + "\n");
        System.out.println("Подзадачи по эпику " + tasksManager.getAllSubtasksByEpic(2) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(1) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(2) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(1) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(3) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(1) + "\n");
        System.out.println("Эпик по id " + tasksManager.getEpicById(2) + "\n");
        System.out.println("Эпик по id " + tasksManager.getSubtaskById(5) + "\n");
        System.out.println("Эпик по id " + tasksManager.getSubtaskById(5) + "\n");
        List<Task> history = tasksManager.getHistory();
//---------------------------------------------
        System.out.println("История");
        System.out.println(history);
//---------------------------------------------
        System.out.println("Удаление из истории просмотров");
        tasksManager.remove(3);
        tasksManager.remove(2);
        List<Task> history2 = tasksManager.getHistory();
        System.out.println(history2);
    }
}