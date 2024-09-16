package ru.yandex.practicum.filmorate;


import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/filmsdb";
        String user = "dbuser";
        String password = "12345";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to PostgreSQL server successfully.");

            Statement stmt = conn.createStatement();
            String createTableQuery =
                    "CREATE TABLE users (" +
                            "id INT PRIMARY KEY NOT NULL, " +
                            "name TEXT NOT NULL, " +
                            "age INT NOT NULL, " +
                            "city TEXT NOT NULL);";

            String insertQuery = "INSERT INTO users (id, name, age, city) VALUES (1, 'Simpa', 8, 'Ufa')";

            stmt.executeUpdate(createTableQuery);
            stmt.executeUpdate(insertQuery);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

