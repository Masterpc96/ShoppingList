package com.michaelsSoftware.ShoppingList.utils;

public class Email {
    // convert email on string without @
    public static String convertEmail(String email) {
        email = email.replace('.', '_');
        email += ".json";
        return email;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getNameFromEmail(String email) {
        int index = email.indexOf('@');
        return email.substring(0, index);
    }
}
