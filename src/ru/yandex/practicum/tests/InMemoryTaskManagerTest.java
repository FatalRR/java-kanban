package ru.yandex.practicum.tests;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.tests.TasksManagerTest;

class InMemoryTaskManagerTest  extends TasksManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}