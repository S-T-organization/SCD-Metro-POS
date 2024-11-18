package backend;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CheckConnectionOfInternet {

    private static final String TEMP_FILE = "temp.txt";
    private static final String DATA_FILE = "data.txt";
    private static final String UPDATE_FILE = "update.txt"; // Separate file for update queries
    private static Connection conn; // Static connection initialized once
    private static boolean wasInternetAvailable = false; // Tracks previous internet state
    private static String lastErrorMessage = null; // Tracks the last SQL error

    public CheckConnectionOfInternet() {
        initializeConnection();
    }

    private static void initializeConnection() {
        try {
            conn = DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection established successfully.");
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            System.out.println("Error initializing connection: " + e.getMessage());
        }
    }

    public static boolean isInternetAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("ping -n 1 8.8.8.8"); // For Windows
            int returnVal = process.waitFor();
            if (returnVal == 0) {
                if (conn == null || conn.isClosed()) {
                    System.out.println("Attempting to re-establish database connection...");
                    initializeConnection();
                }
                return conn != null && !conn.isClosed();
            } else {
                System.out.println("Ping failed: Internet connection is unavailable.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error checking internet availability: " + e.getMessage());
            return false;
        }
    }

    public static void writeTempFile(boolean status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE))) {
            writer.write(String.valueOf(status));
        } catch (IOException e) {
            System.out.println("Error writing to temp file: " + e.getMessage());
        }
    }

    public static boolean readTempFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_FILE))) {
            String status = reader.readLine();
            return Boolean.parseBoolean(status);
        } catch (IOException e) {
            return false; // Default to false if file is missing or unreadable
        }
    }

    public static void saveOperationToFile(String tableName, String columns, String values) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(tableName + "\n");
            writer.write(columns + "\n");
            writer.write(values + "\n");
        } catch (IOException e) {
            System.out.println("Error saving operation to file: " + e.getMessage());
        }
    }

    public static void processPendingData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            StringBuilder newData = new StringBuilder(); // To hold remaining data
            String line;

            while ((line = reader.readLine()) != null) {
                String tableName = line;
                String columns = reader.readLine();
                String values = reader.readLine();

                if (columns == null || columns.isEmpty() || values == null || values.isEmpty()) {
                    System.out.println("Invalid file format for table: " + tableName);
                    continue;
                }

                System.out.println("Processing: " + tableName + " -> " + columns + " -> " + values);

                // Try inserting into the database
                boolean success = insertIntoTable(tableName, columns, values);

                if (!success) {
                    if (isDuplicateEntryError()) {
                        System.out.println("Duplicate entry detected. Skipping: " + tableName + " -> " + values);
                    } else {
                        System.out.println("Failed to restore data. Re-queuing: " + tableName + " -> " + values);
                        newData.append(tableName).append("\n")
                                .append(columns).append("\n")
                                .append(values).append("\n");
                    }
                }
            }

            // Overwrite the file with remaining operations
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                writer.write(newData.toString());
            }

            writeTempFile(false); // Reset temp flag
        } catch (IOException e) {
            System.out.println("Error processing data file: " + e.getMessage());
        }
    }

    private static boolean insertIntoTable(String tableName, String columns, String values) {
        if (conn == null) {
            System.out.println("Cannot insert data. Database connection is null.");
            return false;
        }

        String[] columnArray = columns.split(",");
        String[] valueArray = values.split(",");

        if (columnArray.length != valueArray.length) {
            System.out.println("Column and value count mismatch for table: " + tableName);
            return false;
        }

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        query.append(String.join(",", columnArray));
        query.append(") VALUES (");
        query.append("?,".repeat(columnArray.length - 1)).append("?)");

        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < valueArray.length; i++) {
                pstmt.setString(i + 1, valueArray[i].trim());
            }
            pstmt.executeUpdate();
            lastErrorMessage = null; // Reset on success
            return true;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Error inserting into table: " + tableName + " - " + lastErrorMessage);
            return false;
        }
    }

    private static boolean isDuplicateEntryError() {
        return lastErrorMessage != null && lastErrorMessage.contains("Duplicate entry");
    }

    public static void monitorInternet() {
        new Thread(() -> {
            while (true) {
                try {
                    boolean isConnected = isInternetAvailable();
                    boolean hasPendingData = readTempFile();

                    if (!isConnected) {
                        System.out.println("Internet connection is unavailable.");
                    } else {
                        System.out.println("Internet is HEREE!!");
                    }

                    if (isConnected && !wasInternetAvailable) {
                        System.out.println("Internet restored! Reinitializing database connection...");

                        initializeConnection();

                        if (conn != null && !conn.isClosed()) {
                            System.out.println("Database connection re-established successfully.");

                            if (hasPendingData) {
                                processPendingData();
                            }
                        } else {
                            System.out.println("Failed to re-establish database connection.");
                        }
                    }

                    wasInternetAvailable = isConnected;

                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("Error in monitorInternet: " + e.getMessage());
                }
            }
        }).start();
    }

    public static void saveUpdateToFile(String tableName, String updateQuery, String params) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UPDATE_FILE, true))) {
            writer.write(tableName + "\n");
            writer.write(updateQuery + "\n");
            writer.write(params + "\n");
        } catch (IOException e) {
            System.out.println("Error saving update operation to file: " + e.getMessage());
        }
    }
}
