package models;

// Another subclass of Person (Inheritance + Polymorphism)
public class Staff extends Person {

    public enum StaffRole { ADMIN, ACCOUNTS, COUNSELOR, PRINCIPAL }

    private StaffRole staffRole;
    private String    department;
    private double    salary;
    private String    joiningDate;

    public Staff(String id, String name, String email, String phone,
                 String address, int age, StaffRole staffRole,
                 String department, double salary, String joiningDate) {
        super(id, name, email, phone, address, age);
        this.staffRole   = staffRole;
        this.department  = department;
        this.salary      = salary;
        this.joiningDate = joiningDate;
    }

    @Override
    public String getRole() { return staffRole.toString(); }

    public StaffRole getStaffRole()   { return staffRole; }
    public String    getDepartment()  { return department; }
    public double    getSalary()      { return salary; }
    public String    getJoiningDate() { return joiningDate; }
}
