package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;


public class PageST2A implements Handler {

    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        JDBCConnection jdbc = new JDBCConnection();
        HTMLElements elem = new HTMLElements();
        String year = "2019-2020";
        

        
        ArrayList<String> lgas = jdbc.getAllLGAsName(year);
        ArrayList<String> selections = new ArrayList<String>();
        for(String i: lgas)
        {
            if(context.formParam(i) != null)
            {
                selections.add(context.formParam(i));
            }
        }
        String waste = context.formParam("waste");
        ArrayList<String> wasteSubTypes = jdbc.getWasteSubTypes(waste);
        ArrayList<String> subTypes = new ArrayList<String>();
        for(String i: wasteSubTypes)
        {
            if(context.formParam(i) != null)
            {
                subTypes.add(context.formParam(i));
            }
        }
        
        String sort = context.formParam("sort");
        boolean desc = Boolean.parseBoolean(context.formParam("desc"));

        String html = elem.title;

      

        html = html + "<body>";

        html = html + elem.header;

        html = html + elem.navigation;

        html = html + "<div class='content'>";
        
        html += "<h2>Local Government Area Statistics for 2019-2020</h2>";
        html = html + "<div class='container'>";
        html += "<div class='form-container'>";
        
        
        ArrayList<String> wasteTypes = jdbc.getWasteTypes();
        
        html = html + "<form action='/page2A.html' method='post'>";
        html += "<div class='form-section'>";
        html += "<div class='form-fields'>";
        html = html + "<div>";
        
        
        
        html = html + "<label>Select LGAs</label>";
        
        html += elem.checkSelectionSearch(lgas, selections);
        
        html += "</div><div>";
        html = html + elem.dropSelection("Select Waste Type", wasteTypes, "waste", waste, true);
        if(wasteSubTypes.size() > 0)
        {
            html += "</div><div>";
            html += "<label> Select Waste Sub Types</label>";
            html += "<div class='form-checkbox'>";
            html += elem.checkSelection(wasteSubTypes, subTypes);
            html += "</div>";
            html += "</div><div><button type='apply-button'>APPLY</button>";
        }
            
        
        html += "</div>";
        
        html = html + "</div></div></div><div class='result-container'>";
        

        
        ArrayList<LGA> data = jdbc.getLGAdata1(selections, year, subTypes, sort, desc);
        if(data.size() > 0)
        {
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("LGA");
            columns.add("Population");
            columns.add("Houses Surveyed");
            columns.add("Total Waste Collected");
            columns.add("Total Waste Recycled");
            columns.add("Average Percentage Recycled");
            columns.add("Average Waste per Household");
            html += "<h3>Results for Selected LGAs</h3>";
            html += "<p>(Waste calculated in tonnes)</p>";
            html += elem.dropSelection("Sort By", columns, "sort", sort, false);
            html += "<select name='desc' id='desc' onchange='this.form.submit()'>" +
                        "<option value='false'";
            if(!desc)
            {
                html += " selected";
            }
            html +=     ">Ascending</option>" +
                        "<option value='true'";
            if(desc)
            {
                html += " selected";
            }
            html +=     ">Descending</option></select>";
            html = html + "<table>";
            
            html+= "<thead><tr>";
            for(String i: columns)
            {
                html += "<th>" + i + "</th>";
            }
            
            html += "</tr></thead><tbody>";
            for(LGA i: data)
            {
                html = html + String.format("<tr><td>%s</td><td>%,d</td><td>%,d</td><td>%,d</td><td>%,d</td><td>%.2f%%</td><td>%.2f</td></tr>", i.getName(), i.getPopulation(), i.getHouseholds(), i.getCollected(), i.getRecycled(), i.getRate() * 100, i.getAverage());

            }
            html = html + "</tbody></table>";
            html += elem.barChart2a(data);
            html += "<h2>LGA Percent Recycled and Average Waste</h2>";
            html += "<div id='dual_x_div'></div>";
                    
        }
        else{
            html += "<p>Please fill all the fields to display data table</p>";
            if(selections.size() > 0)
            {
                html += "<h4>Selected LGAs</h4>";
                html += "<table class='lgalist'>";
                for(String i: selections)
                {
                    html += "<tr><td>" + i + "</td></tr>";
                }
                html += "</table>";
            }
        }
        html = html + "</form>";

        // Add HTML for the page content
        

        // Close Content div
        html = html + "</div></div>";

        

        // Finish the HTML webpage
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
