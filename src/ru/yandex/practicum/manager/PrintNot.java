package ru.yandex.practicum.manager;

enum PrintNot {
    NOT_TASK("Такой задачи нет"),
    NOT_EPIC("Такого эпика нет"),
    NOT_SUBTASK("Такой подзадачи нет"),
    NOT_NULL("Задача не может быть пустой");
    private final String notItem;

    PrintNot(String notItem) {
        this.notItem = notItem;
    }

    @Override
    public String toString() {
        return notItem;
    }
}