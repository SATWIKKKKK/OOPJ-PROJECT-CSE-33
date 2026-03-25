package models;

// Abstract class demonstrating Abstraction and Encapsulation
public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;

    public Person(String id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getPassword() { return password; }

    public void setName(String name)    { this.name = name; }
    public void setEmail(String email)  { this.email = email; }
    public void setPhone(String phone)  { this.phone = phone; }
    public void setPassword(String pwd) { this.password = pwd; }

    public abstract String getRole();
    public abstract void displayInfo();

    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Email: %s | Phone: %s", id, name, email, phone);
    }
}
