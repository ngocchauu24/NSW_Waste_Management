[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/9KSYlYIX)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=16625063&assignment_repo_type=AssignmentRepo)
# Studio Project Starter Code (Semester 2 - September 2024)
Starter code for the Studio Project. This is a simple layout connected to a GitHub Classroom that serves as the basis for implementing the studio project. 

This example program provides:

* A Java class for the Index page (index.html).
* 6x Java classes for 6 pages. Additional pages can be added by adding additional classes.
* JDBCConnection Java class, that uses the Waste Recycling Database. This class contains one method to return all Countries contained in the Database.
* Examples CSS (```common.css```) file in the resources directory.
* Example image (```logo.png```) file in the resources directory with where to locate any images you want on your website
* Starting database:
    * ```WasteRecycling.db``` - contains a starting database for you based on a subset of the foodloss ER Model.
* Optional helper program (``WasteRecyclingProcessCSV.java``) that shows an example of how to load the SQLite database by using Java to read the CSV files and JDBC insert statements to update the footloss SQLite database.


Classes backing Web pages:
```bash
├── PageIndex.java            - Homepage page for Level 1 Sub-task A
├── PageMission.java          - Mission Statement page for Level 1 Sub-task B
├── PageST2/3.java            - Sets of 4 Java files backing the 4 pages for 4 Level2/3 sub-tasks. Student in group of 3 will need to add additional Java files
```

Other Classes:
```bash
├── java/app                                - Package location for all Java files for the webserver
│         ├── App.java                      - Main Application entrypoint for Javalin
│         └── JDBCConnection.java           - Example JDBC Connection class based on Studio Project Workshop content
├── java/helper                             - Location of the helper file for loading SQLite with JDBC
│         └── WasteRecyclingProcessCSV.java           - Helper Java program to load SQLite database from the provided CSVs
```

Folders:
```bash
├── /src/main                    - Location of all files as required by build configuration
│         ├── java               - Java Source location
│         │    ├── app           - Package location for all Java files for the webserver
│         │    └── helper        - Location of the helper file for loading SQLite with JDBC
│         └── resources          - Web resources (html templates / style sheets)
│               ├── css          - CSS Style-sheets. Base example style sheet (common.css) provided
│               └── images       - Image files. Base example image (RMIT Logo) provided
│ 
├── /target                      - build directory (DO NOT MODIFY)
├── /database                    - The folder to store sqlite database files (*.db files) and data files (*.csv) related to the database
├── pom.xml                      - Configure Build (DO NOT MODIFY)
└── README.md                    - This file ;)
```

Current Libraries:
* org.xerial.sqlite-jdbc (SQLite JDBC library)
* javalin (lightweight Java Webserver)
* thymeleaf (HTML template) - https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

Libraries required as dependencies:
* By javalin
   * slf4j-simple (lightweight logging)
* By xerial/jdbc
   * sqlite-jdbc

# Building & Running the code
There are two places code can be run from
1. The **main** web server program
2. the **optional** helper program to use JDBC to load your SQLite database from the CSVs using Java

## Running the Main web server
You can run the main webserver program similar to the project workshop activities
1. Open this project within VSCode
2. Allow VSCode to read the pom.xml file
 - Allow the popups to run and "say yes" to VSCode configuring the build
 - Allow VSCode to download the required Java libraries
3. To Build & Run
 - Open the ``src/main/java/app/App.java`` source file, and select "Run" from the pop-up above the main function
4. Go to: http://localhost:7001

## Running the Helper Program
The helper program in ``src/main/java/helper/WasteRecyclingProcessCSV.java`` can be run separetly from the main webserver. This gives a demonstration of how you can use Java to read the provided CSV files and store the information in an SQLite database. This example loads a subset of the data in the ``database/NSW_WasteRecycling_2018-2019_LGA_Services.csv`` and ``database/NSW_WasteRecycling_2018-2019_Organics.csv`` files into a database.

You can run the optional helper program by
1. Open this ``src/main/java/helper/WasteRecyclingProcessCSV.java`` source file
2. Select "Run" from the pop-up above the main function (or "Run Java" from the top-right arrow button)
3. Allow the program to run
4. By default it will drop the existing tables and recreate them before populating each table
5. If you do not want to drop existing tables, comment out line line 52: dropTablesAndRecreateTables();

You can modify this file as you wish, for other tables and CSVs. When modifying you may need to pay attention to:
* ``DATABASE`` field to change the database location
* ``WR_2018_19_LGA_SERVICES_CSV_FILE`` and ``WR_2018_19_LGA_ORGANICS_CSV_FILE`` to change which CSV files are bring read in
* ``INSERT`` statement construction to:
    * Change the table being used
    * Column data being stored

Additionally you will want to modify and add additional field definitions in the files
* ``OrganicFields.java`` and ``ServicesFields.java`` to define the relative column number of each field

# Authors
* Dr. Halil Ali, School of Computing Technologies, STEM College, RMIT University.
* Dr. Timothy Wiley, School of Computing Technologies, STEM College, RMIT University.
* Prof. Santha Sumanasekara, School of Computing Technologies, STEM College, RMIT University.

Copyright RMIT University (c) 2024

