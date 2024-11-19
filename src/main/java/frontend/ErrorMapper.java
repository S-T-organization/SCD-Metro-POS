package frontend;

import java.util.HashMap;

public class ErrorMapper {

    // HashMap to store error codes and their corresponding messages
    private static final HashMap<Integer, String> errorMessages = new HashMap<>();

    static {
        errorMessages.put(-1, "Net not available.");
        errorMessages.put(1, "Success.");
        errorMessages.put(0, "Operation Failed! Please try again.");
        errorMessages.put(2, "Branch does not exist.");
        errorMessages.put(3, "Duplicate Email detected.");
        errorMessages.put(4, "Old password is incorrect.");
        errorMessages.put(5, "Employee not found.");
        errorMessages.put(6, "Invalid login credentials.");
    }

    public static String getErrorMessage(int code)
    {
        return errorMessages.getOrDefault(code, "Unknown error code.");
    }

}
