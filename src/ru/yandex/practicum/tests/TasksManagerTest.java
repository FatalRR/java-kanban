package ru.yandex.practicum.tests;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TasksManagerTest<T extends TasksManager> {
    protected T taskManager;

    protected Task createTask() {
        return new Task("новая задача 1", "описание задачи 1", Status.NEW, Instant.now(), 0);
    }

    protected Epic createEpic() {
        return new Epic("новый эпик 1", "описание эпика 1", Status.NEW,
                Instant.now(), 0);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("новая подзадача 1", "описание подзадачи 1", Status.NEW, epic.getId(),
                Instant.now(), 0);
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        taskManager.createTask(task);
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        List<Epic> epics = taskManager.getAllEpic();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubTaskList());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubTaskList());
    }

    @Test
    void shouldCreateNullTask() {
        Task task = null;
        taskManager.createTask(task);
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void shouldCreateNullEpic() {
        Epic epic = null;
        taskManager.createEpic(epic);
        assertEquals(0, taskManager.getAllEpic().size());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.updateTask(null);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.updateEpic(null);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.updateSubtask(null);
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldCreateNullSubtask() {
        Subtask subtask = null;
        taskManager.createSubtask(subtask);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void shouldUpdateEpicStatusByUpdateSubtaskStatus() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        taskManager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = createTask();
        taskManager.createTask(task);
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(Status.DONE, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInDone() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.deleteAllTasks();
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpic());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteAllSubtasks();
        assertTrue(epic.getSubTaskList().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpic());
    }

    @Test
    public void shouldNotDeleteTaskIfBadId() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.deleteTaskById(2);
        assertEquals(List.of(task), taskManager.getAllTasks());
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(2);
        assertEquals(List.of(epic), taskManager.getAllEpic());
    }

    @Test
    public void shouldNotDeleteSubtaskIfBadId() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(3);
        assertEquals(List.of(subtask), taskManager.getAllSubtasks());
        assertEquals(List.of(subtask.getId()), taskManager.getEpicById(epic.getId()).getSubTaskList());
    }

    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty() {
        taskManager.deleteAllTasks();
        taskManager.deleteTaskById(1);
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty() {
        taskManager.deleteAllEpics();
        taskManager.deleteEpicById(1);
        assertTrue(taskManager.getAllEpic().isEmpty());
    }

    @Test
    public void shouldDoNothingIfSubtaskHashMapIsEmpty() {
        taskManager.deleteAllEpics();
        taskManager.deleteSubtaskById(1);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void shouldReturnEmptyListWhenGetSubtaskByEpicIdIsEmpty() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpic(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(taskManager.getAllEpic().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListSubtasksIfNoSubtasks() {
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        List<Task> list = taskManager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }
}