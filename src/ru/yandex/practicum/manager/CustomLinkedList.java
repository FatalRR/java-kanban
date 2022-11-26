package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;

import java.util.*;

public class CustomLinkedList {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    void linkLast(Task task) {
        Node elem = new Node();
        elem.task = task;
        elem.prev = tail;

        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        if (head == null) {
            head = elem;
        } else {
            tail.next = elem;
        }

        tail = elem;
        elem.next = null;

        history.put(task.getId(), elem);
    }

    List<Task> getTasks() {
        List<Task> resTask = new ArrayList<>();
        Node elem = head;
        while (elem != null) {
            resTask.add(elem.task);
            elem = elem.next;
        }
        return resTask;
    }

    void removeNode(Node node) {
        if (node != null) {
            history.remove(node.task.getId());
            Node prev = node.prev;
            Node next = node.next;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
            }
        }
    }

    Node getNode(int id) {
        return history.get(id);
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;
    }
}