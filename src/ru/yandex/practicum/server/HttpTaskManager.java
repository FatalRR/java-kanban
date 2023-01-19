package ru.yandex.practicum.server;

import com.google.gson.*;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    public HttpTaskManager(String path) throws IOException, InterruptedException {
        super(new File("save.csv"));
        client = new KVTaskClient(path);

        JsonElement jsonTasks = JsonParser.parseString(client.load(String.valueOf(Keys.KEY_TASKS)));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.addTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(String.valueOf(Keys.KEY_EPICS)));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.addEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(String.valueOf(Keys.KEY_SUBTASK)));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                this.addSubtask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(String.valueOf(Keys.KEY_HISTORY)));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subtasks.get(taskId) != null) {
                    this.getSubtaskById(taskId);
                } else if (this.epics.get(taskId) != null) {
                    this.getEpicById(taskId);
                } else if (this.tasks.get(taskId) != null) {
                    this.getTaskById(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        client.register();
        client.put(String.valueOf(Keys.KEY_TASKS), gson.toJson(tasks.values()));
        client.put(String.valueOf(Keys.KEY_EPICS), gson.toJson(epics.values()));
        client.put(String.valueOf(Keys.KEY_SUBTASK), gson.toJson(subtasks.values()));
        client.put(String.valueOf(Keys.KEY_HISTORY), gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

    enum Keys {
        KEY_TASKS("tasks"),
        KEY_EPICS("epics"),
        KEY_SUBTASK("subtasks"),
        KEY_HISTORY("history");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }
}