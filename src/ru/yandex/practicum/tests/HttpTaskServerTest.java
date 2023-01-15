package ru.yandex.practicum.tests;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;
import ru.yandex.practicum.server.HttpTaskServer;
import ru.yandex.practicum.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private static final String TASK_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_URL = "http://localhost:8080/tasks/subtask/";

    protected Task createTask() {
        return new Task("новая задача 1", "описание задачи 1", Status.NEW, LocalDateTime.of(2023,1,1,12,0), 0);
    }

    protected Epic createEpic() {
        return new Epic("новый эпик 1", "описание эпика 1", Status.NEW,
                LocalDateTime.of(2023,1,1,12,1), 0);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("новая подзадача 1", "описание подзадачи 1", Status.NEW, epic.getId(),
                LocalDateTime.of(2023,1,1,12,2), 0);
    }

    @BeforeAll
    static void start() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stop() {
        kvServer.stop();
        taskServer.stop();
    }

    @BeforeEach
    public void reset() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create(EPIC_URL);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create(SUBTASK_URL);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();


        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonElement arrayTasks = JsonParser.parseString(response.body());

            if (arrayTasks.isJsonArray()) {
                assertEquals(1, arrayTasks.getAsJsonArray().size());
            } else if (arrayTasks.isJsonObject()) {
                assertEquals(1, arrayTasks.getAsJsonObject().size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().
                        uri(uri).
                        GET().
                        build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                uri = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setId(id);
                uri = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    uri = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateTask() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                uri = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateEpic() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("новая задача 1", "описание задачи 1", Status.NEW, LocalDateTime.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .header("Content-Type", "application/json")
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSubtask() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setStatus(Status.IN_PROGRESS);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                    uri = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .DELETE()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body());
            uri = URI.create(TASK_URL + "?id=" + id);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с данным id не найдена", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                uri = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .DELETE()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Эпик с данным id не найден", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    uri = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .DELETE()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(204, response.statusCode());

                    request = HttpRequest.newBuilder().uri(uri).GET().build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Подзадача с данным id не найдена", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}