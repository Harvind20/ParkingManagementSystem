# ParkingManagementSystem
This is a university project for CCP6224 – Object-Oriented Analysis and Design 

A modular, Java-based application designed to manage vehicle entry, exit, fee calculation, and fine enforcement for a multi-floor parking facility. 
The system uses **Java Swing** for the GUI and **SQLite** for persistent data storage.

### Architecture & Design Patterns
* **MVC-inspired Architecture:** Separation of Logic (Modules), Data (DAOs), and UI.
* **Singleton Pattern:** Used for the `ParkingLot` controller.
* **Strategy Pattern:** Used in the `FineModule` to switch between `Fixed`, `Hourly`, and `Progressive` fine calculation strategies.
* **DAO Pattern:** Data Access Objects handle all SQL interactions.

---

## Project Structure

```text
ParkingSystem/
├── lib/
│   └── sqlite-jdbc-3.51.2.0.jar   # Database Driver
├── src/
│   ├── coreParkingSystem/  # Database Connection, DAOs, Entity Models
│   ├── EntryModule/        # Ticket generation, Entry validation
│   ├── ExitModule/         # Fee calculation, Receipt generation
│   ├── FineModule/         # Fine strategies (Fixed, Hourly, Progressive)
│   └──UserInterface/      # Swing GUIs (Panels, Frames, Tables)
│   
├── parking_FINAL.db        # SQLite Database (Auto-generated)
├── compile.cmd                 # Compile and Run script
└── README.md

```

---

## Getting Started

### Prerequisites

* **Java Development Kit (JDK):** Version 8 or higher.
* **SQLite JDBC Driver:** Included in the `lib/` folder.

### Installation

1. Ensure your folder structure matches the **Project Structure** above.
2. Place the `sqlite-jdbc-3.51.2.0.jar` inside the `lib` folder.

### Compiling & Running

A `compile.cmd` script is provided to automate the build process.
The script is configured to **RESET** the database (`del parking_FINAL.db`) on every launch for testing purposes.

1. Run `compile.cmd` via command line:

If you wish to run it manually without the script:

```bash
# Compile
javac -cp "lib/sqlite-jdbc-3.51.2.0.jar" src/EntryModule/*.java src/coreParkingSystem/*.java src/UserInterface/*.java src/ExitModule/*.java src/FineModule/*.java
# Run
java -cp "src;lib/sqlite-jdbc-3.51.2.0.jar" UserInterface.MainMenuUI

```

---

## How It Works

### 1. Fine Logic

Fines are calculated based on the **Fine Scheme** active when the ticket was generated.

* **Violation Fines:** If a user parks in a Reserved/Handicap spot without authorization, the system calculates the fine for the 1st hour immediately and saves it to the DB.
* **Hourly Updates:** A background task runs every hour to increment fines for overstaying vehicles or continuing violations.
* **Payment:** All accumulated fines must be cleared at the exit along with the parking fee.

### 2. Database

The system uses `parking_FINAL.db`. The tables are initialized automatically if they do not exist:

* `parking_spots`: Tracks status and current vehicle.
* `vehicles`: Stores accumulated fines and VIP status.
* `tickets`: Active parking sessions (stores the fine scheme used).
* `fines`: Tracks individual fine records and payment status.
* `receipts`: Historical log of all completed exits.
* `admin_settings`: Sets the fine scheme used to be applied on future entries.

## Author
* **Harvind a/l Sethu Pathy 243UC247DM**
* **Sanjeevan a/l Rames 243UC245LQ**
* **Ajjay Naidu a/l Naidu 243UC247DQ**
* **Isaiah Naden a/l Felix Arokianathan 243UC2466L**
* **Thassveen a/l Vijayabaskar 243UC247DT**