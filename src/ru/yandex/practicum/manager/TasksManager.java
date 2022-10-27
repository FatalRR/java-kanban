package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.model.Status;

import java.util.*;

public class TasksManager {
    private int id = 1;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();

    private void updateStatus(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            int countDone = 0;
            int countNew = 0;
            int listSize = epic.getSubTaskList().size();
            for (Subtask subtask : subtasks.values()) {
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

                if (countDone == listSize) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == listSize) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println(PrintNot.NOT_EPIC);
        }
    }

    public int generateId() {
        return id++;
    }

    public void createTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    public void createEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    public void createSubtask(Subtask subtask) {
        int subtaskId = generateId();
        subtask.setId(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtaskId, subtask);
            epic.addSubTaskList(subtaskId);
            updateStatus(epic);
        } else {
            System.out.println(PrintNot.NOT_SUBTASK);
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
            epic.clearSubTaskList();
            epic.setStatus(Status.NEW);
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
        if (tasks.remove(id) == null) {
            System.out.println(PrintNot.NOT_TASK);
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
            System.out.println(PrintNot.NOT_EPIC);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epicsGet = epics.get(subtask.getEpicId());
            Epic epic = epicsGet;
            if (epicsGet != null) {
                epic.removeSubTaskList(subtask.getId());
                updateStatus(epic);
                subtasks.remove(id);
            } else {
                System.out.println(PrintNot.NOT_SUBTASK);
            }
        } else {
            System.out.println(PrintNot.NOT_SUBTASK);
        }
    }

    public List<Task> getAllTasks() {
        Collection<Task> values = tasks.values();
        return new ArrayList<>(values);
    }

    public List<Subtask> getAllSubtasks() {
        Collection<Subtask> values = subtasks.values();
        return new ArrayList<>(values);
    }

    public List<Subtask> getAllSubtasksByEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            List<Subtask> subtaskById = new ArrayList<>();
            for (int i = 0; i < epic.getSubTaskList().size(); i++) {
                Subtask subList = subtasks.get(epic.getSubTaskList().get(i));
                if (subList != null) {
                    subtaskById.add(subList);
                } else {
                    System.out.println(PrintNot.NOT_SUBTASK);
                }
            }
            return subtaskById;
        } else {
            System.out.println(PrintNot.NOT_EPIC);
            return Collections.emptyList();
        }
    }

    public List<Epic> getAllEpic() {
        Collection<Epic> values = epics.values();
        return new ArrayList<>(values);
    }

    public void updateTask(Task task) {
        int idTask = task.getId();
        if (tasks.get(idTask) != null) {
            tasks.put(idTask, task);
        } else {
            System.out.println(PrintNot.NOT_TASK);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println(PrintNot.NOT_EPIC);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) != null) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatus(epic);
        } else {
            System.out.println(PrintNot.NOT_SUBTASK);
        }
    }
}