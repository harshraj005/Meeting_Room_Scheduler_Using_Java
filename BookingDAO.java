package com.scheduler.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.scheduler.model.Booking;

public class BookingDAO {

    // 1. Check if the room is available for the requested time slots
    public boolean isRoomAvailable(int roomId, Timestamp requestedStart, Timestamp requestedEnd) {
        boolean available = true;
        
        // The Overlap Query: Checks if any existing meeting conflicts with the requested times
        String query = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND start_time < ? AND end_time > ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, requestedEnd);   // Note the order!
            pstmt.setTimestamp(3, requestedStart); // Note the order!
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int conflictCount = rs.getInt(1);
                if (conflictCount > 0) {
                    available = false; // A conflict was found
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking room availability.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return available;
    }

    // 2. Insert the new booking into the database
    public boolean bookRoom(Booking booking) {
        String query = "INSERT INTO bookings (room_id, organizer, start_time, end_time) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            
            pstmt.setInt(1, booking.getRoomId());
            pstmt.setString(2, booking.getOrganizer());
            pstmt.setTimestamp(3, booking.getStartTime());
            pstmt.setTimestamp(4, booking.getEndTime());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the insert was successful
            
        } catch (SQLException e) {
            System.out.println("Error saving the booking.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 // 3. Fetch all bookings for search functionality
    public List<Booking> getAllBookings() {
        List<Booking> bookingList = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("booking_id");
                int roomId = rs.getInt("room_id");
                String organizer = rs.getString("organizer");
                Timestamp start = rs.getTimestamp("start_time");
                Timestamp end = rs.getTimestamp("end_time");
                
                Booking booking = new Booking(roomId, organizer, start, end);
                booking.setBookingId(id);
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bookings.");
            e.printStackTrace();
        }
        return bookingList;
    }
}