import java.util.*;

// Represents a reservation
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private double baseCost;

    public Reservation(String reservationId, String guestName, String roomType, double baseCost) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.baseCost = baseCost;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getBaseCost() {
        return baseCost;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room: " + roomType +
                ", Cost: ₹" + baseCost;
    }
}

// Maintains booking history
class BookingHistory {

    // List preserves insertion order
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Retrieve all bookings
    public List<Reservation> getAllReservations() {
        return confirmedBookings;
    }
}

// Generates reports
class BookingReportService {

    // Display all bookings
    public void displayAllBookings(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("=== Booking History ===");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummary(List<Reservation> reservations) {
        int totalBookings = reservations.size();
        double totalRevenue = 0;

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : reservations) {
            totalRevenue += r.getBaseCost();

            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("\n=== Booking Summary Report ===");
        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);

        System.out.println("\nBookings by Room Type:");
        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// Main class
public class Bookmystay {

    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("RES101", "Alice", "Deluxe", 3000);
        Reservation r2 = new Reservation("RES102", "Bob", "Suite", 5000);
        Reservation r3 = new Reservation("RES103", "Charlie", "Deluxe", 3000);

        // Add to history (in order of confirmation)
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Admin views all bookings
        reportService.displayAllBookings(history.getAllReservations());

        // Admin requests summary report
        reportService.generateSummary(history.getAllReservations());
    }
}