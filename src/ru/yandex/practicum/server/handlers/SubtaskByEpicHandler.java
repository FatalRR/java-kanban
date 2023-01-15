package ru.yandex.practicum.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.manager.TasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class SubtaskByEpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TasksManager tasksManager;

    public SubtaskByEpicHandler(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    statusCode = 200;
                    response = gson.toJson(tasksManager.getSubtaskById(id));
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    response = "В запросе отсутствует необходимый параметр - id";
                } catch (NumberFormatException e) {
                    response = "Неверный формат id";
                }
                break;
            default:
                response = "Некорректный запрос";
        }

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        httpExchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}