package com.scheduler.main;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.scheduler.dao.BookingDAO;
import com.scheduler.dao.RoomDAO;
import com.scheduler.model.Booking;
import com.scheduler.model.Room;
import com.scheduler.service.MeetingMonitor;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomDAO roomDAO = new RoomDAO();
        BookingDAO bookingDAO = new BookingDAO();
        boolean running = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // --- START THE BACKGROUND THREAD ---
        Thread monitorThread = new Thread(new MeetingMonitor());
        monitorThread.setDaemon(true); // Makes the thread die automatically when the main program exits
        monitorThread.start();
        // -----------------------------------

        System.out.println("=========================================");
        System.out.println("  Welcome to the Smart Meeting Scheduler ");
        System.out.println("=========================================");

        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. View All Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Search Bookings (String Practice)");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            if (choice == 1) {
                System.out.println("\n--- Fetching Available Rooms ---");
                List<Room> rooms = roomDAO.getAllRooms();
                for (Room room : rooms) {
                    System.out.println(room.toString());
                }
                
            } else if (choice == 2) {
                // ... (Keep your existing booking logic here exactly as it was) ...
                System.out.println("\n--- Book a Room ---");
                try {
                    System.out.print("Enter Room ID to book: ");
                    int roomId = scanner.nextInt();
                    scanner.nextLine();
                    
                    System.out.print("Enter Organizer Name: ");
                    String organizer = scanner.nextLine();
                    
                    System.out.print("Enter Start Time (yyyy-MM-dd HH:mm): ");
                    String startStr = scanner.nextLine();
                    Timestamp startSql = Timestamp.valueOf(LocalDateTime.parse(startStr, formatter));
                    
                    System.out.print("Enter End Time (yyyy-MM-dd HH:mm): ");
                    String endStr = scanner.nextLine();
                    Timestamp endSql = Timestamp.valueOf(LocalDateTime.parse(endStr, formatter));
                    
                    if (endSql.before(startSql)) {
                        System.out.println("Error: End time cannot be before start time.");
                        continue;
                    }
                    
                    if (bookingDAO.isRoomAvailable(roomId, startSql, endSql)) {
                        Booking newBooking = new Booking(roomId, organizer, startSql, endSql);
                        if (bookingDAO.bookRoom(newBooking)) {
                            System.out.println("Success! Room booked for " + organizer);
                        } else {
                            System.out.println("Failed to save booking to database.");
                        }
                    } else {
                        System.out.println("Sorry, the room is already booked during that time.");
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Error: Invalid date format. Please exactly follow 'yyyy-MM-dd HH:mm'.");
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                    scanner.nextLine();
                }

            } else if (choice == 3) {
                // --- STRING MANIPULATION PRACTICE ---
                System.out.println("\n--- Search Bookings ---");
                System.out.print("Enter Organizer Name to search: ");
                String searchTerm = scanner.nextLine();
                
                List<Booking> allBookings = bookingDAO.getAllBookings();
                boolean matchFound = false;
                
                for (Booking booking : allBookings) {
                    // Practice: Convert both to lowercase to make the search case-insensitive, 
                    // and use .contains() so "Har" finds "Harsh"
                    String dbName = booking.getOrganizer().toLowerCase();
                    String searchName = searchTerm.toLowerCase();
                    
                    if (dbName.contains(searchName)) {
                        System.out.println("Match Found -> Room ID: " + booking.getRoomId() + 
                                           " | Organizer: " + booking.getOrganizer() + 
                                           " | From: " + booking.getStartTime() + " To: " + booking.getEndTime());
                        matchFound = true;
                    }
                }
                
                if (!matchFound) {
                    System.out.println("No bookings found for: '" + searchTerm + "'");
                }
                
            } else if (choice == 4) {
                System.out.println("Exiting scheduler. Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
