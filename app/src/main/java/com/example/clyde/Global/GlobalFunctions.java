package com.example.clyde.Global;

public class GlobalFunctions {

    public static boolean verifyEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean verifyPassword (String password) {
        String regex = "^(?=.*\\d).{6,14}$";
        return password.matches(regex);
    }

    public static boolean verifyName (String name) {
        String regex = "^[a-zA-Z]+(([\\'\\,\\.\\-][a-zA-Z])?[a-zA-Z]*)*$";
        return name.matches(regex);
    }

    public static boolean verifyPhoneNumber(String phoneNumber) {
        String regex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
        return phoneNumber.matches(regex);
    }
}
