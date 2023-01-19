package ru.yandex.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class TaskHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private final TasksManager tasksManager;

    public TaskHandler(TasksManager taskManager) {
        this.tasksManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode;
        String response;
        QueriesType method = QueriesType.fromValue(httpExchange.getRequestMethod());

        switch (method) {
            case GET:
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = HttpURLConnection.HTTP_OK;
                    response = gson.toJson(tasksManager.getAllTasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = tasksManager.getTaskById(id);
                        if (task != null) {
                            response = gson.toJson(task);
                        } else {
                            response = "Задача с данным id не найдена";
                        }
                        statusCode = HttpURLConnection.HTTP_OK;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                        response = "Неверный формат id";
                    }
                }
                break;
            case POST:
                String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    int id = task.getId();

                    if (tasksManager.getTaskById(id) != null) {
                        tasksManager.updateTask(task);
                        statusCode = HttpURLConnection.HTTP_CREATED;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        tasksManager.createTask(task);
                        System.out.println("CREATED TASK: " + task);
                        int idCreated = task.getId();
                        statusCode = HttpURLConnection.HTTP_CREATED;
                        response = "Создана задача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                    response = "Неверный формат запроса";
                }
                break;
            case DELETE:
                response = "";
                query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    tasksManager.deleteAllTasks();
                    statusCode = HttpURLConnection.HTTP_OK;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        tasksManager.deleteTaskById(id);
                        statusCode = HttpURLConnection.HTTP_OK;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                response = "Некорректный запрос";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}