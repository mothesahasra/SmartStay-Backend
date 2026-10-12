package com.smartstay;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseTestController {

    @GetMapping("/test-db")
    public String testDatabase() {
        String url = "jdbc:mysql://localhost:3306/smartstay_db";
        String username = "root";
        String password = "sahasramothe";

        try (Connection connection =
                 DriverManager.getConnection(url, username, password)) {
            return "MySQL connection successful!";
        } catch (Exception e) {
            return "MySQL connection failed: " + e.getMessage();
        }
    }
}
