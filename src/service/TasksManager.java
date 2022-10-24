package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.*;

public class TasksManager {
    private enum printNot {
        NOT_TASK("Такой задачи нет"),
        NOT_EPIC("Такого эпика нет"),
        NOT_SUBTASK("Такой подзадачи нет");
        private final String notItem;
        printNot(String notItem) {
            this.notItem = notItem;
        }

        @Override
        public String toString() {
            return notItem;
        }
    }

    private int id = 1;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();

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
            System.out.println(printNot.NOT_SUBTASK);
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
        if (tasks.remove(id) == null) {
            System.out.println(printNot.NOT_TASK);
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
            System.out.println(printNot.NOT_EPIC);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic.getSubTaskList().contains(id)) {
                epic.removeSubTaskList(subtask.getId());
                updateStatus(epic);
                subtasks.remove(id);
            } else {
                System.out.println(printNot.NOT_SUBTASK);
            }
        } else {
            System.out.println(printNot.NOT_SUBTASK);
        }
    }

    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    public Map<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public List<Subtask> getAllSubtasksByEpic(int id) {
        if (tasks.get(id) != null) {
            List<Subtask> subtaskById = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubTaskList().size(); i++) {
                subtaskById.add(subtasks.get(epic.getSubTaskList().get(i)));
            }
            return subtaskById;
        } else {
            System.out.println(printNot.NOT_EPIC);
            return Collections.emptyList();
        }
    }

    public Map<Integer, Epic> getAllEpic() {
        return epics;
    }

    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println(printNot.NOT_TASK);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println(printNot.NOT_EPIC);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) != null) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatus(epic);
        } else {
            System.out.println(printNot.NOT_SUBTASK);
        }
    }

    private void updateStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            int countDone = 0;
            int countNew = 0;
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

                if (countDone == epic.getSubTaskList().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubTaskList().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println(printNot.NOT_EPIC);
        }
    }
}