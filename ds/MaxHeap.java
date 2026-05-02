package kfupm.clinic.ds;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * MaxHeap starter.
 *
 * Day-1 safety rule:
 * - Constructors must NEVER throw, so the program can start.
 * - Operations may throw UnsupportedOperationException until students implement them;
 *   the command dispatcher will catch and print [NOT SUPPORTED] instead of crashing.
 */


public class MaxHeap<T> {

    protected final Comparator<T> comparator;
    private Object[] heap;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MaxHeap(Comparator<T> comparator) {
        if (comparator == null) throw new IllegalArgumentException("comparator is null");
        this.comparator = comparator;
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    private void ensureCapacity() {
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @SuppressWarnings("unchecked")
    private boolean isLessThan(int i, int j) {
        return comparator.compare((T) heap[i], (T) heap[j]) < 0;
    }

    private void heapifyUp(int index) {
        int parent = (index - 1) / 2;
        while (index > 0 && isLessThan(parent, index)) {
            swap(index, parent);
            index = parent;
            parent = (index - 1) / 2;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int largest = index;

            if (leftChild < size && isLessThan(largest, leftChild)) {
                largest = leftChild;
            }
            if (rightChild < size && isLessThan(largest, rightChild)) {
                largest = rightChild;
            }

            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }

    public void push(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot push null item");
        ensureCapacity();
        heap[size] = item;
        heapifyUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) return null;
        T root = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null; 
        size--;
        heapifyDown(0);
        return root;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) return null;
        return (T) heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Non-destructive view for printing. */
    @SuppressWarnings("unchecked")
    public List<T> toListSnapshot() {
        List<T> snapshot = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            snapshot.add((T) heap[i]);
        }
        return snapshot;
    }
}
