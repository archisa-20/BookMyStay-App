import java.util.*;

// Custom Exception for invalid booking scenarios
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Represents a reservation
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType;
    }
}

// Handles validation logic
class InvalidBookingValidator {

    private Set<String> validRoomTypes;
    private Map<String, Integer> inventory;

    public InvalidBookingValidator() {
        validRoomTypes = new HashSet<>(Arrays.asList("Standard", "Deluxe", "Suite"));

        inventory = new HashMap<>();
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 1);
    }

    // Validate booking request
    public void validate(String guestName, String roomType) throws InvalidBookingException {

        // Validate guest name
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!validRoomTypes.contains(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        // Validate availability
        if (inventory.get(roomType) == 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }
    }

    // Allocate room (only after validation)
    public void allocateRoom(String roomType) throws InvalidBookingException {
        int available = inventory.get(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("Cannot allocate. Inventory exhausted for: " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Main class
public class Bookmystay {

    public static void main(String[] args) {

        InvalidBookingValidator validator = new InvalidBookingValidator();

        // Test cases (valid + invalid scenarios)
        String[][] bookingRequests = {
                {"Alice", "Deluxe"},     // valid
                {"", "Standard"},        // invalid name
                {"Bob", "Luxury"},       // invalid room type
                {"Charlie", "Suite"},    // valid
                {"David", "Suite"}       // no availability
        };

        int counter = 101;

        for (String[] request : bookingRequests) {
            String guestName = request[0];
            String roomType = request[1];

            try {
                // Fail-fast validation
                validator.validate(guestName, roomType);

                // Allocate only if valid
                validator.allocateRoom(roomType);

                // Create reservation
                Reservation reservation = new Reservation("RES" + counter++, guestName, roomType);

                System.out.println("Booking Successful: " + reservation);

            } catch (InvalidBookingException e) {
                // Graceful error handling
                System.out.println("Booking Failed: " + e.getMessage());
            }
        }

        // Show final inventory state
        validator.displayInventory();

        System.out.println("\nSystem remains stable after handling errors.");
    }
}