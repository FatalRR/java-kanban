package ru.yandex.practicum.manager;

import ru.yandex.practicum.model.tasks.Task;

import java.util.*;

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
            head = elem;
            elem.setPrev(null);
        } else {
            elem.setPrev(tail);
            tail.setNext(elem);
        }

        tail = elem;
        elem.setNext(null);

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

/*            if (head == node) {
                head = node.next;
            }

            if (tail == node) {
                tail = node.prev;
            }

            if (prev != null) {
                prev.setNext(next);
            }

            if (next != null) {
                next.setPrev(prev);
            }*/
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

        public void setTask(Task task) {
            this.task = task;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}