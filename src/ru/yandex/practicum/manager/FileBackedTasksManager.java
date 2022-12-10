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

public class FileBackedTasksManager extends InMemoryTaskManager implements TasksManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private String toString(Task task) {
        String[] line = {Integer.toString(task.getId()), getTaskType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), getEpic(task)};
        return String.join(",", line);
    }

    private Task fromString(String value) {
        String[] elem = value.split(",");

        if (elem[1].equals("EPIC")) {
            Epic epic = new Epic(elem[4], elem[2], Status.valueOf(elem[3].toUpperCase()));
            epic.setId(Integer.parseInt(elem[0]));
            epic.setStatus(Status.valueOf(elem[3].toUpperCase()));
            return epic;
        } else if (elem[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(elem[4], elem[2], Integer.parseInt(elem[5]),
                    Status.valueOf(elem[3].toUpperCase()));
            subtask.setId(Integer.parseInt(elem[0]));
            return subtask;
        } else {
            Task task = new Task(elem[4], elem[2], Status.valueOf(elem[3].toUpperCase()));
            task.setId(Integer.parseInt(elem[0]));
            return task;
        }
    }

    private String getEpic(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private TaskType getTaskType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Файл для записи отсутствует");
        }
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                fw.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpic()) {
                fw.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                fw.write(toString(subtask) + "\n");
            }

            fw.write("\n");
            fw.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные");
        }

    }

    public void loadFromFile() {
        try (BufferedReader bf = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bf.readLine();

            while (bf.ready()) {
                line = bf.readLine();

                if (line.equals("")) {
                    break;
                }

                Task task = fromString(line);

                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    addEpic(epic);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    addSubtask(subtask);
                } else {
                    addTask(task);
                }
            }

            line = bf.readLine();

            for (int id : historyFromString(line)) {
                addHistory(id);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные");
        }
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

    public void addTask(Task task) {
        super.createTask(task);
    }

    public void addEpic (Epic epic) {
        super.createEpic(epic);
    }

    public void addSubtask(Subtask subtask) {
        super.createSubtask(subtask);
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

        for (Task task : historyManager) {
            sb.append(task.getId());
            sb.append(",");
        }

        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
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