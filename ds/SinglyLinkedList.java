package kfupm.clinic.ds;

import java.util.ArrayList;
import java.util.List;

/** Students implement. */

public class SinglyLinkedList<T> {

    private static class Node<T> {
        private T data;
        private Node<T> next; 

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head, tail;

    public SinglyLinkedList() {
        head = tail = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> ptr = head;
        while (ptr != null) {
            list.add(ptr.data);
            ptr = ptr.next; 
        }
        return list;
    }
} 