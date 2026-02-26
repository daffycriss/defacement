package defacement.service;

public class PasswordValidator {

    /**
     * Checks password complexity:
     * - Minimum 8 characters
     * - At least 1 uppercase
     * - At least 1 lowercase
     * - At least 1 digit
     * - At least 1 special character
     */
    public static boolean isValid(String password) {
        if (password == null) return false;

        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;      // Uppercase
        if (!password.matches(".*[a-z].*")) return false;      // Lowercase
        if (!password.matches(".*\\d.*")) return false;        // Digit
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) return false; // Special char

        return true;
    }
}
