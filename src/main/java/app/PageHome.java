package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PageHome implements Handler {

    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        HTMLElements elem = new HTMLElements();
        JDBCConnection jdbc = new JDBCConnection();

        // Start HTML structure
        String html = elem.title.replace("NAME", "Home");

        html += "<body>";
        html += elem.header;
        html += elem.navigation;
 
        html += "<div class='home-content'>";

        html += """
        <div class='grid-two-columns1'>
            <div class='section-box'>
                <h2>What can you find</h2>
                <ul>
                <li>Explore the most extensive survey information on NSW domestic waste and recycling.</li>
                <li>How does your area compare?</li>
                <li>What more can you be doing?</li>
                </ul>
            </div>
            
            <div class='section-box'>
                <h2>How New South Wales Recycles</h2>
            """;

        html += """
                <div>
                    <table class='home-table'>
                        <thead><tr>
                            <th colspan='2' style='background-color: #C5CEB6; color: #000000;'>2018-2019</th>
                        </tr>
                        <tr>
                            <th>Total Local Government Areas</th>
                            <th>Houses Surveyed</th>
                        </tr></thead><tbody>
                        <tr>
                            <td>""" + jdbc.getTotalLGAs("2018-2019") + """
                                    </td>
                            <td>""" + jdbc.formatInt(jdbc.getHouseholdsSurveyed("2018-2019")) + """
                                </td>
                        </tr></tbody>
                    </table>
                    <table class='home-table'>
                        <thead><tr>
                            <th colspan='2' style='background-color: #C5CEB6; color: #000000;'>2019-2020</th>
                        </tr>
                        <tr>
                            <th>Total Local Government Areas</th>
                            <th>Houses Surveyed</th>
                        </tr></thead><tbody>
                        <tr>
                            <td>""" + jdbc.getTotalLGAs("2019-2020") + """
                                    </td>
                            <td>""" + jdbc.formatInt(jdbc.getHouseholdsSurveyed("2019-2020")) + """
                            </td>
                        </tr></tbody>
                    </table>
                </div>
            </div>
        </div>    
        """;

        html += """
        <div class='section-box'>
            <h2>Which data is provided</h2>
            <p>A survey of domestic waste collection has been conducted since 2013 looking at the different regional areas of New South Wales. These regional areas are: Sydney Metropolitan Area (SMA), Extended Regulated Area (ERA), Regional Regulated Area (RRA) and Rest of NSW. 
            This website currently has date upto 2022 for these areas as well as more detailed Local Goverment Area Stats for 2018-2019 and 2019-2020. Each stat is split into 3 different waste types with further sub waste types:<br>
            <div class='grid-three-columns'>
                <div class='waste-box'>
                    <strong>Waste</strong>
                        <ul><li>Kerbside Waste Bin</li>
                        <li>Clean Up</li>
                        <li>Drop Off</li></ul>
                </div>
                <div class='waste-box'>
                    <strong>Recycling</strong>
                        <ul><li>Kerbside Recycling Bin</li>
                        <li>CDS Recycling (Container Deposit Scheme)</li>
                        <li>Drop Off Recycling</li>
                        <li>Clean Up Recycling</li></ul>
                </div>
                <div class='waste-box'>
                    <strong>Organics</strong>
                        <ul><li>Kerbside Organics Bin</li>
                        <li>Kerbside FOGO Organics (Food Orgnaics Garden Organics)</li>
                        <li>Drop Off Organics</li>
                        <li>Clean Up Organics</li>
                        <li>Other Council Garden Organics</li></ul></ol></p>
                </div>
            </div>
        </div>
         """;         

        // html = html + "</div>";

        // Close content, body, and HTML tags
        html += "</div>"; // Close content
        html += "</body></html>";

        // Send HTML response
        context.html(html);
    }
}