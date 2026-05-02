package kfupm.clinic.ds;

/** Students implement. */
public class LinkedStack<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data, Node<T> next) { this.data = data; this.next = next; }
    }

    private Node<T> top;

    public void push(T item) {
        top = new Node<>(item, top);
    }

    public T pop() {
        if (isEmpty()) return null;
        T item = top.data;
        top = top.next;
        return item;
    }

    public T peek() {
        return isEmpty() ? null : top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }
}