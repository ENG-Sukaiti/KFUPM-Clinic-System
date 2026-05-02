package kfupm.clinic.ds;

import java.util.function.BiConsumer;

/**
 * Students implement AVL tree (balanced BST).
 */
public class AVLTree<K extends Comparable<K>, V> {

    public void put(K key, V value) { throw new UnsupportedOperationException("TODO: AVLTree.put"); }
    public V get(K key) { throw new UnsupportedOperationException("TODO: AVLTree.get"); }
    public void remove(K key) { throw new UnsupportedOperationException("TODO: AVLTree.remove"); }

    /** In-order traversal (sorted by key). */
    public void inOrder(BiConsumer<K, V> visitor) { throw new UnsupportedOperationException("TODO: AVLTree.inOrder"); }

    /** Returns the smallest key/value (leftmost node), or null if empty. */
    public Entry<K, V> minEntry() { throw new UnsupportedOperationException("TODO: AVLTree.minEntry"); }

    public record Entry<K, V>(K key, V value) {}
}
