package br.com.examplefatec.service;

import java.util.Locale;
import java.util.regex.Pattern;

final class ValidationSupport {

    private static final Pattern LETTERS_AND_SPACES = Pattern.compile("^[\\p{L} ]+$");
    private static final Pattern ALPHANUMERIC = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private ValidationSupport() {
    }

    static String trim(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    static String lowerEmail(String value) {
        String trimmed = trim(value);
        return trimmed == null ? null : trimmed.toLowerCase(Locale.ROOT);
    }

    static String upperAlphanumeric(String value) {
        String trimmed = trim(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    static String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    static void maxLength(String value, int max, String message) {
        if (value != null && value.length() > max) {
            throw new IllegalArgumentException(message);
        }
    }

    static void lettersAndSpaces(String value, String message) {
        if (value != null && !value.isBlank() && !LETTERS_AND_SPACES.matcher(value).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    static void alphanumeric(String value, String message) {
        if (value != null && !value.isBlank() && !ALPHANUMERIC.matcher(value).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    static void email(String value, String message) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    static void exactDigits(String value, int length, String message) {
        if (value == null || value.length() != length) {
            throw new IllegalArgumentException(message);
        }
    }

    static void phoneDigits(String value, String message) {
        if (value == null || (value.length() != 10 && value.length() != 11)) {
            throw new IllegalArgumentException(message);
        }
    }

    static void positive(int value, String message) {
        if (value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
