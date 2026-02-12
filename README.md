# ParkingManagementSystem
This is a university project for CCP6224 – Object-Oriented Analysis and Design 

===============================================================
       PARKING LOT MANAGEMENT SYSTEM - GROUP WORK DIVISION
===============================================================

STATUS: CORE BACKEND IS COMPLETE AND INTEGRATED.

---------------------------------------------------------------
FOLDER STRUCTURE (DO NOT CHANGE THIS!)
---------------------------------------------------------------
Your project source folder (src) must look EXACTLY like this:

src/
 ├── coreParkingSystem/       <-- Sanjeevan's Folder
 │    ├── ParkingLot.java     (The Database & Singleton)
 │    ├── ParkingSpot.java
 │    ├── Floor.java
 │    └── Row.java
 │
 ├── EntrySystem/             <-- Harvind's Folder
 │    ├── EntryController.java
 │    ├── Ticket.java         (Shared Ticket Object)
 │    ├── Vehicle.java
 │    └── ... (Car, SUV, etc.)
 │
 ├── feeCalculator/           <-- Isaiah's Folder
 │    ├── ExitSystem.java     (The Facade)
 │    ├── FeeCalculator.java
 │    └── Receipt.java
 │
 └── fineManagement/          <-- Thassveen's Folder
      ├── FineManager.java
      └── ... (FineScheme, FixedFine, etc.)

---------------------------------------------------------------
HOW TO CONNECT THE GUI (FOR AJJAY)
---------------------------------------------------------------

1. ENTRY BUTTON (Park Vehicle)
   ---------------------------
   Inside your "Park" button action listener:
   
   // A. Create the Controller
   EntrySystem.EntryController entry = new EntrySystem.EntryController();
   
   // B. Create the Vehicle based on Dropdown
   EntrySystem.Vehicle v = new EntrySystem.Car(plateNumberTextField.getText());
   // (Use if-else to switch between Car/SUV/Motorcycle classes)
   
   // C. Attempt to Park
   String result = entry.attemptPark(v, "1-1-1"); // Pass the Spot ID selected
   
   // D. Show Result
   JOptionPane.showMessageDialog(this, result);


2. EXIT BUTTON (Pay & Leave)
   -------------------------
   Inside your "Pay" button action listener:
   
   // A. Create the System
   feeCalculator.ExitSystem exit = new feeCalculator.ExitSystem();
   
   // B. Process the Exit
   feeCalculator.Receipt receipt = exit.processExit(plateNumberTextField.getText(), amountPaid);
   
   // C. Show Receipt
   if (receipt != null) {
       textArea.setText(receipt.toString());
   } else {
       JOptionPane.showMessageDialog(this, "Ticket not found!");
   }


3. ADMIN PANEL (View Reports)
   --------------------------
   To show how many cars are parked:
   
   // A. Get the Database
   coreParkingSystem.ParkingLot db = coreParkingSystem.ParkingLot.getInstance();
   
   // B. Loop through floors (Ask Sanjeevan for helper method if needed)
   // For now, you can just display static text or ask Sanjeevan to add a 
   // "getOccupancyRate()" method to ParkingLot.java.

---------------------------------------------------------------
HOW TO RUN THE INTEGRATION TEST
---------------------------------------------------------------
If you want to check if the backend is broken:
1. Open Terminal/CMD in the 'src' folder.
2. Type: java EntrySystem.IntegrationTest
3. If it prints a Receipt, the system is working.