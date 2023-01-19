package ru.yandex.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.TasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class SubtaskByEpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private final TasksManager tasksManager;

    public SubtaskByEpicHandler(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
        String response;
        QueriesType method = QueriesType.fromValue(httpExchange.getRequestMethod());
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        if (QueriesType.GET.equals(method)) {
            String query = httpExchange.getRequestURI().getQuery();
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                statusCode = HttpURLConnection.HTTP_OK;
                response = gson.toJson(tasksManager.getSubtaskById(id));
            } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                response = "В запросе отсутствует необходимый параметр - id";
            } catch (NumberFormatException e) {
                response = "Неверный формат id";
            }
        } else {
            response = "Некорректный запрос";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}