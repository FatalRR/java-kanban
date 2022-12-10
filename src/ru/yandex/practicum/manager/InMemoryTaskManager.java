package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TasksManager {
    private int id = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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

    private int generateId() {
        return id++;
    }

    @Override
    public void createTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
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

    @Override
    public void deleteAllTasks() {
        clearHistoryTask();
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        clearHistoryEpic();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        clearHistorySubtask();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTaskList();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.remove(id) == null) {
            System.out.println(PrintNot.NOT_TASK);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskList()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println(PrintNot.NOT_EPIC);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epicsGet = epics.get(subtask.getEpicId());
            Epic epic = epicsGet;
            if (epicsGet != null) {
                epic.removeSubTaskList(subtask.getId());
                updateStatus(epic);
                subtasks.remove(id);
                historyManager.remove(id);
            } else {
                System.out.println(PrintNot.NOT_SUBTASK);
            }
        } else {
            System.out.println(PrintNot.NOT_SUBTASK);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        Collection<Task> values = tasks.values();
        return new ArrayList<>(values);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        Collection<Subtask> values = subtasks.values();
        return new ArrayList<>(values);
    }

    @Override
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

    @Override
    public List<Epic> getAllEpic() {
        Collection<Epic> values = epics.values();
        return new ArrayList<>(values);
    }

    @Override
    public void updateTask(Task task) {
        int idTask = task.getId();
        if (tasks.get(idTask) != null) {
            tasks.put(idTask, task);
        } else {
            System.out.println(PrintNot.NOT_TASK);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println(PrintNot.NOT_EPIC);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) != null) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatus(epic);
        } else {
            System.out.println(PrintNot.NOT_SUBTASK);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getTaskHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public void clearHistoryTask() {
        tasks.forEach((key, value) -> {
            historyManager.remove(key);
        });
    }

    public void clearHistoryEpic() {
        epics.forEach((key, value) -> {
            historyManager.remove(key);
        });
        clearHistorySubtask();
    }

    public void clearHistorySubtask() {
        subtasks.forEach((key, value) -> {
            historyManager.remove(key);
        });
    }

    public void addHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else {
            historyManager.add(tasks.get(id));
        }
    }
}