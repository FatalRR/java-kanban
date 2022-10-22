package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.*;

public class TasksManager {
    private static int id = 1;
    private static final String NOT_TASK = "Такой задачи нет";
    private static final String NOT_EPIC = "Такого эпика нет";
    private static final String NOT_SUBTASK = "Такой подзадачи нет";

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

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        int epicId = epic.getId();
        epics.put(epicId, epic);
    }

    public void createSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtaskId, subtask);
            epic.setSubTaskList(subtaskId);
            updateStatus(epic);
        } else {
            System.out.println(NOT_EPIC);
        }
    }


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
            updateStatus(epic);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }


    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println(NOT_TASK);
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskList()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println(NOT_EPIC);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubTaskList().remove((Integer) subtask.getId());
            updateStatus(epic);
            subtasks.remove(id);
        } else {
            System.out.println(NOT_SUBTASK);
        }
    }

    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    public Map<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public Map<Integer, Subtask> getAllSubtasksByEpic(int id) {
        if (epics.containsKey(id)) {
            Map<Integer, Subtask> subtaskById = new HashMap<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubTaskList().size(); i++) {
                subtaskById.put(i, subtasks.get(epic.getSubTaskList().get(i)));
            }
            return subtaskById;
        } else {
            System.out.println(NOT_EPIC);
            return Collections.emptyMap();
        }
    }

    public Map<Integer, Epic> getAllEpic() {
        return epics;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println(NOT_TASK);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println(NOT_EPIC);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatus(epic);
        } else {
            System.out.println(NOT_SUBTASK);
        }
    }

    public void updateStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            List<Subtask> test = new ArrayList<>();
            int countDone = 0;
            int countNew = 0;
            for (int i = 0; i < epic.getSubTaskList().size(); i++) {
                test.add(subtasks.get(epic.getSubTaskList().get(i)));
            }
            for (Subtask subtask : test) {
                switch (subtask.getStatus()) {
                    case DONE:
                        countDone++;
                        break;
                    case NEW:
                        countNew++;
                        break;
                    case IN_PROGRESS:
                        return;
                }

                if (countDone == epic.getSubTaskList().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubTaskList().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println(NOT_EPIC);
        }
    }
}