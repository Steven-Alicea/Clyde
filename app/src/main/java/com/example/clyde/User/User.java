package com.example.clyde.User;

public class User {

    private long id, latitude, longitude;
    private String firstName, lastName, middleName, phoneNumber, email, password;

    public User(long id, String lastName, String firstName, String middleName, String phoneNumber, String email, String password, long latitude, long longitude) {
        setId(id);
        setLastName(lastName);
        setFirstName(firstName);
        setMiddleName(middleName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setPassword(password);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getMiddleName() {

        return middleName;
    }

    public void setMiddleName(String middleName) {
        if (middleName.isEmpty())
            this.middleName = null;
        this.middleName = middleName;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty())
            this.phoneNumber = null;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public long getLatitude() {

        return latitude;
    }

    public void setLatitude(long latitude) {

        this.latitude = latitude;
    }

    public long getLongitude() {

        return longitude;
    }

    public void setLongitude(long longitude) {

        this.longitude = longitude;
    }
}