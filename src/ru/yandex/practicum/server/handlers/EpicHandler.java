package ru.yandex.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.model.tasks.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class EpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private final TasksManager tasksManager;

    public EpicHandler(TasksManager tasksManager) {
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
                    response = gson.toJson(tasksManager.getAllEpic());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = tasksManager.getEpicById(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                        } else {
                            response = "Эпик с данным id не найден";
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
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    int id = epic.getId();
                    if (tasksManager.getEpicById(id) != null) {
                        tasksManager.updateTask(epic);
                        statusCode = HttpURLConnection.HTTP_OK;
                        response = "Эпик с id=" + id + " обновлен";
                    } else {
                        System.out.println("CREATED");
                        tasksManager.createEpic(epic);
                        System.out.println("CREATED EPIC: " + epic);
                        int idCreated = epic.getId();
                        statusCode = HttpURLConnection.HTTP_CREATED;
                        response = "Создан эпик с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                    response = "Неверный формат запроса";
                }
                break;
            case DELETE:
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    tasksManager.deleteAllEpics();
                    statusCode = HttpURLConnection.HTTP_OK;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        tasksManager.deleteEpicById(id);
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