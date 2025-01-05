package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;


public class PageST3B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        HTMLElements elem = new HTMLElements();

        String html = elem.title;

        html += "<body>";

        html += elem.header;

        html += elem.navigation;
        
        html += "<div class='content'>";

        html += "<h2>Recycling and Waste levels varied between periods for a region</h2>";
        
        html += "<div class='container'>";
        
        html += "<div class='form-container'>";

        html += 
            "<form action='/page3B.html' method='post'>" + 
                "<div class='form-section'>" + 
                    "<div class='form-fields'>";

        String startPeriod = context.formParam("startPeriod");
        String endPeriod = context.formParam("endPeriod");
        String regionalGroup = context.formParam("regionalGroup");
        String wasteType = context.formParam("wasteType");
        String resultDisplay = context.formParam("resultDisplay");

        JDBCConnection jdbc = new JDBCConnection();

        ArrayList<String> periods = jdbc.getPeriods();
        html +=             "<div>" +
                                "<label for='start-period'>Start Period</label>" + 
                                "<select name='startPeriod' id='start-period' required onchange='this.form.submit()'>" + 
                                    "<option value='' disabled selected>-Select-</option>";
        for (String period : periods) {
            html += "<option value='" + period + "' " + (period.equals(startPeriod) ? "selected" : "") + ">" + period + "</option>";
        }
        html +=                 "</select>";
        html +=             "</div>";

        html +=             "<div>" + 
                                "<label for='end-period'>End Period</label>" + 
                                "<select name='endPeriod' id='end-period' required>" + 
                                    "<option value='' disabled selected>-Select-</option>";
        for (String period : periods) {
            if (startPeriod != null && period.compareTo(startPeriod) > 0) { 
                html += "<option value='" + period + "' " + (period.equals(endPeriod) ? "selected" : "") + ">" + period + "</option>";
            }
        }
        html +=                 "</select>"+ 
                            "</div>";


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
                                "<label for='resultDisplay'>Results Display</label>" +
                                "<select name='resultDisplay' id='resultDisplay' required>" +
                                    "<option value='' disabled selected>-Select-</option>";
        html +=                     "<option value='percentage'" + ("percentage".equals(resultDisplay) ? "selected" : "") + ">Percentage (%)</option>";
        html +=                     "<option value='absolute'" + ("absolute".equals(resultDisplay) ? "selected" : "") + ">Absolute Value</option>";
        html +=                 "</select>" +
                            "</div>";

        html +=             "<button type='apply-button'>APPLY</button>";

        html +=         "</div>" +  
                    "</div>" + 
                "</form>" +        
            "</div>" ;

        ArrayList<String[]> statistics;
        
        if (startPeriod != null && endPeriod != null && regionalGroup != null && wasteType != null && resultDisplay != null) {
            statistics = jdbc.getPeriodicalRegionalWasteStatistic(startPeriod, endPeriod, regionalGroup, wasteType, resultDisplay);

            html += elem.barChart3B(jdbc.get3BChartStatistics(startPeriod, endPeriod, regionalGroup, wasteType));
            
            String regionalGroupDisplay = null;

            for (String[] group : groups) {
                if (group[0].equals(regionalGroup)) {
                    regionalGroupDisplay = group[1];
                }
            }

            html += "<div class='result-container'>" +
                        "<h3>" + wasteType + " Statistics for " + regionalGroupDisplay + "</h3>";

            html += "<table>";

            html += "<thead><tr> <th>Period</th><th>Collected</th><th>Recycled</th> </tr></thead> <tbody>";

            html += "<tr><td>" + startPeriod  + "</td><td>" + statistics.get(0)[0] + "</td><td>" + statistics.get(0)[1] + "</td>";
            html += "<tr><td>" +  endPeriod   + "</td><td>" + statistics.get(1)[0] + "</td><td>" + statistics.get(1)[1] + "</td>";
            html += "<tr><td>" + "Difference" + "</td><td>" + statistics.get(2)[0] + "</td><td>" + statistics.get(2)[1] + "</td>";

            html += "</tbody></table>";

            html += "<div id='chart_div'></div>";
            
            html += "</div>";
        }
        else {
            html += "<p>Please fill all the fields to display data table</p>";
        }
        
        // Finish the HTML webpage
        html = html + "</div></div>";
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
        
        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
}