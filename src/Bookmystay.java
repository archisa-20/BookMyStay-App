import java.util.*;

// Booking Request Model
class BookingRequest {
    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
        notifyAll(); // wake up waiting threads
    }

    public synchronized BookingRequest getRequest() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return queue.poll();
    }
}

// Shared Inventory (Critical Section Protected)
class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryManager() {
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 1);
    }

    // synchronized ensures thread-safe allocation
    public synchronized boolean allocateRoom(String roomType, String guestName) {

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            System.out.println(Thread.currentThread().getName() +
                    " allocated " + roomType + " to " + guestName);

            inventory.put(roomType, available - 1);

            // simulate processing delay (to expose race conditions if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            return true;
        } else {
            System.out.println(Thread.currentThread().getName() +
                    " FAILED for " + guestName + " (No " + roomType + " available)");
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// Worker Thread (Consumer)
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private InventoryManager inventory;

    public BookingProcessor(String name, BookingQueue queue, InventoryManager inventory) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        // Each thread processes 2 requests (for demo)
        for (int i = 0; i < 2; i++) {
            BookingRequest request = queue.getRequest();

            inventory.allocateRoom(
                    request.getRoomType(),
                    request.getGuestName()
            );
        }
    }
}

// Main Class
public class Bookmystay{

    public static void main(String[] args) {

        BookingQueue queue = new BookingQueue();
        InventoryManager inventory = new InventoryManager();

        // Simulate multiple guest requests
        queue.addRequest(new BookingRequest("Alice", "Deluxe"));
        queue.addRequest(new BookingRequest("Bob", "Deluxe"));
        queue.addRequest(new BookingRequest("Charlie", "Suite"));
        queue.addRequest(new BookingRequest("David", "Suite"));

        // Create multiple threads (simulating concurrent users)
        BookingProcessor t1 = new BookingProcessor("Thread-1", queue, inventory);
        BookingProcessor t2 = new BookingProcessor("Thread-2", queue, inventory);

        // Start threads
        t1.start();
        t2.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Final state
        inventory.displayInventory();

        System.out.println("\nSystem maintained consistency under concurrent load.");
    }
}