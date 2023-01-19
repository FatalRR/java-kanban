package ru.yandex.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class SubtaskHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private final TasksManager tasksManager;

    public SubtaskHandler(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;

        QueriesType method = QueriesType.fromValue(exchange.getRequestMethod());

        switch (method) {
            case GET:
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = HttpURLConnection.HTTP_OK;
                    response = gson.toJson(tasksManager.getAllSubtasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Subtask subtask = tasksManager.getSubtaskById(id);
                        if (subtask != null) {
                            response = gson.toJson(subtask);
                        } else {
                            response = "Подзадача с данным id не найдена";
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
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                    int id = subtask.getId();
                    if (tasksManager.getSubtaskById(id) != null) {
                        tasksManager.updateTask(subtask);
                        statusCode = HttpURLConnection.HTTP_OK;
                        response = "Подзадача с id=" + id + " обновлена";
                    } else {
                        System.out.println("CREATED");
                        tasksManager.createSubtask(subtask);
                        System.out.println("CREATED SUBTASK: " + subtask);
                        int idCreated = subtask.getId();
                        statusCode = HttpURLConnection.HTTP_CREATED;
                        response = "Создана подзадача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                    statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                }
                break;
            case DELETE:
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    tasksManager.deleteAllSubtasks();
                    statusCode = HttpURLConnection.HTTP_OK;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=")));
                        tasksManager.deleteSubtaskById(id);
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

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}