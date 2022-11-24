package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* Наставник посоветовал вынести в отдельный класс логику реализации кастомной коллекции, хотя в тз написано,
что сделать в одном классе*/
public class CustomLinkedList {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    void linkLast(Task task) {
        Node elem = new Node();
        elem.setTask(task);

        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        if (head == null) {
            tail = elem;
            head = elem;
            elem.setNext(null);
            elem.setPrev(null);
        } else {
            elem.setPrev(tail);
            elem.setNext(null);
            tail.setNext(elem);
            tail = elem;
        }

        history.put(task.getId(), elem);
    }

    List<Task> getTasks() {
        List<Task> resTask = new ArrayList<>();
        Node elem = head;
        while (elem != null) {
            resTask.add(elem.getTask());
            elem = elem.getNext();
        }
        return resTask;
    }

    void removeNode(Node node) {
        if (node != null) {
            history.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }

            if (tail == node) {
                tail = node.getPrev();
            }

            if (prev != null) {
                prev.setNext(next);
            }

            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    Node getNode(int id) {
        return history.get(id);
    }
}
