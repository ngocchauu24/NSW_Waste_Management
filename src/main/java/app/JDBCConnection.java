package app;

import java.util.ArrayList;
import java.lang.Math;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JDBCConnection {

    public static final String DATABASE = "jdbc:sqlite:database/WasteRecycling.db";

    
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    
    
    public ArrayList<LGA> getAllLGAs(String year) {
        ArrayList<LGA> lgas = new ArrayList<LGA>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM lga l JOIN LGAStatistics s ON s.LGAcode = l.code WHERE period IS '" + year + "' ORDER BY name";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                int code     = results.getInt("code");
                String name  = results.getString("name");

                LGA lga = new LGA(code, name);

                lgas.add(lga);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lgas;
    }

    public ArrayList<LGA> getLGAdata1(ArrayList<String> lgaName, String year, ArrayList<String> subtype, String sort, boolean desc) {
        ArrayList<LGA> lgas = new ArrayList<LGA>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT l.name AS LGA, s.population AS Population, s.householdSurveyed AS 'Houses Surveyed'," + 
            " SUM(w.collected) AS 'Total Waste Collected', SUM(w.recycled) AS 'Total Waste Recycled'," + 
            " CAST(SUM(w.recycled) AS REAL) / SUM(w.collected) AS 'Average Percentage Recycled'," + 
            " CAST(SUM(w.collected) AS REAL) / s.householdSurveyed AS 'Average Waste per Household' FROM lga l JOIN LGAStatistics s ON l.code = s.LGAcode" + 
            " JOIN LGAWasteStatistic w ON w.LGAcode = l.code AND w.period = s.period" + 
            " WHERE (" + queryList("l.name", lgaName) + ") AND s.period = '" + year + "' AND (" + queryList("w.name", subtype) + ")" +
            " GROUP BY LGA ORDER BY \"" + sort + (desc ? "\" DESC": "\" ASC");
            
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String name  = results.getString("LGA");
                int population = results.getInt("Population");
                int householdSurveyed = results.getInt("Houses Surveyed");
                int collected = results.getInt("Total Waste Collected");
                int recycled = results.getInt("Total Waste Recycled");
                double rate = results.getDouble("Average Percentage Recycled");
                double average = results.getDouble("Average Waste per Household");

                LGA lga = new LGA(name, population, householdSurveyed, collected, recycled, rate, average);

                lgas.add(lga);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lgas;
    }

    public ArrayList<LGA> getLGAdata2(String lgaName, String year, String type, ArrayList<String> subtypes, String qty, String sort, boolean desc) {
        ArrayList<LGA> lgas = new ArrayList<LGA>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT lc.name AS LGA";
            for(int i = 1; i <= subtypes.size(); i++)
            {
                query += ", CAST(s" + i + ".recycled AS REAL) / s" + i + ".collected AS " + subtypes.get(i - 1).replace(" ", "_") + "";
            }
            query += ", dif.Difference FROM LGA lc JOIN (SELECT lb.name AS name, CAST(SUM(sb.recycled) AS REAL) / SUM(sb.collected) - " +
            "((SELECT CAST(SUM(sa.recycled) AS REAL) / SUM(sa.collected) AS 'Percentage Recycled' FROM LGAWasteStatistic sa JOIN LGA la ON la.code = sa.LGAcode " +
            "WHERE (la.name = '" + lgaName + "') AND (sa.WTName = '" + type + "') AND (sa.period = '" + year + "'))) AS Difference FROM LGAWasteStatistic sb " +
            "JOIN LGA lb ON lb.code = sb.LGAcode WHERE (sb.WTName = '" + type + "') AND (sb.period = '" + year + "') AND (lb.name IS NOT '" + lgaName + "') GROUP BY lb.name " +
            "HAVING Difference IS NOT null ORDER BY ABS(Difference) ASC LIMIT " + qty + ") AS dif ON dif.name = lc.name ";
            for(int i = 1; i <= subtypes.size(); i++)
            {
                query += "JOIN LGAWasteStatistic s" + i + " ON s" + i + ".LGAcode = lc.code AND s" + i + ".name = \"" + subtypes.get(i - 1) + "\" AND s" + i + ".period = \"" + year + "\" ";
            }
            query += "GROUP BY lc.name ORDER BY ABS(\"" + sort.replace(" ", "_") + "\") " + (desc ? "DESC": "ASC");
        
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String name  = results.getString("LGA");

                ArrayList<Double> percentages = new ArrayList<>();
                for(String i : subtypes)
                {
                    percentages.add(results.getDouble(i.replace(" ", "_")));
                }

                double difference = results.getDouble("Difference");
                

                LGA lga = new LGA(name, percentages, difference);

                lgas.add(lga);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lgas;
    }

    public LGA getLGAcomparedata2(String lgaName, String year, String type, ArrayList<String> subtypes) {
        
        LGA lga = new LGA(lgaName);

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT la.name AS LGA";
            for(int i = 1; i <= subtypes.size(); i++)
            {
                query += ", CAST(s" + i + ".recycled AS REAL) / s" + i + ".collected AS " + subtypes.get(i - 1).replace(" ", "_") + "";
            }
            query += ", CAST(SUM(sa.recycled) AS REAL) / SUM(sa.collected) AS 'Percentage Recycled' FROM LGAWasteStatistic sa JOIN LGA la ON la.code = sa.LGAcode ";
            for(int i = 1; i <= subtypes.size(); i++)
            {
                query += "JOIN LGAWasteStatistic s" + i + " ON s" + i + ".LGAcode = la.code AND s" + i + ".name = \"" + subtypes.get(i - 1) + "\" AND s" + i + ".period = \"" + year + "\" ";
            }
            query += "WHERE (la.name = '" + lgaName + "') AND (sa.WTName = '" + type + "') AND (sa.period = '" + year + "')";

        
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String name  = results.getString("LGA");

                ArrayList<Double> percentages = new ArrayList<>();
                for(String i : subtypes)
                {
                    percentages.add(results.getDouble(i.replace(" ", "_")));
                }

                double difference = results.getDouble("Percentage Recycled");
                

                lga = new LGA(name, percentages, difference);

            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lga;
    }

    public ArrayList<String> getAllLGAsName(String year) {
        ArrayList<String> lgas = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT name FROM lga l JOIN LGAStatistics s ON s.LGAcode = l.code WHERE period IS '" + year + "' ORDER BY name";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                lgas.add(results.getString("name"));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return lgas;
    }

    public int getTotalLGAs(String year) {

        Connection connection = null;
        int count = -1;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT COUNT(*) FROM lga l JOIN LGAStatistics s ON s.LGAcode = l.code WHERE s.period IS '" + year + "'";
            
            ResultSet results = statement.executeQuery(query);

            count     = results.getInt("COUNT(*)");
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return count;
    }

    public int getHouseholdsSurveyed(String year) {

        Connection connection = null;
        int count = -1;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT SUM(householdSurveyed) FROM LGAStatistics WHERE period IS '" + year + "'";
            
            ResultSet results = statement.executeQuery(query);

            count     = results.getInt("SUM(householdSurveyed)");
            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return count;
    }

    public String formatInt(int input)
    {
        return String.format("%,d",input);
    }

    public ArrayList<String> getLGAPeriods()
    {
        ArrayList<String> periods = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT(period) FROM LGAStatistics";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String year  = results.getString("period");

                periods.add(year);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return periods;
    }

    public ArrayList<String> getWasteTypes() {
        ArrayList<String> wastes = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT(WTname) FROM WasteCollectionType";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                wastes.add(results.getString("WTname"));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return wastes;
    }

    public ArrayList<String> getWasteSubTypes(String type) {
        ArrayList<String> wastes = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT name FROM WasteCollectionType WHERE WTname IS '" + type + "'";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                wastes.add(results.getString("name"));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return wastes;
    }

    public ArrayList<String[]> getRegionalGroups() {
        ArrayList<String[]> groups = new ArrayList<String[]>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT name, fullName from RegionalGroup";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String[] groupNames = {results.getString("name"), results.getString("fullName")};
                groups.add(groupNames);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return groups;
    }


    public ArrayList<Persona> getPersonas() {
        ArrayList<Persona> personas = new ArrayList<Persona>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM Persona ORDER BY age DESC";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Persona persona = new Persona(
                    results.getString("name"),
                    results.getString("photo"),
                    results.getInt("age"),
                    results.getString("background"),
                    results.getString("needsAndGoals"),
                    results.getString("skillAndExperience"),
                    results.getString("directQuotes")
                );
                personas.add(persona);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return personas;
    }

    public ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<Student>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM Student";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                Student student = new Student(
                    results.getString("id"),
                    results.getString("name")
                );
                students.add(student);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }


        return students;
    }

    public ArrayList<String[]> getRegionalGroupWasteStatistics(String sortColumn, String sortOrder, String regionalGroup, String wasteType, String threshold_input, String wasteStatistic) {
        ArrayList<String[]> statistics = new ArrayList<String[]>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT round(sum(collected), 2) as totalCollected, round(sum(recycled), 2) as totalRecycled, round(sum(collected - recycled), 2) as totalDisposed, \r\n" + //
            "round(sum(collected - recycled)/sum(collected)*100, 2) as disposedPercentage, period FROM RegionalGroupWasteStatistics \r\n" + // 
            "WHERE WTname = '" + wasteType + "' AND regionalGroup = '" + regionalGroup + "'\r\n" + //
            "GROUP BY period HAVING total" + wasteStatistic + " >= " + threshold_input + " ORDER BY " + sortColumn + " " + sortOrder;
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String[] record = new String[5];
                record[0] = results.getString("period");
                record[1] = results.getString("totalCollected");
                record[2] = results.getString("totalRecycled");
                record[3] = results.getString("totalDisposed");
                record[4] = results.getString("disposedPercentage") + "%";
                statistics.add(record);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        for (int i = 0; i < statistics.size(); i++) {
            String[] formattedStats = new String[5];
            formattedStats[0] = statistics.get(i)[0];
            for (int j = 1; j < 4; j++) {
                formattedStats[j] = formatDouble(statistics.get(i)[j]);
            }
            formattedStats[4] = statistics.get(i)[4];
            statistics.set(i, formattedStats);
        }

        return statistics;
    }

    public ArrayList<String[]> get2BChartStatistics(String regionalGroup, String wasteType, String threshold_input, String wasteStatistic) {
        ArrayList<String[]> statistics = new ArrayList<String[]>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT round(sum(collected), 2) as totalCollected, round(sum(recycled), 2) as totalRecycled, round(sum(collected - recycled), 2) as totalDisposed, \r\n" + //
            "round(sum(collected - recycled)/sum(collected)*100, 2) as disposedPercentage, period FROM RegionalGroupWasteStatistics \r\n" + // 
            "WHERE WTname = '" + wasteType + "' AND regionalGroup = '" + regionalGroup + "'\r\n" + //
            "GROUP BY period HAVING total" + wasteStatistic + " >= " + threshold_input + " ORDER BY period";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String[] record = new String[5];
                record[0] = results.getString("period");
                record[1] = results.getString("totalCollected");
                record[2] = results.getString("totalRecycled");
                record[3] = results.getString("totalDisposed");
                record[4] = results.getString("disposedPercentage") + "%";
                statistics.add(record);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return statistics;
    }

    public ArrayList<String> getPeriods() {
        ArrayList<String> periods = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT period FROM period ORDER BY period";
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                periods.add(results.getString("period"));
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return periods;
    }

    public ArrayList<String[]> getPeriodicalRegionalWasteStatistic(String startPeriod, String endPeriod, String regionalGroup, String wasteType, String resultDisplay) {
        ArrayList<String[]> stats = new ArrayList<String[]>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT round(sum(collected), 2) as collected, round(sum(recycled), 2) as recycled FROM RegionalGroupWasteStatistics WHERE WTname = '" + 
                            wasteType + "' AND regionalGroup = '" + regionalGroup + 
                            "' AND (period = '" + startPeriod + "' or period = '" + endPeriod + "') group by period" ;
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String[] retrieved = new String[2];
                retrieved[0] = results.getString("collected");
                retrieved[1] = results.getString("recycled");
                stats.add(retrieved);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        double[] difference = {Math.abs(Double.valueOf(stats.get(1)[0]) - Double.valueOf(stats.get(0)[0])), 
                            Math.abs(Double.valueOf(stats.get(1)[1]) - Double.valueOf(stats.get(0)[1]))};

        if (resultDisplay.equals("percentage")) {
            difference[0] = (double) Math.round(difference[0] / Double.valueOf(stats.get(0)[0]) * 10000) / 100;
            difference[1] = (double) Math.round(difference[1] / Double.valueOf(stats.get(0)[1]) * 10000) / 100;
        }

        else {
            difference[0] = (double) Math.round(difference[0] * 100) / 100;
            difference[1] = (double) Math.round(difference[1] * 100) / 100;
        }

        
        String[] differenceDisplay = {String.valueOf(difference[0]), String.valueOf(difference[1])};
        stats.add(differenceDisplay);

        for (int i = 0; i < 3; i++) {
            String[] formattedStats = {formatDouble(stats.get(i)[0]), formatDouble(stats.get(i)[1])};
            stats.set(i, formattedStats);
        }

        stats.get(2)[0] += (resultDisplay.equals("percentage") ? "%": "");
        stats.get(2)[1] += (resultDisplay.equals("percentage") ? "%": "");

        return stats;
    }

    public static String formatDouble(String input) {
        return String.format("%,.2f", Double.valueOf(input));
    }

    public ArrayList<String[]> get3BChartStatistics(String startPeriod, String endPeriod, String regionalGroup, String wasteType) {
        ArrayList<String[]> stats = new ArrayList<String[]>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT period, round(sum(collected), 2) as collected, round(sum(recycled), 2) as recycled FROM RegionalGroupWasteStatistics WHERE WTname = '" + 
                            wasteType + "' AND regionalGroup = '" + regionalGroup + 
                            "' AND (period = '" + startPeriod + "' or period = '" + endPeriod + "') group by period" ;
            
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String[] retrieved = new String[3];
                retrieved[0] = results.getString("period");
                retrieved[1] = results.getString("collected");
                retrieved[2] = results.getString("recycled");
                stats.add(retrieved);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        
        return stats;
    }

    private String queryList(String column, ArrayList<String> names)
    {
        String query = "";
        for(int i = 0; i < names.size(); i++)
        {
            query += column + " = '" + names.get(i) + "'";
            if(i < names.size() - 1)
            {
                query += " OR ";
            }
        }
        return query;
    }

}


