package kfupm.clinic.ds;

import java.util.List;

/** Students implement. */
public class SinglyLinkedList<T> {

    private class Node<T> {
        private T data;
        private Node<T> next; 

        Node(){
            this(null, null);
        }

        Node(T data){
            this(data, null);
        }

        Node(T data, Node<T> node){
            this.data = data;
            this.next = node;
        }
    }

    private Node<T> head, tail;

    SinglyLinkedList(){
        head = tail = null;
    }

    boolean isEmpty() {
        return head == tail;
    }

    public void addToHead(T data){
        Node<T> newNode = new Node<T>(data);

        if (isEmpty()){
            head = tail = newNode;
            return;
        }

        newNode.next = head;
        head = newNode;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<T>(data);
        if (isEmpty()){
            head = tail = newNode;
        }

        tail.next = newNode;
        tail = newNode;
    }
    public List<T> toList() {
        List<T> list = new List<T>();

        Node<T> ptr = head;

        while (ptr.next != null){
            list.add(ptr.data);
        }

        return list;
    }
}
