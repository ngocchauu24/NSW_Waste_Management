package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;


public class PageST2B implements Handler {

    
    public static final String URL = "/page2B.html";

    @Override
    public void handle(Context context) throws Exception {
        HTMLElements elem = new HTMLElements();
    
        String html = elem.title + "<body>" + elem.header + elem.navigation;
        
        html += "<div class='content'>";
                
        html +=  "<h2>Recycling and Waste statistics over time for a Region</h2>";
  
        html += "<div class='container'>";
        
        html += "<div class='form-container'>";

        html += 
            "<form action='/page2B.html' method='post'>" + 
                "<div class='form-section'>" + 
                    "<div class='form-fields'>";

        String regionalGroup = context.formParam("regionalGroup");
        String wasteType = context.formParam("wasteType");
        String threshold = context.formParam("threshold");
        String wasteStatistic = context.formParam("wasteStatistic");
        String sortColumn = context.formParam("sortColumn");
        String sortOrder = context.formParam("sortOrder");

        JDBCConnection jdbc = new JDBCConnection();

        ArrayList<String[]> groups = jdbc.getRegionalGroups();
        html +=             "<div>" +
                                "<label for='regional-group'>Regional Group</label>" +
                                "<select name='regionalGroup' id='regional-group' required>" +
                                    "<option value='' disabled selected>-Select-</option>";
        for (String[] group : groups) {
            html += "<option value='" + group[0] + "'" + (group[0].equals(regionalGroup) ? "selected" : "") + ">" + group[1] + "</option>";
        }
        html +=                 "</select>" +
                            "</div>";


        ArrayList<String> wasteTypes = jdbc.getWasteTypes();
        html +=             "<div>" +
                                "<label for='waste-resource-type'>Waste Resource Type</label>" +
                                "<select name='wasteType' id='waste-resource-type' required>" +
                                    "<option value='' disabled selected>-Select-</option>"; 
        for (String type : wasteTypes) {
            html += "<option value='" + type + "'" + (type.equals(wasteType) ? "selected" : "") + ">" + type + "</option>";
        }
        html +=                 "</select>" +
                            "</div>";
               
                            
        html +=             "<div>" +
                                "<label for='threshold'>Threshold (tonnes)</label>" +
                                "<input type='number' name='threshold' id='threshold' placeholder='-Enter number-' min='0' step='1' required" +
                                (threshold != null ? " value='" + threshold + "'" : "") + ">" +
                            "</div>";


        String[] wasteStatistics = {"Collected", "Recycled", "Disposed"};
        html +=             "<div>" +
                                "<label for='waste-statistic'>Apply Threshold on</label>" +
                                "<select name='wasteStatistic' id='waste-statistic' required>" +
                                    "<option value='' disabled selected>-Select-</option>";
        for (String stat : wasteStatistics) {
            html += "<option value='" + stat.toLowerCase() + "'" + (stat.toLowerCase().equals(wasteStatistic) ? "selected" : "") + ">" + stat + "</option>";
        }
        html +=                  "</select>" +
                            "</div>";

                
        String[][] thresholdColumns = {{"period", "Recorded Period"},
                                        {"totalCollected", "Total Waste Collected"},
                                        {"totalRecycled", "Total Waste Recycled"},
                                        {"totalDisposed", "Total Waste Disposed"},
                                        {"disposedPercentage", "Disposed Waste Percentage"}};
        html +=             "<div>" +
                                "<label for='sort-column'>Sort by Column</label>" +
                                "<select name='sortColumn' id='sort-column' required>" +
                                    "<option value='' disabled selected>-Select-</option>";
        for (String[] column : thresholdColumns) {
            html += "<option value='" + column[0] + "'" + (column[0].equals(sortColumn) ? "Selected" : "") + ">" + column[1] + "</option>";
        }
        html +=                 "</select>" +
                            "</div>";

                            
        html +=             "<div>" +
                                "<label for='sort-order'>Sort by Order</label>" +
                                "<select name='sortOrder' id='sort-order' required>" +
                                    "<option value='asc'";
                            if (sortOrder == null || sortOrder.equals("asc")) {
                                html += "selected";
                            }
                            else {
                                html += "";
                            }
        html +=                      ">Low to High</option>" +
                                    "<option value='desc'" + ("desc".equals(sortOrder) ? "selected" : "") + ">High to Low</option>" +
                                "</select>" +
                            "</div>";

        html +=              "<button type='apply-button'>APPLY</button>";
       
        html +=         "</div>" +  
                    "</div>" +       
                "</form>" +          
            "</div>";


        ArrayList<String[]> retrieved, chartData;

        if (wasteType != null && threshold != null && wasteStatistic != null && sortColumn != null) {
            retrieved = jdbc.getRegionalGroupWasteStatistics(sortColumn, sortOrder, regionalGroup, wasteType, threshold, wasteStatistic);

            if (retrieved.size() > 0) {

                chartData = jdbc.get2BChartStatistics(regionalGroup, wasteType, threshold, wasteStatistic);
                html += elem.lineChart2B(chartData);

                String regionalGroupDisplay = null;

                for (String[] group : groups) {
                    if (group[0].equals(regionalGroup)) {
                        regionalGroupDisplay = group[1];
                    }
                }

                html += "<div class='result-container'>" +
                            "<h3>" + wasteType + " Statistics for " + regionalGroupDisplay + "</h3>";

                html += "<table>";

                html += "<thead><tr> <th>Period</th><th>Collected</th><th>Recycled</th><th>Disposed</th><th>Percentage Disposed</th> </tr></thead> <tbody>";

                for (String[] row : retrieved) {
                    html += "<tr><td>" + row[0] + "</td><td>" + row[1] + "</td><td>" + row[2] + "</td>" + "<td>" + row[3] + "</td>" + "<td>" + row[4] + "</td>";
                }
                
                html += "</tbody></table>";

                html += "<div id='chart_div'></div>";

                html += "</div>";  
            }
            else {
                html += "<p style='color: red;'>Please select a lower threshold</p>";
            }
        }
        else {
            html += "<p>Please fill all the fields to display data table</p>";
        }

        // Finish the HTML webpage
        html += "</div></div>";
        html += """
        <div class="footer-container">
            <p>&copy; 2025 NSW Waste Management and Recycling Project</p>
            <p>
                Created by <strong>Ngoc Chau Vu (s4066600)</strong> and <strong>Jordan Wallace (s4087393)</strong> 
                for the <em>Java Programming Studio (COSC 2803)</em> course at <strong>RMIT University</strong>.
            </p>
            <p>
                Logo from 
                <a href="https://www.flaticon.com/free-icons/environmental" 
                   title="Environmental icons" 
                   target="_blank">Environmental icons by Freepik - Flaticon</a>.
            </p>
            <p>
                Persona images from 
                <a href="https://thispersondoesnotexist.com/" target="_blank">Persona image</a>.
            </p>
        </div>
        </body></html>
        """;
    
        // Makes Javalin render the webpage
        context.html(html);
    }

}
