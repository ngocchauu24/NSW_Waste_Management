package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;


public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        HTMLElements elem = new HTMLElements();
        JDBCConnection jdbc = new JDBCConnection();
        String year = context.formParam("year");
        ArrayList<String> periods = jdbc.getLGAPeriods();
        ArrayList<String> lgas = jdbc.getAllLGAsName(year);
        String lga = context.formParam("lga");
        ArrayList<String> wasteTypes = jdbc.getWasteTypes();
        String wasteType = context.formParam("waste");
        String quantity = context.formParam("quantity");
        ArrayList<String> subtypes = jdbc.getWasteSubTypes(wasteType);
        String sort = context.formParam("sort");
        boolean desc = Boolean.parseBoolean(context.formParam("desc"));
        if(sort == null)
        {
            sort = "Difference";
        }


        String html = elem.title;


        html = html + "<body>";

        html = html + elem.header;

        html = html + elem.navigation;

        
        // Add Div for page Content
        html = html + "<div class='content'>";
        html += "<h2>Compare Local Goverment Areas</h2>";
        html += "<div class='container'>";
        html += "<div class='form-container'>";
        html += "<form action='/page3A.html' method='post'>";
        html += "<div class='form-section'>";
        html += "<div class='form-fields'>";
        html += "<div>";
        html += elem.dropSelection("Select Period", periods, "year", year, true);
        html += "</div><div>";
        if(year != null)
        {

        
            html += elem.dropSelection("Select One LGA", lgas, "lga", lga, true);
            html += "</div><div>";
            html += elem.dropSelection("Select Waste Type", wasteTypes, "waste", wasteType, true);
            html += "</div><div>";
            html += "<label for='quantity'>LGA's to Compare</label>";
            html+= "<input type='number' name='quantity' id='quantity' placeholder='Enter number' min='1' max='100' step='1' required onchange='this.form.submit()'" +
            (quantity != null ? " value='" + quantity + "'>" : ">");
            html +=              "</div><div><button type='apply-button'>APPLY</button>";

           
        }

       html += "</div></div></div></div><div class='results-container'>";
       
       
        ArrayList<LGA> data = jdbc.getLGAdata2(lga, year, wasteType, subtypes, quantity, sort, desc);
        LGA compare = jdbc.getLGAcomparedata2(lga, year, wasteType, subtypes);
       if(data.size() > 0)
       {
           ArrayList<String> columns = new ArrayList<String>();
           columns.add("LGA");
           for(String i: subtypes)
           {
            columns.add(i);
           }
           columns.add("Difference");

           ArrayList<String> columnsCompare = new ArrayList<String>();
           columnsCompare.add("LGA");
           for(String i: subtypes)
           {
            columnsCompare.add(i);
           }
           columnsCompare.add("Total Percentage Recycled");

           html += "<h3>Results for " + quantity + " most similar LGAs to " + lga + "</h3>";
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
           html = html + "<div><table>";
           html += "<thead><tr>";
           for(String i: columnsCompare)
           {
            html += "<th>" + i + "</th>";
           }
           html += "</tr></thead><tbody>";
           html += "<tr><td>" + compare.getName() + "</td>";
           for(double i: compare.getPercentages())
           {
            html += String.format("<td>%.2f%%</td>", i * 100);
           }
           html += String.format("<td>%.2f%%</td></tr></tbody>", compare.getRate() * 100);
           
           html+= "<thead><tr>";
           for(String i: columns)
           {
               html += "<th>" + i + "</th>";
           }
           
           html += "</tr></thead><tbody>";
           for(LGA i: data)
           {
               html += "<tr><td>" + i.getName() + "</td>";
               for(Double j: i.getPercentages())
               {
                    html += String.format("<td>%.2f%%</td>", j * 100);
               }
               html += String.format("<td>%+.2f%%</td></tr>", i.getRate() * 100);
               
           }
           html = html + "</tbody></table></div>";
           ArrayList<LGA> chartLGAs = new ArrayList<>();
           chartLGAs.add(compare);
           for(LGA i: data)
           {
            chartLGAs.add(jdbc.getLGAcomparedata2(i.getName(), year, wasteType, subtypes));
           }
           html += elem.barChart3a(chartLGAs);
           html += "<div id='dual_x_div'></div>";
                   
       }
       else{
           html += "<p>Please fill all the fields to display data table</p>";
       }


       html += "</form>";
        html = html + "</div></div></div>";

        

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

}
