import java.util.Arrays; 
 
public class HashTableDivideAndConquer { 
    private String[] table; 
    private int capacity; 
    private int size; 
    private static final String DELETED = "__DELETED__"; // Marker for deleted entries
    private static final double LOAD_FACTOR_THRESHOLD = 0.75; // 75% load factor
 
    // Constructor to initialize the hash table with a given capacity 
    public HashTableDivideAndConquer(int capacity) { 
        this.capacity = capacity;
        this.table = new String[capacity];
        this.size = 0;
    } 
 
    // Simple hash function to map a string key to an index 
    private int hash(String key) { 
        int hashValue = 0;
        for (int i = 0; i < key.length(); i++) {
            hashValue = (hashValue * 31 + key.charAt(i)) % capacity;
        }
        return Math.abs(hashValue);
    } 
 
    // Insert a key into the hash table 
    public void insert(String key) { 
        // Check if resize is needed (load factor > 75%)
        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        
        int index = hash(key);
        int originalIndex = index;
        
        // Linear probing for collision resolution
        while (table[index] != null && !table[index].equals(DELETED)) {
            if (table[index].equals(key)) {
                // Key already exists, update it
                return;
            }
            index = (index + 1) % capacity; // Linear probing
            
            // If we've looped around, table is full
            if (index == originalIndex) {
                resize();
                insert(key); // Try inserting again after resize
                return;
            }
        }
        
        table[index] = key;
        size++;
    } 
 
    // Search for a key in the hash table 
    public boolean search(String key) { 
        int index = hash(key);
        int originalIndex = index;
        
        // Linear probing to find the key
        while (table[index] != null) {
            if (table[index].equals(key)) {
                return true;
            }
            index = (index + 1) % capacity;
            
            // If we've looped around, key not found
            if (index == originalIndex) {
                break;
            }
        }
        
        return false;
    } 
 
    // Delete a key from the hash table 
    public void delete(String key) { 
        int index = hash(key);
        int originalIndex = index;
        
        // Linear probing to find the key
        while (table[index] != null) {
            if (table[index].equals(key)) {
                table[index] = DELETED; // Mark as deleted
                size--;
                rehash(); // Rehash to fill gaps
                return;
            }
            index = (index + 1) % capacity;
            
            // If we've looped around, key not found
            if (index == originalIndex) {
                break;
            }
        }
    } 
 
    // Divide and Conquer Resize: Rehash the table by dividing the task into smaller chunks 
    private void resize() { 
        System.out.println("Resizing from capacity " + capacity + " to " + (capacity * 2));
        // Snapshot existing entries to rehash after capacity grows
        String[] oldTable = Arrays.copyOf(table, table.length);
        int oldCapacity = capacity;
        
        // Double the capacity
        capacity *= 2;
        table = new String[capacity];
        size = 0;
        
        // Divide and conquer approach: split the old table into chunks
        divideAndConquerRehash(oldTable, 0, oldCapacity - 1);
    }
    
    // Divide and conquer helper for rehashing
    private void divideAndConquerRehash(String[] oldTable, int start, int end) {
        // Base case: if range is small enough, process directly
        if (end - start <= 10) {
            for (int i = start; i <= end; i++) {
                if (oldTable[i] != null && !oldTable[i].equals(DELETED)) {
                    insert(oldTable[i]);
                }
            }
            return;
        }
        
        // Divide: split the range in half
        int mid = start + (end - start) / 2;
        
        // Conquer: recursively process both halves
        divideAndConquerRehash(oldTable, start, mid);
        divideAndConquerRehash(oldTable, mid + 1, end);
    } 
 
    // Rehash remaining elements to fill gaps after a deletion (Divide and Conquer Approach) 
    private void rehash() { 
        // Find all DELETED markers and compact the table
        String[] tempTable = new String[capacity];
        int tempSize = 0;
        
        // Collect all non-deleted, non-null elements
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && !table[i].equals(DELETED)) {
                tempTable[tempSize++] = table[i];
            }
        }
        
        // Clear the table
        table = new String[capacity];
        size = 0;
        
        // Reinsert using divide and conquer
        divideAndConquerRehashHelper(tempTable, 0, tempSize - 1);
    }
    
    // Helper method for divide and conquer rehashing after deletion
    private void divideAndConquerRehashHelper(String[] elements, int start, int end) {
        // Base case
        if (start > end) {
            return;
        }
        
        if (end - start <= 5) {
            for (int i = start; i <= end; i++) {
                if (elements[i] != null) {
                    insertWithoutResize(elements[i]);
                }
            }
            return;
        }
        
        // Divide
        int mid = start + (end - start) / 2;
        
        // Conquer
        divideAndConquerRehashHelper(elements, start, mid);
        divideAndConquerRehashHelper(elements, mid + 1, end);
    }
    
    // Insert without checking for resize (used during rehashing)
    private void insertWithoutResize(String key) {
        int index = hash(key);
        int originalIndex = index;
        
        while (table[index] != null && !table[index].equals(DELETED)) {
            if (table[index].equals(key)) {
                return;
            }
            index = (index + 1) % capacity;
            
            if (index == originalIndex) {
                return; // Table full
            }
        }
        
        table[index] = key;
        size++;
    } 
 
    // Print the hash table for debugging
    public void printTable() { 
        System.out.println("\n=== Hash Table (Size: " + size + ", Capacity: " + capacity + 
                           ", Load Factor: " + String.format("%.2f", (double)size/capacity) + ") ===");
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && !table[i].equals(DELETED)) {
                System.out.println("Index " + i + ": " + table[i]);
            } else if (table[i] != null && table[i].equals(DELETED)) {
                System.out.println("Index " + i + ": [DELETED]");
            }
        }
        System.out.println("==================\n");
        // full view
        System.out.println("Full table: " + Arrays.toString(table));
        System.out.println("==================\n");
    } 
 
    public static void main(String[] args) { 
        HashTableDivideAndConquer unihashTable = new HashTableDivideAndConquer(20); 
 
        // Insert keys 
        unihashTable.insert("ATU Letterkenny"); 
        unihashTable.insert("ATU Killybegs"); 
        unihashTable.insert("ATU Sligo"); 
        unihashTable.insert("ATU Galway Mayo"); 
        unihashTable.insert("ATU Killybegs 1"); //add 15 more sample university campuses 
        unihashTable.insert("ATU Donegal");
        unihashTable.insert("ATU Monaghan");
        unihashTable.insert("ATU Cavan");
        unihashTable.insert("ATU Athlone");
        unihashTable.insert("ATU Limerick");
        unihashTable.insert("ATU Dublin 2");
        unihashTable.insert("ATU Dublin 3");
        unihashTable.insert("ATU Dublin");
        unihashTable.insert("ATU Carlow");
        unihashTable.insert("ATU Wexford");
        unihashTable.insert("ATU Waterford");
        unihashTable.insert("ATU Dundalk");
        unihashTable.insert("ATU Dundalk 2");
        unihashTable.insert("ATU Dundalk 3");
        unihashTable.insert("ATU Dundalk 4");

 
 
        // Print the hash table 
        unihashTable.printTable(); 
 
        // Search for a key 
        System.out.println("Is 'ATU Sligo' in the table? " + unihashTable.search("ATU Sligo"));  // true 
        System.out.println("Is 'ATU Dundalk' in the table? " + unihashTable.search("ATU Dundalk"));  
// false 
 
        // Delete a key 
        unihashTable.delete("ATU Galway Mayo"); 
        unihashTable.printTable(); 
    } 
}