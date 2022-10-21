package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public class TasksManager {
    private static int id = 1;
    private static int epicId=0;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;

    public TasksManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public static int generateId() {
        return id++;
    }

    public void createTask(String name, String description) {
        Task task = new Task(name, description);
        tasks.put(task.getId(), task);
    }

    public void createEpic(String name, String description) {
        Epic task = new Epic(name, description);
        epics.put(task.getId(), task);
        epicId=task.getId();
    }

    public void createSubtask(String name, String description) {
        Subtask task = new Subtask(name, description);
        subtasks.put(task.getId(), task);

    }



    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void getTaskById() {
    }


    public void deleteTaskById() {
    }

    public HashMap<Integer, Subtask> getAllSubtasksByEpic() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }


}