package com.college.models;

// Abstract class demonstrating ABSTRACTION and ENCAPSULATION
public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String dateOfBirth;

    public Person(String id, String name, String email, String phone,
                  String address, String gender, String dateOfBirth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    // Encapsulated getters
    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getPhone()       { return phone; }
    public String getAddress()     { return address; }
    public String getGender()      { return gender; }
    public String getDateOfBirth() { return dateOfBirth; }

    // Encapsulated setters (only mutable fields)
    public void setEmail(String email)     { this.email = email; }
    public void setPhone(String phone)     { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }

    // Abstract methods — subclasses MUST implement (Abstraction)
    public abstract String getRole();
    public abstract void displayInfo();

    @Override
    public String toString() {
        return String.format("ID: %-10s | Name: %-20s | Email: %-25s | Phone: %s",
                id, name, email, phone);
    }
}
