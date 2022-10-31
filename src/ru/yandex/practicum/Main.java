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
        TasksManager tasksManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
//---------------------------------------------
        System.out.println("Создание");
        tasksManager.createTask(new Task("новая задача 1", "описание задачи 1", Status.NEW));
        tasksManager.createTask(new Task("новая задача 2", "описание задачи 2", Status.NEW));
        tasksManager.createEpic(new Epic("новый эпик 1", "описания эпик 1", Status.DONE));
        tasksManager.createSubtask(new Subtask("подзадача 1-1", "описание подзадачи 1-1", 3, Status.IN_PROGRESS));
        tasksManager.createSubtask(new Subtask("подзадача 1-2", "описание подзадачи 1-2", 3, Status.NEW));
        tasksManager.createEpic(new Epic("новый эпик 2", "описания эпик 2", Status.NEW));
        tasksManager.createSubtask(new Subtask("подзадача 2-1", "описание подзадачи 2-1", 6, Status.IN_PROGRESS));

        System.out.println("Все задачи: " + tasksManager.getAllTasks() + "\n" +
                "Все эпик задачи: " + tasksManager.getAllEpic() + "\n" +
                "Все подзадачи эпиков: " + tasksManager.getAllSubtasks() + "\n");
//---------------------------------------------
        System.out.println("Получение подзадач по эпику");
        System.out.println("Подзадачи по эпику " + tasksManager.getAllSubtasksByEpic(3) + "\n");
//---------------------------------------------
        System.out.println("Изменение статусов задач");
        Task task = tasksManager.getTaskById(1);
        task.setStatus(Status.DONE);
        tasksManager.updateTask(task);
        Epic epic = tasksManager.getEpicById(3);
        epic.setStatus(Status.IN_PROGRESS);
        tasksManager.updateEpic(epic);
//---------------------------------------------
        System.out.println("История");
        List<Task> history = tasksManager.getHistory();
        System.out.println(history);
        //---------------------------------------------
        Subtask subtask = tasksManager.getSubtaskById(7);
        subtask.setStatus(Status.IN_PROGRESS);
        tasksManager.updateSubtask(subtask);
        System.out.println("Задача по id" + tasksManager.getTaskById(1));
        System.out.println("Подзадача по id" + tasksManager.getSubtaskById(7));
        System.out.println("Эпик по id" + tasksManager.getEpicById(3));
        System.out.println("Эпик по id" + tasksManager.getEpicById(6));

//---------------------------------------------
        System.out.println("Удаление по id");
        tasksManager.deleteTaskById(1);
        System.out.println("Все задачи: " + tasksManager.getAllTasks());
        tasksManager.deleteSubtaskById(7);
        System.out.println("Все подзадачи эпиков: " + tasksManager.getAllSubtasks());
        tasksManager.deleteEpicById(6);
        System.out.println("Все эпик задачи: " + tasksManager.getAllEpic());
//---------------------------------------------
        System.out.println("Удаление всех задач");
        tasksManager.deleteAllTasks();
        tasksManager.deleteAllSubtasks();
        tasksManager.deleteAllEpics();
        System.out.println("Все задачи: " + tasksManager.getAllTasks() + "\n" +
                "Все эпик задачи: " + tasksManager.getAllEpic() + "\n" +
                "Все подзадачи эпиков: " + tasksManager.getAllSubtasks());
//---------------------------------------------
        System.out.println("История");
        System.out.println(history);
    }
}