import java.util.*;

/**
 * Reservation class
 * Represents a guest booking request.
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * BookingRequestQueue
 * Stores booking requests in FIFO order.
 */
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Booking request received from " + reservation.getGuestName());
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }
}

/**
 * RoomInventory
 * Maintains centralized availability state.
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {

        System.out.println("\nCurrent Inventory Status:");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

/**
 * RoomAllocationService
 * Processes booking requests and assigns unique room IDs.
 */
class RoomAllocationService {

    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;

    public RoomAllocationService(RoomInventory inventory) {

        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
    }

    public void processBooking(Reservation reservation) {

        String roomType = reservation.getRoomType();

        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            System.out.println("Booking failed for " + reservation.getGuestName() +
                    " - No rooms available for " + roomType);
            return;
        }

        String roomId = generateRoomId(roomType);

        allocatedRooms
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        inventory.decrementRoom(roomType);

        System.out.println("Reservation Confirmed!");
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Room Type: " + roomType);
        System.out.println("Allocated Room ID: " + roomId);
        System.out.println();
    }

    private String generateRoomId(String roomType) {

        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();

        int number = new Random().nextInt(900) + 100;

        return prefix + number;
    }

    public void displayAllocatedRooms() {

        System.out.println("\nAllocated Rooms:");

        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {

            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

/**
 * Use Case 6: Reservation Confirmation & Room Allocation
 */
public class Bookmystay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Single Room"));

        System.out.println("\nProcessing Booking Requests...\n");

        while (queue.hasRequests()) {

            Reservation request = queue.getNextRequest();

            allocationService.processBooking(request);
        }

        allocationService.displayAllocatedRooms();

        inventory.displayInventory();
    }
}