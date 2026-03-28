package com.scheduler.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.scheduler.dao.DatabaseConnection;

public class MeetingMonitor implements Runnable {

    @Override
    public void run() {
        Timestamp lastCheckTime = new Timestamp(System.currentTimeMillis());
        System.out.println("\n[MONITOR START] Background thread initialized at: " + lastCheckTime);

        while (true) {
            try {
                Thread.sleep(30000); // Check every 30 seconds
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                
                checkCompletedMeetings(lastCheckTime, currentTime);
                
                lastCheckTime = currentTime;

            } catch (InterruptedException e) {
                System.out.println("Background monitor was interrupted.");
                break; 
            }
        }
    }

    private void checkCompletedMeetings(Timestamp fromTime, Timestamp toTime) {
        // We added booking_id to this query so we know exactly which row to delete
        String selectQuery = "SELECT booking_id, room_id, organizer FROM bookings WHERE end_time > ? AND end_time <= ?";
        
        // This is our new cleanup query
        String deleteQuery = "DELETE FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            
            selectStmt.setTimestamp(1, fromTime);
            selectStmt.setTimestamp(2, toTime);
            
            ResultSet rs = selectStmt.executeQuery();
            
            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                int roomId = rs.getInt("room_id");
                String organizer = rs.getString("organizer");
                
                // 1. Notify the User
                System.out.println("\n=======================================================");
                System.out.println("[SYSTEM NOTIFICATION]: Meeting in Room " + roomId + " (Org: " + organizer + ") has concluded!");
                System.out.println("[SYSTEM ACTION]: Deleting old booking data to free up database space...");
                System.out.println("=======================================================");
                System.out.print("Choose an option: "); 
                
                // 2. Delete the old booking!
                deleteStmt.setInt(1, bookingId);
                deleteStmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.out.println("Monitor Error: Failed to check or clean up completed meetings.");
            e.printStackTrace();
        }
    }
}
