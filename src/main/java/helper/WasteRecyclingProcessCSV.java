package helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Stand-alone Java file for processing the database CSV files.
 * <p>
 * You can run this file using the "Run" or "Debug" options
 * from within VSCode. This won't conflict with the web server.
 * <p>
 * This program opens a CSV file from the WasteRecycling data set
 * and uses JDBC to load up data into the database.
 * <p>
 * To use this program you will need to change:
 * 1. The input file location
 * 2. The output file location
 * <p>
 * This assumes that the CSV files are in the **database** folder.
 * <p>
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */
public class WasteRecyclingProcessCSV {

   // MODIFY these to load/store to/from the correct locations
   private static final String DATABASE = "jdbc:sqlite:database/WasteRecycling.db";
   private static final String WR_2018_19_LGA_SERVICES_CSV_FILE = "database/NSW_WasteRecycling_2018-2019_LGA_Services.csv";
   private static final String WR_2018_19_LGA_ORGANICS_CSV_FILE = "database/NSW_WasteRecycling_2018-2019_Organics.csv";
   private static final int START_YEAR = 2013;
   private static final int END_YEAR = 2021;
   private static final int REGION_TYPE = 0;
   private static final int REGION_NAME = 1;
   

   public static void main (String[] args) {
      
      // Drops the date, country and class tables then recreates them
      // This only needs to be done once (unless your tables need to be updated and recreated)
      // Comment this out after runnning it the first time
      // ****WARNING**** 
      // ****WARNING**** do not run this again accidentily as it will remove data
      // ****WARNING**** 
      dropTablesAndRecreateTables();


      // Load up the Period table
      // This only needs to be done once
      // Comment this out after runnning it the first time
      loadPeriods();


      // Load up the RegionTypes table
      // This only needs to be done once
      // Comment this out after runnning it the first time
      loadRegionTypes();


      // loads the '2018-2019' LGA data into the LGA table
      // note it only loads the code, name, and region type
      // need to update this to handle other fields as necessary (based on your design)
      // Comment this out after runnning it the first time
       load2018_19LGAs();
       

      // Loads the '2018-2019' Organics data for each LGA into the LgaOrganicsStatistics table
      // note it only loads the code, year, and KerbsideOrganicsCollected statistics
      // need to update this to handle other fields as necessary (based on your design)
      // Comment this out after runnning it the first time
      load2018_19Organics();

      // Runs query to check all data joins correctly
      checkData();

      return;
   }

   // Drops and recreates empty lga, period, regionType and LgaOrganicsStatistics tables
   // Add additional create statements to create the rest of your tables
   public static void dropTablesAndRecreateTables() {
      // JDBC Database Object
      Connection connection = null;
      Scanner s = new Scanner(System.in);
      String response = null;

      System.out.println("\nWARNING: existing tables will be dropped and recreated\nAre you sure? (y/n)");
      response = s.nextLine();
      while(!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n"))
      {
         response = s.nextLine();
      }
      if(response.equalsIgnoreCase("n")){
         System.out.println("aborting");
         System.out.println("Comment out 'dropTablesAndRecreateTables();' to avoid deleting tables and run again");
         System.exit(0);
      }
      // Like JDBCConnection, we need some error handling.
      try {
         connection = DriverManager.getConnection(DATABASE);

         
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();

         // Create Insert Statement
         String query = null;
         query = "PRAGMA foreign_keys = OFF";
         System.out.println("Executing: \n" + query);
         statement.execute(query);
         query = "DROP TABLE IF EXISTS Lga";
         System.out.println("Executing: \n" + query);
         statement.execute(query);
         query = "DROP TABLE IF EXISTS Period";
         System.out.println("Executing: \n" + query);
         statement.execute(query);
         query = "DROP TABLE IF EXISTS RegionType";
         System.out.println("Executing: \n" + query);
         statement.execute(query);
         query = "DROP TABLE IF EXISTS LgaOrganicsStatistics";
         System.out.println("Executing: \n" + query);
         statement.execute(query);
         query = "PRAGMA foreign_keys = ON";
         System.out.println("Executing: \n" + query);
         statement.execute(query);

         query =  "CREATE TABLE Period (" + "\n" +
                  "      period                 TEXT NOT NULL," + "\n" +
                  "      startYear              INTEGER NOT NULL," + "\n" +
                  "      endYear                INTEGER NOT NULL," + "\n" +
                  "      PRIMARY KEY (period)" + "\n" +
                  ")";
         System.out.println("Executing: \n" + query);
         statement.execute(query);

         query =  "CREATE TABLE RegionType (" + "\n" +
         "      regionType              TEXT NOT NULL," + "\n" +
         "      name                    TEXT NOT NULL," + "\n" +
         "      PRIMARY KEY (regionType)" + "\n" +
         ")";
         System.out.println("Executing: \n" + query);
         statement.execute(query);

         query =  "CREATE TABLE Lga ( " + "\n" +
                  "     code               INTEGER NOT NULL," + "\n" +
                  "     name               TEXT NOT NULL," + "\n" +
                  "     regionType         TEXT NOT NULL," + "\n" +
                  "     PRIMARY KEY (code)" + "\n" +
                  "     FOREIGN KEY (regionType) REFERENCES REGIONTYPE(regionType)" + "\n" +
                  ")";
                      
         System.out.println("Executing: \n" + query);
         statement.execute(query);

         query =  "CREATE TABLE LgaOrganicsStatistics ( " + "\n" +
         "     code                               INTEGER NOT NULL," + "\n" +
         "     period                             TEXT NOT NULL," + "\n" +
         "     KerbsideOrganicsCollected          INTEGER NOT NULL," + "\n" +
         "     PRIMARY KEY (code, period)" + "\n" +
         "     FOREIGN KEY (code) REFERENCES Lgs(code)" + "\n" +
         "     FOREIGN KEY (period) REFERENCES Period(period)" + "\n" +
         ")";
             
         System.out.println("Executing: \n" + query);
         statement.execute(query);

         System.out.println("\ndropped and recreated tables\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void loadPeriods() {
      // JDBC Database Object
      Connection connection = null;

      // Like JDBCConnection, we need some error handling.
      try {
         connection = DriverManager.getConnection(DATABASE);

         for (int i = START_YEAR; i != END_YEAR; ++i) {
            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();

            // Create Insert Statement
            String query = "INSERT into period VALUES (\""
                           + i + "-" + (i+1) + "\", "
                           + i + ", " + (i+1)
                           + ")";

            // Execute the INSERT
            System.out.println("Executing: \n" + query);
            statement.execute(query);
         }
         System.out.println("\ninserted all periods\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }


   // Load up the RegionTypes table
   // This only needs to be done once
   // Comment this out after runnning it the first time
   public static void loadRegionTypes() {
      // JDBC Database Object
      Connection connection = null;
      PreparedStatement statement = null;
      String Regions[][] = {{"A", "Area"},{"C", "City"}};

      // Like JDBCConnection, we need some error handling.
      try {
         connection = DriverManager.getConnection(DATABASE);

         for (int i = 0; i != Regions.length; ++i) {

            // Prepare a new SQL Query & Set a timeout
            String myStatement = " INSERT INTO RegionType (regionType, name) VALUES (?, ?)";
            statement= connection.prepareStatement(myStatement );
            statement.setString(1, Regions[i][REGION_TYPE]);
            statement.setString(2, Regions[i][REGION_NAME]);
            System.out.println(statement.toString());
            statement.executeUpdate();
         }
         System.out.println("\ninserted all regiontypes\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // loads the '2018-2019' LGA data into the LGA table
   // note it only loads the code, name, and region type
   // need to update this to handle other fields as necessary (based on your design)
   // also need to load the 2019-2020 data as necessary
   public static void load2018_19LGAs() {
      // JDBC Database Object
      Connection connection = null;
      PreparedStatement statement = null;
      BufferedReader reader = null;
      String line;
      int year = 2018;

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         // CHANGE THIS to process a different file
         reader = new BufferedReader(new FileReader(WR_2018_19_LGA_SERVICES_CSV_FILE));

         // Read (and ignore) the first line of "headings"
         String header = reader.readLine();

         System.out.println("Heading row\n" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);

         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {
            //System.out.println(row);
            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get some of the columns in order (and remove double quotes)
            // Note you will need to do the same in the other csv files to match
            // Add additional code to retrieve other columns as necessary
            String lgaCode = (splitline[ServicesFields.LGA_CODE]).replaceAll("^\"|\"$", "");
            String lgaName = (splitline[ServicesFields.LGA_NAME]).replaceAll("^\"|\"$", "");
            String regionType = (splitline[ServicesFields.REGION_TYPE]).replaceAll("^\"|\"$", "");


            // Create Insert Statement
            String myStatement = " INSERT INTO lga (code, name, regionType) VALUES (?, ?, ?)";
            statement= connection.prepareStatement(myStatement );
            statement.setString(1, lgaCode);
            statement.setString(2, lgaName);
            statement.setString(3, regionType);
            System.out.println(statement.toString());
            statement.executeUpdate();
            
         }
         System.out.println("\ninserted all " + year + " lga data\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
      finally {
         if(reader!=null) {
            try{
            reader.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   // Loads the '2018-2019' Organics data for each LGA into the LgaOrganicsStatistics table
   // note it only loads the code, year, and KerbsideOrganicsCollected statistics
   // need to update this to handle other fields as necessary (based on your design)
   // need to also load the 2019-2020 data
   // Comment this out after runnning it the first time
   public static void load2018_19Organics() {
      // JDBC Database Object
      Connection connection = null;
      PreparedStatement statement = null;
      BufferedReader reader = null;
      String line;
      String period = "2018-2019";

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         // copy and CHANGE THIS to process a different file
         reader = new BufferedReader(new FileReader(WR_2018_19_LGA_ORGANICS_CSV_FILE));

         // Read (and ignore) the first 3 lines of "headings"
         String header = reader.readLine();
         header = reader.readLine();
         header = reader.readLine();

         System.out.println("Heading row\n" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);

         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get relevant columns in order (and remove double quotes)
            // Note you will need to do the same in the other csv files to match
            String lgaCode = (splitline[OrganicsFields.LGACODE]).replaceAll("^\"|\"$", "");
            // remove double quotes, commas and hyphens within the statistics data (otherwise it will be text not int)
            String KerbsideOrganicsCollected = (splitline[OrganicsFields.KERBSIDE_ORGANICS_COLLECTED]).replaceAll("^\"| |,|-|\"$", "");
            //String KerbsideOrganicsRecycled  = (splitline[OrganicsFields.KERBSIDE_ORGANICS_RECYCLED]).replaceAll("^\"| |,|-|\"$", "");
            //String KerbsideOrganicsDisposed  = (splitline[OrganicsFields.KERBSIDE_ORGANICS_DISPOSED]).replaceAll("^\"| |,|-|\"$", "");

            // Create Insert Statement
            String myStatement = " INSERT INTO LgaOrganicsStatistics (code, period, KerbsideOrganicsCollected) VALUES (?, ?, ?)";
            statement= connection.prepareStatement(myStatement );
            statement.setString(1, lgaCode);
            statement.setString(2, period);
            statement.setString(3, KerbsideOrganicsCollected);
            System.out.println(statement.toString());
            statement.executeUpdate();
         }
         System.out.println("\ninserted all " + period + " LGA Kerbside Collected Organics data\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
      finally {
         if(reader!=null) {
            try{
            reader.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   public static void checkData() {
      // JDBC Database Object
      Connection connection = null;

      // Like JDBCConnection, we need some error handling.
      try {
         connection = DriverManager.getConnection(DATABASE);

         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();

         // Create query Statement
         String query = "SELECT l.code AS \"lga_code\", l.name AS \"lga_name\", period, r.name AS \"regiontype_name\", kerbsideOrganicsCollected\n"
                       + "FROM LgaOrganicsStatistics ls\n" 
                       + "     JOIN lga l ON ls.code = l.code\n"
                       + "          JOIN regiontype r ON r.regionType=l.regiontype";
 
            // Execute the INSERT
         System.out.println("Executing: \n" + query);
            // Get Result
         ResultSet results = statement.executeQuery(query);

         // Process all of the results
         while (results.next()) {
            int lgaCode = results.getInt("lga_code");
            String lgaName  = results.getString("lga_name");
            String period = results.getString("period");
            String regionTypeName = results.getString("regiontype_name");
            String kerbsideOrganicsCollected = results.getString("kerbsideOrganicsCollected");

            System.out.println(lgaCode + " " + lgaName + " " + period + " " + regionTypeName + " " + kerbsideOrganicsCollected);
         }
         System.out.println("\nSuccessfully ran query\n" + query + "\npress enter to continue");
         System.in.read();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}