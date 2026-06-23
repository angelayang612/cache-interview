package org.intern;

import java.util.*;

class Node {
    int key, value;
    Node prev, next;
    Node(int k, int v) { key = k; value = v; }
}

class LRUCache {
    private final Map<Integer, Node> map;
    private final Node head, tail; // sentinels: head=MRU end, tail=LRU end
    private final int capacity;

    LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public Integer get(int key) {
        Node node = map.get(key);
        if (node == null) return null;
        removeNode(node);
        addToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            removeNode(node);
            addToFront(node);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
        Node newNode = new Node(key, value);
        addToFront(newNode);
        map.put(key, newNode);
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

public class Solution {
    public static void main(String[] args) {
        // basic get/put
        LRUCache cache = new LRUCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assert cache.get(1) == 10 : "get existing key";

        // eviction: key 2 is LRU after get(1), adding key 3 evicts key 2
        cache.put(3, 30);
        assert cache.get(2) == null : "evicted key returns null";
        assert cache.get(3) == 30 : "recently added key survives";

        // re-access promotes to MRU: get(1) promotes 1, next eviction removes 3
        cache.get(1);
        cache.put(4, 40);
        assert cache.get(3) == null : "lru evicted after re-access of other key";
        assert cache.get(1) == 10 : "promoted key survives";
        assert cache.get(4) == 40 : "new key survives";

        // update existing key
        cache.put(1, 99);
        assert cache.get(1) == 99 : "update existing key";

        // missing key
        assert cache.get(999) == null : "missing key returns null";

        System.out.println("All tests completed!");
    }
}
