import java.util.HashMap; 
 
/**
 * LRU (Least Recently Used) Cache implementation.
 *
 * Data structures:
 * - HashMap<Integer, Node> for O(1) key → node lookup.
 * - Doubly linked list to maintain recency: `head` is MRU, `tail` is LRU.
 *
 * Operations:
 * - get: O(1) lookup, moves node to `head` (mark MRU).
 * - put: O(1) insert/update; on capacity, evicts `tail` (LRU) before inserting.
 * - delete: O(1) remove from both list and map.
 */
class LRUCache { 
    // Node class for doubly linked list representing one cache entry
    class Node { 
        int key, value; 
        Node prev, next; 
         
        Node(int key, int value) { 
            this.key = key; 
            this.value = value; 
        } 
    } 
 
    // Fast key → node mapping for O(1) access
    private HashMap<Integer, Node> cache; 
    // Fixed capacity and current size of the cache
    private int capacity, size; 
    // Doubly linked list anchors: `head` (MRU) and `tail` (LRU)
    private Node head, tail; 
 
    // Constructor initializes empty list and map with given capacity
    public LRUCache(int capacity) { 
        this.capacity = capacity; 
        this.size = 0; 
        this.cache = new HashMap<>(); 
        this.head = null; 
        this.tail = null; 
    } 
 
    // Move a node to the front (most recently used)
    // No-op if the node is already the head
    private void moveToFront(Node node) { 
        if (node == head) return; 
        remove(node); 
        addToFront(node); 
    } 
 
    // Add a node to the front (MRU position) of the list
    private void addToFront(Node node) { 
        node.prev = null; 
        node.next = head; 
        if (head != null) { 
            head.prev = node; 
        } 
        head = node; 
        // If the list was empty, set tail to the new node as well
        if (tail == null) { 
            tail = node; 
        } 
    } 
 
    // Remove a node from the list in O(1) by relinking neighbors
    private void remove(Node node) { 
        if (node.prev != null) { 
            node.prev.next = node.next; 
        } else { 
            // Node was head; advance head
            head = node.next; 
        } 
        if (node.next != null) { 
            node.next.prev = node.prev; 
        } else { 
            // Node was tail; retract tail
            tail = node.prev; 
        } 
        // Clear pointers to avoid accidental misuse
        node.prev = null; 
        node.next = null; 
    } 
 
    // Get the value for `key` if present; marks entry as MRU
    public int get(int key) { 
        Node node = cache.get(key); 
        if (node == null) return -1; 
        // Touching (access) promotes node to MRU
        moveToFront(node); 
        return node.value;  // Key found 
    } 
 
    // Insert or update a key-value pair.
    // When inserting and at capacity, evict the LRU (tail) first.
    public void put(int key, int value) { 
        Node node = cache.get(key); 
        if (node != null) { 
            // Update existing value and mark as MRU
            node.value = value; 
            moveToFront(node); 
            return; 
        } 
        // Cache is full: evict least recently used (tail)
        if (size == capacity) { 
            if (tail != null) { 
                // Remove from HashMap and unlink from list
                cache.remove(tail.key); 
                remove(tail); 
                size--; 
            } 
        } 
        // Create and insert new node at MRU position
        Node newNode = new Node(key, value); 
        addToFront(newNode); 
        cache.put(key, newNode); 
        size++; 
    } 
 
    // Delete a key from the cache (no-op if missing)
    public void delete(int key) { 
        Node node = cache.get(key); 
        if (node == null) return; 
        remove(node); 
        cache.remove(key); 
        size--; 
    } 
 
    // Print current order for debugging: from MRU (head) to LRU (tail)
    public void printCache() { 
        System.out.println("\n=== LRU Cache (Size: " + size + ", Capacity: " + capacity + ") ==="); 
        Node cur = head; 
        System.out.print("[MRU] "); 
        while (cur != null) { 
            System.out.print("(" + cur.key + ":" + cur.value + ") "); 
            cur = cur.next; 
        } 
        System.out.println("[LRU]"); 
    } 
 
    // Demo: exercise put/get/delete to show LRU behavior and eviction
    public static void main(String[] args) { 
        LRUCache lruCache = new LRUCache(3);  // Cache capacity of 3 
         
        // Insert three items: list will be [MRU 3, 2, 1 LRU]
        lruCache.put(1, 102345); 
        lruCache.put(2, 102342); 
        lruCache.put(3, 102303); 
        lruCache.printCache();   
         
        // Access key 2 → promotes 2 to MRU: [MRU 2, 3, 1 LRU]
        System.out.println("Get 2: " + lruCache.get(2));  // Should return 102342 
        lruCache.printCache();   
 
        // Insert keys 4..20; with capacity=3, older entries are evicted as needed
        for (int k = 4; k <= 20; k++) { 
            lruCache.put(k, 100000 + k); 
        } 
        lruCache.printCache(); 
        // Add more items beyond capacity to further evict LRU elements
        lruCache.put(21, 100021); 
        lruCache.put(22, 100022); 
        lruCache.printCache();   
 
        // Access key 3 (if present) and print; otherwise returns -1
        System.out.println("Get 3: " + lruCache.get(3));   
        lruCache.printCache();   
 
        // Delete a key and show updated cache
        lruCache.delete(10); 
        lruCache.printCache(); 
    } 
}