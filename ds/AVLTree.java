package kfupm.clinic.ds;

import java.util.function.BiConsumer;

/**
 * Students implement AVL tree (balanced BST).
 */

public class AVLTree<K extends Comparable<K>, V> {

    private class Node {
        K key;
        V value;
        int height;
        Node left, right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;

    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int getBalance(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private Node balance(Node node) {
        updateHeight(node);
        int balance = getBalance(node);

        // Left Heavy
        if (balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        // Right Heavy
        if (balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private Node put(Node node, K key, V value) {
        if (node == null) {
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        return balance(node);
    }

    public V get(K key) {
        Node n = get(root, key);
        return n == null ? null : n.value;
    }

    private Node get(Node node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        }
        return node;
    }

    public void remove(K key) {
        root = remove(root, key);
    }

    private Node remove(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node temp = getMin(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = remove(node.right, temp.key);
            }
        }

        if (node == null) {
            return null;
        }

        return balance(node);
    }

    private Node getMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public Entry<K, V> minEntry() {
        if (root == null) {
            return null;
        }
        Node minNode = getMin(root);
        return new Entry<>(minNode.key, minNode.value);
    }

    public void inOrder(BiConsumer<K, V> visitor) {
        inOrder(root, visitor);
    }

    private void inOrder(Node node, BiConsumer<K, V> visitor) {
        if (node != null) {
            inOrder(node.left, visitor);
            visitor.accept(node.key, node.value);
            inOrder(node.right, visitor);
        }
    }

    public record Entry<K, V>(K key, V value) {}
}
