package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.Status;
import ru.yandex.practicum.model.tasks.Epic;
import ru.yandex.practicum.model.tasks.Subtask;
import ru.yandex.practicum.model.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private String toString(Task task) {
        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicId();
    }

    private static Task fromString(String value) {
        String[] elem = value.split(",");

        int id = Integer.parseInt(elem[0]);
        String name = elem[2];
        String description = elem[4];
        Status status = Status.valueOf(elem[3].toUpperCase());

        switch (TaskType.valueOf(elem[1])) {
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicNumber = 0;
                if (elem.length == 6) {
                    epicNumber = Integer.parseInt(elem[5]);
                }
                Subtask subtask = new Subtask(name, description, epicNumber, status);
                subtask.setId(id);
                return subtask;
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            default:
                return null;
        }
    }

    private void addTask(Task task) {
        super.createTask(task);
    }

    private void addEpic(Epic epic) {
        super.createEpic(epic);
    }

    private void addSubtask(Subtask subtask) {
        super.createSubtask(subtask);
    }

    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Файл для записи отсутствует");
        }
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write(HEADER);
            fw.write(System.lineSeparator());

            for (Task task : getAllTasks()) {
                fw.write(toString(task) + System.lineSeparator());
            }
            for (Epic epic : getAllEpic()) {
                fw.write(toString(epic) + System.lineSeparator());
            }
            for (Subtask subtask : getAllSubtasks()) {
                fw.write(toString(subtask) + System.lineSeparator());
            }

            fw.write(System.lineSeparator());
            fw.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные");
        }

    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try (BufferedReader bf = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bf.readLine();

            while (bf.ready()) {
                line = bf.readLine();

                if (line.isBlank()) {
                    break;
                }

                Task task = fromString(line);

                switch (task.getTaskType()) {
                    case EPIC:
                        Epic epic = (Epic) task;
                        fileBackedTasksManager.addEpic(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        fileBackedTasksManager.addSubtask(subtask);
                        break;
                    case TASK:
                        fileBackedTasksManager.addTask(task);
                        break;
                }
            }

            line = bf.readLine();

            for (int id : historyFromString(line)) {
                fileBackedTasksManager.addHistory(id);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные");
        }

        return fileBackedTasksManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    static String historyToString(List<Task> historyManager) {
        StringBuilder sb = new StringBuilder();

        if (historyManager.isEmpty()) {
            return "";
        }

        sb.append(historyManager.get(0).getId());

        for (int i = 1; i < historyManager.size(); i++) {
            sb.append(",");
            sb.append(historyManager.get(i).getId());
        }

        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        if (value != null) {
            String[] elem = value.split(",");

            for (String e : elem) {
                list.add(Integer.parseInt(e));
            }
        }
        return list;
    }
}