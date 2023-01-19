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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static HttpClient client;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private static final String TASK_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_URL = "http://localhost:8080/tasks/subtask/";

    protected Task createTask() {
        return new Task("новая задача 1", "описание задачи 1", Status.NEW, LocalDateTime.of(2023, 1, 1, 12, 0), 0);
    }

    protected Epic createEpic() {
        return new Epic("новый эпик 1", "описание эпика 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 12, 1), 0);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("новая подзадача 1", "описание подзадачи 1", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 1, 1, 12, 2), 0);
    }

    @BeforeAll
    static void start() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
            client = HttpClient.newHttpClient();
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
        URI uri = URI.create(TASK_URL);
        Task task = createTask();
        task.setId(1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();


        try {
            this.client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
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
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasks() {
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                System.out.println(postResponse.body());
                int epicId = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
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
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTaskById() {
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                task.setId(id);
                uri = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpicById() {
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                epic.setId(id);
                uri = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtaskById() {
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int epicId = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                    subtask.setId(id);
                    uri = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
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
        URI url = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                task.setId(id);
                task.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSubtask() {
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_OK) {
                int epicId = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                epic.setId(epicId);
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                    int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                    subtask.setId(id);
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
                    assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
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
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
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
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtasks() {
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int epicId = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
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
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTaskById() {
        URI uri = URI.create(TASK_URL);
        Task task = createTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
            uri = URI.create(TASK_URL + "?id=" + id);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

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
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                uri = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .DELETE()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

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
        URI uri = URI.create(EPIC_URL);
        Epic epic = createEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == HttpURLConnection.HTTP_OK) {
                Subtask subtask = createSubtask(epic);
                uri = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(HttpURLConnection.HTTP_CREATED, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == HttpURLConnection.HTTP_CREATED) {
                    int id = Integer.parseInt(postResponse.body().replaceAll("\\D+", ""));
                    subtask.setId(id);
                    uri = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(uri)
                            .DELETE()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

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