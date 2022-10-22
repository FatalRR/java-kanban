package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public class TasksManager {
    private static int id = 1;
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
        int epicId=task.getId();
        epics.put(epicId, task);
    }

    public void createSubtask(String name, String description, ) {
        Subtask task = new Subtask(name, description, epicId);
        int subtaskId = task.getId();
        subtasks.put(subtaskId, task);


    }
//    public void updateTask(Task task) {
//        tasks.put(task.getId(), task);
//    }



    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskList().clear();
            //изменить статус эпика, дописать
        }
    }

    public Task getTaskById(int id) {
        return  tasks.get(id);
    }
    public Epic getEpicById(int id) {
        return  epics.get(id);
    }
    public Subtask getSubtaskById(int id) {
        return  subtasks.get(id);
    }


    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача не найдена");
        }
    }
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }
    public HashMap<Integer, Subtask> getAllSubtasksByEpic() { // изменить метод
        return subtasks;
    }
    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }


}