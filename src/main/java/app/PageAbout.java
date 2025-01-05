package app;

import java.util.ArrayList;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PageAbout implements Handler {

    public static final String URL = "/PageAbout.html";

    @Override
    public void handle(Context context) throws Exception {
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<Persona> personas = jdbc.getPersonas();
        ArrayList<Student> students = jdbc.getStudents();

        HTMLElements elem = new HTMLElements();
        String html = elem.title.replace("NAME", "About");

        html += "<body>";
        html += elem.header;
        html += elem.navigation;

        html += """
        <div class='about-content'>
        """;

        html += """
        <div class='grid-two-columns1'>
            <div class='section-box'>
                <h2>Our Purpose</h2>
                <p>Our purpose is to provide unbiased information on recycling and waste production / management levels in recent years for various Local Government Areas (LGAs) throughout NSW.</p>
            </div>
            <div class='section-box'>
                <h2>How to Use This Website Efficiently</h2>
                <ul>
                    <li>The "Home" page on the left content bar shows total number of LGAS and total houses surveyed for 2018-2019 and 2019-2020 periods.</li>
                    <li>The "About" page presents our purpose, our customers' attributes, the instruction to use this page and our contacts.</li>
                    <li>The "LGA Statistic" page presents information about domestic waste management and recycling for user selected LGAs in the 2019-2020 period.</li>
                    <li>The "Regional Group Statistics" page presents information about domestic waste management and recycling by a user sekected Regional group.</li>
                    <li>The "Compare LGA" page enable to identify LGAs that have similar levels of recycling and waste for specific time periods and combinations of statistics.</li>
                    <li>The "Changes Over Time" page enable to identify how recycling and waste levels have varied between any two selected yearly periods for a selected Region.</li>
                </ul>
            </div>
        </div>
        """;

        html += """
        <div class='section-box'>
            <h2>Our Typical Customers</h2>
            <div class='grid-three-columns'>
        """;

        for (Persona persona : personas) {
            html += 
            "<div class='persona-box'>" +
                "<h3>" + persona.name + "</h3>" +
                "<img src='" + persona.getImageSource() + "' class='persona-image'>" +
                "<p><strong>Age:</strong> " + persona.age + "</p>" +
                "<p><strong>Background:</strong> " + persona.background + "</p>" +
                "<p><strong>Needs and Goals:</strong> " + persona.needsAndGoals + "</p>" +
                "<p><strong>Skills and Experience:</strong> " + persona.skillAndExperience + "</p>" +
                "<p><strong>Direct Quotes:</strong> " + persona.directQuotes + "</p>" +
            "</div>";
        }
        html += "</div></div>";  

        html += """
        <div class='section-box'>
            <h2>About Us</h2>
            <div class='grid-two-columns2'>
        """;

        for (Student student : students) {
            html += 
            "<div class='student-box'>" +
                "<h3>" + student.name + "</h3>" +
                "<p><strong>ID:</strong> " + student.id + "</p>" +
            "</div>";
        }
        html += "</div></div>";  

        html += "</div>";  

        // Footer
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

        context.html(html);
    }
}