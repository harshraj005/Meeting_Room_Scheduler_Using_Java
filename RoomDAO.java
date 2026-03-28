package com.scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.scheduler.model.Room;

public class RoomDAO {

    // Method to fetch all rooms from the database
    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 1. Get the connection
            conn = DatabaseConnection.getConnection();
            
            // 2. Prepare the SQL statement
            pstmt = conn.prepareStatement(query);
            
            // 3. Execute the query and get the results
            rs = pstmt.executeQuery();
            
            // 4. Loop through the result set
            while (rs.next()) {
                int id = rs.getInt("room_id");
                String name = rs.getString("room_name");
                int capacity = rs.getInt("capacity");
                boolean hasProjector = rs.getBoolean("has_projector");
                boolean hasVideoConf = rs.getBoolean("has_videoconf");
                
                // Create a new Room object and add it to our list
                Room room = new Room(id, name, capacity, hasProjector, hasVideoConf);
                roomList.add(room);
            }
            
        } catch (SQLException e) {
            System.out.println("Error: Failed to fetch rooms from the database.");
            e.printStackTrace();
        } finally {
            // 5. Always close your database resources in a finally block to prevent memory leaks!
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return roomList;
    }
}