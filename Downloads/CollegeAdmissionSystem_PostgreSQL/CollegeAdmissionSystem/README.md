# 🎓 College Admission and Fee Management System
**Java OOP + PostgreSQL | 4th Semester Project**

---

## 📁 Project Structure

```
CollegeAdmissionSystem/
├── src/com/college/
│   ├── Main.java                        ← Entry point + full menu UI
│   ├── database/
│   │   └── DBConnection.java            ← Singleton PostgreSQL connection
│   ├── models/
│   │   ├── Person.java                  ← Abstract base class
│   │   ├── Student.java                 ← extends Person (Inheritance)
│   │   ├── Applicant.java               ← extends Person (Inheritance)
│   │   ├── Course.java
│   │   ├── FeeStructure.java
│   │   └── Payment.java
│   ├── interfaces/
│   │   ├── Manageable.java              ← Generic CRUD interface
│   │   └── Payable.java                 ← Fee payment interface
│   ├── exceptions/
│   │   ├── AdmissionException.java
│   │   └── PaymentException.java
│   └── services/
│       ├── AdmissionService.java        ← JDBC: applicants, students, courses
│       ├── FeeService.java              ← JDBC: fee structures, payments
│       └── ReportService.java           ← SQL aggregate reports
│
├── lib/                                 ← ⚠️ Place JDBC .jar here
├── out/                                 ← Compiled .class files (auto-created)
├── schema.sql                           ← Run this FIRST in PostgreSQL
├── run.bat                              ← Windows compile + run
├── run.sh                               ← Linux/Mac compile + run
└── README.md
```

---

## ⚙️ Setup Instructions

### Step 1 — Install PostgreSQL
Download from https://www.postgresql.org/download/ and install.
Default port: `5432`, default superuser: `postgres`.

### Step 2 — Create the Database
Open **pgAdmin** or **psql** and run:
```sql
CREATE DATABASE college_db;
```

### Step 3 — Run the Schema
In pgAdmin: open Query Tool → paste contents of `schema.sql` → Execute.

Or via terminal:
```bash
psql -U postgres -d college_db -f schema.sql
```
This creates all 5 tables and seeds 8 courses + fee structures automatically.

### Step 4 — Download PostgreSQL JDBC Driver
Go to: https://jdbc.postgresql.org/download/
Download the latest `postgresql-42.x.x.jar`
Place it inside the `lib/` folder.

### Step 5 — Configure DB Credentials
Open `src/com/college/database/DBConnection.java` and update:
```java
private static final String DATABASE = "college_db";  // your DB name
private static final String USERNAME = "postgres";     // your pg username
private static final String PASSWORD = "admin123";     // your pg password
private static final String HOST     = "localhost";    // usually localhost
private static final String PORT     = "5432";         // default pg port
```

---

## ▶️ How to Run

### VS Code
1. Install **Extension Pack for Java** by Microsoft
2. Open the `CollegeAdmissionSystem` folder
3. Go to `.vscode/settings.json` — ensure `"java.project.referencedLibraries"` points to your jar:
   ```json
   { "java.project.referencedLibraries": ["lib/postgresql-*.jar"] }
   ```
4. Open `Main.java` → click **▶ Run**

### Terminal (Windows)
```cmd
run.bat
```

### Terminal (Linux / Mac)
```bash
chmod +x run.sh
./run.sh
```

### Manual Compile + Run
```bash
# Compile
javac -cp "lib/postgresql-42.x.x.jar" -d out \
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

---

## 🗄️ Database Tables

| Table | Description |
|---|---|
| `courses` | All available courses with seat counts |
| `fee_structures` | Per-course fee breakdown |
| `applicants` | Admission applications |
| `students` | Enrolled students (created on approval) |
| `payments` | All fee transactions |

**Key design choices:**
- Approving an application is an **SQL Transaction** — updates applicant status, decrements seats, inserts student atomically. If any step fails, the whole thing rolls back.
- Counters are seeded from `MAX(id)` on startup so restarting the app never duplicates IDs.
- All reports use **SQL GROUP BY / SUM / COUNT** directly — no Java-side aggregation.

---

## 🧠 OOP Concepts Map

| Concept | Where |
|---|---|
| **Abstraction** | `Person` (abstract class), `Manageable<T>`, `Payable` interfaces |
| **Encapsulation** | All model fields private + getters/setters |
| **Inheritance** | `Student extends Person`, `Applicant extends Person` |
| **Polymorphism** | `displayInfo()` and `getRole()` behave differently per subclass |
| **Interface** | `Manageable<T>` (generic), `Payable` — both implemented by services |
| **Custom Exceptions** | `AdmissionException`, `PaymentException` with error codes |
| **Singleton Pattern** | `DBConnection` — one shared DB connection |
| **Generics** | `Manageable<T>` is fully generic |
| **SQL Transactions** | `approveApplication()` uses `commit/rollback` |
| **PreparedStatement** | All queries use parameterized statements (SQL injection safe) |

---

## 🧪 Test Flow

1. Run app → Student Portal → Apply (Course: `CS101`, Score: `75`) → note **APP ID**
2. Admin Login (`admin` / `admin123`) → Approve Application → note **Student ID**
3. Student Portal → Pay Fees → enter Student ID → pick TUITION + ONLINE → enter `45000`
4. Admin → Reports → Admission Report / Fee Collection Report

---

*Project for B.Tech / BCA 4th Semester — Java OOP Subject*
