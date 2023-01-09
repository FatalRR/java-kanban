package ru.yandex.practicum.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TasksManagerTest<InMemoryTaskManager> {
    private static final Path path = Path.of("save_test.csv");
    private static final File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Description", "Title", Status.NEW, Instant.now(), 0);
        taskManager.createTask(task);
        Epic epic = new Epic("Description", "Title", Status.NEW, Instant.now(), 0);
        taskManager.createEpic(epic);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(List.of(task), fileManager.getAllTasks());
        assertEquals(List.of(epic), fileManager.getAllEpic());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpic());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }
}