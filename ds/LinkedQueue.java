package kfupm.clinic.ds;

import java.util.List;

/** Students implement. */

public class LinkedQueue<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head, tail;

    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public T dequeue() {
        if (isEmpty()) return null;
        T item = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        return item;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}