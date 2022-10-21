import service.TasksManager;

public class Main {

    public static void main(String[] args) {
        TasksManager tasksManager = new TasksManager();

        tasksManager.createTask("новая задача 1", "описание задачи 1");
        tasksManager.createTask("новая задача 2", "описание задачи 2");
        tasksManager.createEpic("новый эпик 1", "описания эпик 1");
        tasksManager.createSubtask("подзадача 1-1", "описание подзадачи 1-1");
        tasksManager.createSubtask("подзадача 1-2", "описание подзадачи 1-2");
        tasksManager.createEpic("новый эпик 2", "описания эпик 2");
        tasksManager.createSubtask("подзадача 2-1", "описание подзадачи 2-1");
        System.out.println("Все задачи: " + tasksManager.getAllTasks());
        System.out.println("Все эпик задачи: " + tasksManager.getAllEpic());
        System.out.println("Все подзадачи эпиков: " + tasksManager.getAllSubtasksByEpic());
    }
}
