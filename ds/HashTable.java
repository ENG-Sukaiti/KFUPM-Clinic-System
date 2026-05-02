package kfupm.clinic.ds;

/**
 * Students implement a hash table.
 * Recommended collision handling: separate chaining.
 */
public class HashTable<K, V> {
    private static class HashNode<K, V> {
        K key;
        V value;
        HashNode<K, V> next;
        HashNode(K key, V value) { this.key = key; this.value = value; }
    }

    private HashNode<K, V>[] buckets;
    private int size;
    private static final int INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = new HashNode[INITIAL_CAPACITY];
        size = 0;
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    public void put(K key, V value) {
        int index = getBucketIndex(key);
        HashNode<K, V> head = buckets[index];
        
        while (head != null) {
            if (head.key.equals(key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }
        
        size++;
        head = buckets[index];
        HashNode<K, V> newNode = new HashNode<>(key, value);
        newNode.next = head;
        buckets[index] = newNode;
        
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        HashNode<K, V> head = buckets[index];
        while (head != null) {
            if (head.key.equals(key)) return head.value;
            head = head.next;
        }
        return null;
    }

    public V remove(K key) {
        int index = getBucketIndex(key);
        HashNode<K, V> head = buckets[index];
        HashNode<K, V> prev = null;
        
        while (head != null) {
            if (head.key.equals(key)) {
                break;
            }
            prev = head;
            head = head.next;
        }
        
        if (head == null) return null;
        size--;
        if (prev != null) {
            prev.next = head.next;
        } else {
            buckets[index] = head.next;
        }
        return head.value;
    }

    public int size() {
        return size;
    }
}