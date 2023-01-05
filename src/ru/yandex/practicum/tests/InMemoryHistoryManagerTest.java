package ru.yandex.practicum.tests;

import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.InMemoryHistoryManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Task;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    private int id = 1;

    private static final String NOT_NULL = "История не пустая.";
    public int generateId() {
        return id++;
    }

    private Task createTask() {
        return new Task("новая задача 1", "описание задачи 1", Status.NEW, Instant.now(), 0);
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }
    @Test
    public void shouldAddTaskToHistory() {
        Task task = createTask();
        task.setId(generateId());
        historyManager.add(task);
        final List<Task> history = historyManager.getTaskHistory();
        assertNotNull(history, NOT_NULL);
        assertEquals(1, history.size(), NOT_NULL);
    }

    @Test
    public void shouldRemoveTask() {
        Task task = createTask();
        task.setId(generateId());
        Task task1 = createTask();
        task1.setId(generateId());
        Task task2 = createTask();
        task2.setId(generateId());
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getTaskHistory();
        assertNotNull(history, NOT_NULL);
        assertEquals(2, history.size(), NOT_NULL);
    }

    @Test
    public void shouldRemoveTasks() {
        Task task = createTask();
        task.setId(generateId());
        Task task1 = createTask();
        task1.setId(generateId());
        Task task2 = createTask();
        task2.setId(generateId());
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task.getId());
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        final List<Task> history = historyManager.getTaskHistory();
        assertNotNull(history, NOT_NULL);
        assertEquals(0, history.size(), NOT_NULL);
    }

    @Test
    public void shouldHistoryIsEmpty() {
        Task task = createTask();
        task.setId(generateId());
        Task task1 = createTask();
        task1.setId(generateId());
        Task task2 = createTask();
        task2.setId(generateId());
        historyManager.remove(task.getId());
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        final List<Task> history = historyManager.getTaskHistory();
        assertNotNull(history, NOT_NULL);
        assertEquals(0, history.size(), NOT_NULL);
    }

    @Test
    public void shouldRemoveTaskBadId() {
        Task task = createTask();
        task.setId(generateId());
        historyManager.add(task);
        historyManager.remove(5);
        final List<Task> history = historyManager.getTaskHistory();
        assertNotNull(history, NOT_NULL);
        assertEquals(1, history.size(), NOT_NULL);
    }
}