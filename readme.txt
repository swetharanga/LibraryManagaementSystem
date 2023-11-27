# Library System Project Group H

This README file provides instructions on how to compile, build, and install the Library System application.

## Description

The Library System project is a software system designed to manage the operations of a library. It provides functionalities for librarians and patrons to efficiently handle various tasks such as checking in/out books, check/pay fines, create new borrowers and searching for books.

## Requirements

- Java SE Development Kit (JDK) 8 or later
- Apache Ant (for building the project)
- MySQL Database
- Python (for running the setup script)
- Your preferred Integrated Development Environment (IDE), such as NetBeans, Eclipse, etc.

## Dependencies
- rs2xml.jar (used for net.proteanit.sql.DbUtils import in application)
- mysql-connector-j-8.2.0.jar

Ensure you download the `rs2xml.jar` file and `mysql-connector-java` library before building and running the project.

## Database Configuration 

**NOTE:** The Library System application is configured to connect to a MySQL database using the host address `localhost`. Please ensure that your MySQL Server is running on the same machine where the application is executed.

1. Install and start MySQL Server on your machine.

2. Ensure that the MySQL server is configured to accept connections with the following credentials:
   - Username: "root"
   - Password: "password123"

3. Create a new database named "library" and populate it with data from csv files by running the Python script as mentioned in the "Python Script for Database Setup" section below.

## Python Script Database Setup

1. Open your preferred command line interface. You can use PowerShell, Command Prompt, or any other terminal.

2. Ensure you have Python installed on your machine.

3. Make sure the CSV files (`books.csv` and `borrowers.csv`) are in the same directory as the Python script `DBInitializer.py`.

4. Navigate to the directory where DBInitializer.py, books.csv, and borrowers.csv are located and run the script by using the command `python DBInitializer.py`. (This should create the database "library" and populate it with books and borrowers data)
	cd path/to/your/project/scripts
	python DBInitializer.py

## Running the application

1. Extract java files and open your IDE and navigate to the main class of the project (MainPage.java).

2. Run the main class. This should launch the Java Swing GUI for the Library System application.

3. Interact with the GUI to perform various tasks such as checking in/out books, managing borrowers, checking/paying fines, and searching for books.

