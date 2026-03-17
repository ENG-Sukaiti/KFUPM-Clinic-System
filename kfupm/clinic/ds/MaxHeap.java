package kfupm.clinic.ds;

import java.util.Comparator;
import java.util.List;

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

    public MaxHeap(Comparator<T> comparator) {
        if (comparator == null) throw new IllegalArgumentException("comparator is null");
        this.comparator = comparator;
    }

    public void push(T item) { throw new UnsupportedOperationException("TODO: MaxHeap.push"); }
    public T pop() { throw new UnsupportedOperationException("TODO: MaxHeap.pop"); }
    public T peek() { throw new UnsupportedOperationException("TODO: MaxHeap.peek"); }
    public boolean isEmpty() { throw new UnsupportedOperationException("TODO: MaxHeap.isEmpty"); }

    /** Non-destructive view for printing. */
    public List<T> toListSnapshot() { throw new UnsupportedOperationException("TODO: MaxHeap.toListSnapshot"); }
}
