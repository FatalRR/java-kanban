package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TasksManager {
    private int id = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);

    protected final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

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
                        break;
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

    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validateTaskPriority();
    }

    private void validateTaskPriority() {
        List<Task> tasks = getPrioritizedTasks();

        for (int taskNum = 1; taskNum < tasks.size(); taskNum++) {
            Task task = tasks.get(taskNum);
            boolean taskHasIntersections = task.getStartTime().isBefore(tasks.get(taskNum - 1).getEndTime());

            if (taskHasIntersections) {
                throw new ManagerValidateException("Задачи " + task.getId() + " и " + tasks.get(taskNum - 1) + "пересекаются");
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            int taskId = generateId();
            task.setId(taskId);
            addNewPrioritizedTask(task);
            tasks.put(taskId, task);
        } else {
            System.out.println(PrintNot.NOT_NULL);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            int epicId = generateId();
            epic.setId(epicId);
            epics.put(epicId, epic);
        } else {
            System.out.println(PrintNot.NOT_NULL);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = generateId();
            subtask.setId(subtaskId);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                addNewPrioritizedTask(subtask);
                subtasks.put(subtaskId, subtask);
                epic.addSubTaskList(subtaskId);
                updateStatus(epic);
                updateTimeEpic(epic);
            } else {
                System.out.println(PrintNot.NOT_SUBTASK);
            }
        } else {
            System.out.println(PrintNot.NOT_NULL);
        }
    }

    @Override
    public void deleteAllTasks() {
        clearHistoryTask();
        tasks.clear();
        prioritizedTasks.clear();
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
            prioritizedTasks.remove(epic);
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
            prioritizedTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);
        } else {
            System.out.println(PrintNot.NOT_TASK);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskList()) {
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
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
                updateTimeEpic(epic);
                prioritizedTasks.remove(subtask);
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
        if (task != null) {
            int idTask = task.getId();
            if (tasks.get(idTask) != null) {
                prioritizedTasks.remove(task);
                addNewPrioritizedTask(task);
                tasks.put(idTask, task);
            } else {
                System.out.println(PrintNot.NOT_TASK);
            }
        } else {
            System.out.println(PrintNot.NOT_NULL);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            if (epics.get(epic.getId()) != null) {
                epics.put(epic.getId(), epic);
                updateStatus(epic);
                updateTimeEpic(epic);
            } else {
                System.out.println(PrintNot.NOT_EPIC);
            }
        } else {
            System.out.println(PrintNot.NOT_NULL);
        }
    }

    public void updateTimeEpic(Epic epic) {
        List<Subtask> subtasks = getAllSubtasksByEpic(epic.getId());
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();
        long durationCount = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
                durationCount += subtask.getDuration();
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(durationCount);
    }

    @Override
    public void updateSubtask(Subtask subtask)
    {
        if (subtask != null) {
            if (subtasks.get(subtask.getId()) != null) {
                prioritizedTasks.remove(subtask);
                addNewPrioritizedTask(subtask);
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                updateStatus(epic);
                updateTimeEpic(epic);
            } else {
                System.out.println(PrintNot.NOT_SUBTASK);
            }
        } else {
            System.out.println(PrintNot.NOT_NULL);
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
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        } else if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
        } else {
            historyManager.add(tasks.get(id));
        }
    }
}