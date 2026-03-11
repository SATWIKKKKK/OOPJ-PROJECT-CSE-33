# 🎓 College Admission and Fee Management System

**Java OOP + PostgreSQL | CLI-Based | 4th Semester OOPJ Project**

A fully-featured **command-line College Admission and Fee Management System** built with **Java** and **PostgreSQL**. Demonstrates core OOP principles including Abstraction, Encapsulation, Inheritance, Polymorphism, Interfaces, Generics, Custom Exceptions, Singleton Pattern, and SQL Transactions.

---

## ✨ Features

### Core Features
- **Dual Portal System** — Separate Admin and Student/Applicant portals
- **Admission Workflow** — Apply → Review → Approve/Reject → Auto-enroll (atomic SQL transaction)
- **Fee Management** — Multiple payment types (Tuition, Hostel, Transport, Exam, Admission) and methods (Cash, Online, Cheque, DD)
- **Reporting** — Admission reports, fee collection reports, student-wise breakdowns, department-wise stats
- **Secure Credentials** — All DB and admin credentials stored in `.env` file (not hardcoded)

### Advanced Features
- 🔍 **Search Courses** — Search by keyword across name, department, type, or ID
- 📝 **Update Student Records** — Modify semester, CGPA, or status (Active/Inactive/Graduated)
- 🗑️ **Delete Courses** — Safe deletion with FK constraint checks
- 💰 **Update Fee Structures** — Modify any fee component for a course
- 🎓 **Scholarship Calculator** — Auto-calculates tuition waiver based on score slabs (50%/30%/15%/10%)
- 📊 **Export to CSV** — Export students, applications, payments, or courses to CSV files
- 🔐 **Change Admin Password** — Runtime password change with `.env` auto-update
- 🧮 **Fee Calculator** — Estimate total fees for any number of semesters with optional hostel/transport

---

## 📁 Project Structure

```
CollegeAdmissionSystem/
├── .env                                 ← DB + Admin credentials (DO NOT COMMIT)
├── .gitignore                           ← Ignores .env, out/, .class files
├── schema.sql                           ← PostgreSQL schema (run FIRST)
├── run.bat                              ← Windows: compile + run
├── run.sh                               ← Linux/Mac: compile + run
├── README.md
│
├── lib/
│   └── postgresql-42.x.x.jar           ← PostgreSQL JDBC driver (required)
│
├── data/                                ← CSV exports go here
│
├── sql/
│   └── schema.sql                       ← MySQL version (alternate)
│
└── src/com/college/
    ├── Main.java                        ← Entry point + full CLI menu system
    │
    ├── utils/
    │   └── EnvConfig.java               ← Reads credentials from .env file
    │
    ├── database/
    │   └── DBConnection.java            ← Singleton PostgreSQL connection (via .env)
    │
    ├── models/
    │   ├── Person.java                  ← Abstract base class (Abstraction)
    │   ├── Student.java                 ← extends Person (Inheritance + Polymorphism)
    │   ├── Applicant.java               ← extends Person (Inheritance + Polymorphism)
    │   ├── Course.java                  ← Course model (Encapsulation)
    │   ├── FeeStructure.java            ← Fee breakdown per course
    │   └── Payment.java                 ← Payment transaction record
    │
    ├── interfaces/
    │   ├── Manageable.java              ← Generic CRUD interface (Generics)
    │   └── Payable.java                 ← Fee payment contract
    │
    ├── exceptions/
    │   ├── AdmissionException.java      ← Custom exception with error codes
    │   └── PaymentException.java        ← Custom exception with error codes + amount
    │
    └── services/
        ├── AdmissionService.java        ← Admission operations + CRUD (implements Manageable<Applicant>)
        ├── FeeService.java              ← Payment processing (implements Manageable<Payment> + Payable)
        └── ReportService.java           ← SQL aggregate reports
```

---

## ⚙️ Setup Instructions

### Prerequisites
- **Java JDK 17+** (uses switch expressions)
- **PostgreSQL 12+** installed and running

### Step 1 — Create the Database
```sql
CREATE DATABASE college_db;
```

### Step 2 — Run the Schema
```bash
psql -U postgres -d college_db -f schema.sql
```
Or paste `schema.sql` contents into pgAdmin Query Tool and execute.

### Step 3 — Download JDBC Driver
Download `postgresql-42.x.x.jar` from [jdbc.postgresql.org](https://jdbc.postgresql.org/download/) and place it in the `lib/` folder.


> **🔐 How to change admin credentials:**
> - **Option 1:** Edit the `.env` file directly — change `ADMIN_USERNAME` and `ADMIN_PASSWORD`
> - **Option 2:** At runtime — Login as Admin → Option 20 (Change Admin Password)
> - The runtime change automatically updates the `.env` file

---

## ▶️ How to Run

### Option A — Windows (Double-click or Terminal)
```cmd
run.bat
```

### Option B — Linux / Mac
```bash
chmod +x run.sh
./run.sh
```

### Option C — Manual Compile + Run
```bash
# Compile
javac -cp "lib/postgresql-42.x.x.jar" -d out \
  src/com/college/utils/*.java \
  src/com/college/models/*.java \
  src/com/college/interfaces/*.java \
  src/com/college/exceptions/*.java \
  src/com/college/database/*.java \
  src/com/college/services/*.java \
  src/com/college/Main.java

# Run (Linux/Mac)
java -cp "out:lib/postgresql-42.x.x.jar" com.college.Main

# Run (Windows)
java -cp "out;lib/postgresql-42.x.x.jar" com.college.Main
```

### Option D — VS Code
1. Install **Extension Pack for Java**
2. Open the `CollegeAdmissionSystem` folder
3. Open `Main.java` → click **▶ Run**

---

## 🗄️ Database Tables

| Table | Purpose |
|---|---|
| `courses` | Course catalog with seat tracking |
| `fee_structures` | Per-course fee breakdown (7 fee components) |
| `applicants` | Admission applications with status (Pending/Approved/Rejected) |
| `students` | Enrolled students (auto-created when application is approved) |
| `payments` | Fee payment transactions with receipt generation |

---

## 🖥️ Menu Structure

```
MAIN MENU
├── 1. Admin Login
│   ├── 1–8.   Admission Management (View/Add courses, applications, students)
│   ├── 9–11.  Fee Management (View fee structures, payments)
│   ├── 12–13. Reports (Admission Report, Fee Collection Report)
│   └── 14–20. Advanced Features
│       ├── 14. Search Courses by Keyword
│       ├── 15. Update Student (Semester/CGPA/Status)
│       ├── 16. Delete a Course
│       ├── 17. Update Fee Structure
│       ├── 18. Scholarship Calculator
│       ├── 19. Export Data to CSV
│       └── 20. Change Admin Password
│
├── 2. Student / Applicant Portal
│   ├── 1–7.   Core (Apply, Check Status, Pay Fees, View Profile)
│   ├── 8.     Search Courses
│   ├── 9.     Fee Calculator
│   └── 10.    Check Scholarship Eligibility
│
└── 3. Exit
```

---

## 🧠 OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Abstraction** | `Person` (abstract class with abstract methods `getRole()`, `displayInfo()`) |
| **Encapsulation** | All model fields `private` — accessed only via getters/setters |
| **Inheritance** | `Student extends Person`, `Applicant extends Person` |
| **Polymorphism** | `displayInfo()` and `getRole()` behave differently per subclass |
| **Interfaces** | `Manageable<T>` (generic CRUD), `Payable` (payment contract) |
| **Generics** | `Manageable<T>` — parameterized with `Applicant` and `Payment` |
| **Custom Exceptions** | `AdmissionException` (with error codes), `PaymentException` (with error codes + amount) |
| **Singleton Pattern** | `DBConnection` — single shared DB connection instance |
| **SQL Transactions** | `approveApplication()` uses `commit/rollback` for atomicity |
| **PreparedStatement** | All queries use parameterized statements (SQL injection safe) |

---

## 🎓 Scholarship Slabs

| Previous Score | Scholarship Tier | Tuition Waiver |
|---|---|---|
| 95%+ | GOLD | 50% |
| 90–94% | SILVER | 30% |
| 85–89% | BRONZE | 15% |
| 80–84% | MERIT | 10% |
| Below 80% | — | 0% |

---

## 🧪 Quick Test Flow

1. **Run the app** → Choose **Student Portal**
2. **Apply for Admission** → Enter details, pick a course (e.g. `CS101`), enter score (e.g. `88`) → Note the **Application ID**
3. Go back → **Admin Login** (`admin` / `admin123`)
4. **View Pending Applications** → **Approve** using the Application ID → Note the **Student ID**
5. Logout → **Student Portal** → **Pay Fees** → Enter Student ID → Pick TUITION + ONLINE → Enter amount
6. Go to **Admin** → **Reports** → View Admission Report / Fee Collection Report
7. Try **Export to CSV** → Check the `data/` folder for the exported file

---


## 👥 Team — CSE-33

| Name | Roll |
|---|---|
| Satwik Chandra | - |

---

*Built for B.Tech 4th Semester — Object-Oriented Programming with Java (OOPJ)*
