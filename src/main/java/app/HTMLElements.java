package app;
import java.util.*;

public class HTMLElements {



    public String title = """
            <html>
                <head>
                    <title>NSW Waste and Recycling Mangement</title>
                    <link rel="icon" href="environment_icon_nobg.png" type="image/png">
                    <link rel='stylesheet' type='text/css' href='common.css' />
                </head>
            """;
            
            public String navigation = """
                <div class="topnav">
                    <a href="/" class="nav-link">Home</a>
                    <a href="/PageAbout.html" class="nav-link">About</a>
                    <a href="/page2A.html" class="nav-link">LGA Statistics</a>
                    <a href="/page2B.html" class="nav-link">Regional Group Statistics</a>
                    <a href="/page3A.html" class="nav-link">Compare LGA</a>
                    <a href="/page3B.html" class="nav-link">Changes Over Time</a>
                </div>
            
                <script>
                    document.addEventListener('DOMContentLoaded', () => {
                    const navLinks = document.querySelectorAll('.topnav .nav-link');
                    const currentPage = window.location.pathname;

                    navLinks.forEach(link => {
                        // Check for an exact match instead of "includes"
                        if (link.getAttribute('href') === currentPage) {
                            link.classList.add('active');
                        }

                        link.addEventListener('click', (event) => {
                            // Remove 'active' from all links
                            navLinks.forEach(nav => nav.classList.remove('active'));
                            // Add 'active' to the clicked link
                            event.target.classList.add('active');
                        });
                    });
                });
                </script>
            """;

    
    public String header = """
                <h1 class="header">
                    <div class="header-container">
                        <div class="top-image-container">
                            <a href='/'><img src="environment_icon_nobg.png" class="top-image" alt="Environment Icon"></a>
                        </div>
                        <div class="banner-container">
                            <span>NEW SOUTH WALES WASTE MANAGEMENT AND RECYCLING</span>
                        </div>
                    </div>
                </h1>
            """;

    public String lineChart2B(ArrayList<String[]> retrieved) {
        String html = """
                    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                    <script type="text/javascript">

                    google.charts.load('current', {'packages':['corechart', 'line']});
                    google.charts.setOnLoadCallback(drawChart);

                    function drawChart() {

                        var data = new google.visualization.DataTable();
                        data.addColumn('string', 'Period');
                        data.addColumn('number', 'Collected');
                        data.addColumn('number', 'Recycled');
                        data.addColumn('number', 'Disposed');
                        data.addRows([
                    """;

        for (int i = 0; i < retrieved.size(); i++) {
            String[] record = retrieved.get(i);
            html += "['" + record[0] + "', " + record[1] + ", " + record[2] + ", " + record[3] + "]";
            if (i < retrieved.size() - 1) {  
                html += ",\n";
            }
        } 

        html += """
                        ]);

                        var options = {
                        title: 'Waste Statistics annually for selected regional group',
                        titleTextStyle: {
                            fontSize: 22, 
                            bold: true
                        },
                        width: 'fit',
                        height: 500,
                        hAxis: {
                            title: 'Period', 
                            titleTextStyle: {
                                fontSize: 14, 
                                italic: false
                            },
                            slantedText: true,
                            slantedTextAngle: 45
                        },
                        vAxis: {
                            title: 'Waste statistic in Tonnes', 
                            titleTextStyle: {
                                fontSize: 14, 
                                italic: false
                            }
                        }
                    };

                    var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                    chart.draw(data, google.charts.Line.convertOptions(options));
                    }
                    </script>
                """;

        return html;
    }

    public String barChart3B(ArrayList<String[]> retrieved) {
        String html = """
                    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                    <script type="text/javascript">

                    google.charts.load('current', {'packages':['corechart']});
                    google.charts.setOnLoadCallback(drawChart);

                    function drawChart() {

                        var data = new google.visualization.DataTable();
                        data.addColumn('string', 'Period');
                        data.addColumn('number', 'Collected');
                        data.addColumn('number', 'Recycled');
                        data.addRows([
                    """;

        
        html += "['" + retrieved.get(0)[0] + "', " + retrieved.get(0)[1] + ", " + retrieved.get(0)[2] + "],";
        html += "['" + retrieved.get(1)[0] + "', " + retrieved.get(1)[1] + ", " + retrieved.get(1)[2] + "]";
            
        html += """
                        ]);

                        var options = {
                            title: 'Waste Collection and Recycling Comparison',
                            titleTextStyle: { fontSize: 22, bold: true },
                            hAxis: {title: 'Period',
                                    titleTextStyle: {
                                    fontSize: 14, 
                                    italic: false
                                }
                            },
                            bars: 'horizontal',
                            vAxis: {title: 'Amount in Tonnes',
                                    titleTextStyle: {
                                    fontSize: 14, 
                                    italic: false
                                }
                            },
                            legend: { position: 'top' },
                            annotations: {
                                alwaysOutside: true,
                                textStyle: { fontSize: 12, color: '#000' }
                            },
                            width: 'fit',
                            height: 500
                        };

                    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
                    chart.draw(data, options);

                    }
                    </script>
                """;

        return html;
    }

    public String barChart2a(ArrayList<LGA> lgas) {
       String html = """
                <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                <script type="text/javascript">
                google.charts.load('current', {'packages':['bar']});
                google.charts.setOnLoadCallback(drawStuff);

                function drawStuff() {
                    var data = new google.visualization.arrayToDataTable([
                    ['LGA', 'Percent Recycled', 'Average Waste']
                    """;
                for(LGA i: lgas) {
                    html += ",['"+ i.getName() + "'," + i.getRate() * 100 + "," + i.getAverage() +"]";
                }
                            
                    
                    
                html += """
                    ]);

                    var options = {
                        title: 'LGA Percent Recycled and Average Waste',
                        titleTextStyle: { fontSize: 22, bold: true },
                        width: 'fit',
                        height: 500,
                        
                        bars: 'horizontal', 
                        series: {
                            0: { axis: 'Percent' }, 
                            1: { axis: 'Average' } 
                        },
                        axes: {
                            x: {
                            Percent: {side: 'top', label: 'Percentage Recycled'}, 
                            Average: {label: 'Average Waste per Household (in Tonnes)'} 
                            }
                        }
                    };

                var chart = new google.charts.Bar(document.getElementById('dual_x_div'));
                chart.draw(data, options);

                }
                </script>
               """;
        return html;
    }

    public String barChart3a(ArrayList<LGA> lgas) {
        String html = """
                <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
                <script type="text/javascript">

                google.charts.load('current', {'packages':['corechart']});
                google.charts.setOnLoadCallback(drawChart);

                function drawChart() {
                     var data = new google.visualization.arrayToDataTable([
                     ['LGA', 'Percent Recycled', {role: 'style'}]
                     """;
                boolean top = true;
                 for(LGA i: lgas) {
                    html += ",['"+ i.getName() + "'," + i.getRate() * 100;
                    if(top)
                    {
                        html += ", 'color: #ff5733']";
                        top = false;
                    }
                    else
                    {
                        html += ", 'color: #3366cc']";
                    }
                 }
                             
                     
                     
                 html += """
                     ]);
 
                     var options = {
                        title: 'Total Recycled Percentage Comparison',
                        titleTextStyle: { fontSize: 22, bold: true },
                        hAxis: {title: 'Percent Recycled',
                                titleTextStyle: {
                                fontSize: 14, 
                                italic: false
                            }
                        },
                        bars: 'horizontal',
                        vAxis: {title: 'LGA',
                                titleTextStyle: {
                                fontSize: 14, 
                                italic: false
                            }
                        },
                        legend: { position: 'none' },
                        annotations: {
                            alwaysOutside: true,
                            textStyle: { fontSize: 12, color: '#000' }
                        },
                        width: 'fit',
                        height: 500
                    };

                var chart = new google.visualization.BarChart(document.getElementById('dual_x_div'));
                chart.draw(data, options);

                }
                </script>
                """;
         return html;
     }

    public String breadcrumb(String... titles)
    {
        return """
                <ul class='breadcrumb'>
                <li><a href='#'>Home</a></li>
                </ul>
                """;
    }

    public String radioSelection(ArrayList<String> options, String option, String outcome)
    {
        String html = "";
        for(String i: options)
        {
            html += "<input type='radio' class='checks' id='" + i + "' name='" + option + "' value='" + i + "'";
            
            if(outcome != null && outcome.equals(i))
            {
                html += " checked";
            }
            html += " onchange='this.form.submit()'><label for='" + i + "'>" + i + "</label><br>";
        }
        
        return html;
    }

    public String checkSelection(ArrayList<String> options, ArrayList<String> selections)
    {
        
        String html = "";
        for(String i: options)
        {
            html += "<input type='checkbox' class='checks' id='" + i + "' name='" + i + "' value='" + i + "'";
            if(selections.contains(i))
            {
                html += " checked";
            }
            html += " onchange='this.form.submit()'><label for='" + i + "'>" + i + "</label><br>";
        }
        return html;
    }      
    
    
    public String dropSelection(String label, ArrayList<String> options, String option, String outcome, boolean firstOption)
    {
        String html = "<label for ='" + option + "'>" + label + "</label>";
        html += "<select name='" + option + "' id='" + option + "' onchange='this.form.submit()'>";
        html += (firstOption ? "<option value=''>Select</option>": "");
        for(String i: options)
        {
            html += "<option value='" + i + "'";
            
            if(outcome != null && outcome.equals(i))
            {
                html += " selected";
            }
            html += ">" + i + "</option>";
        }
        html += "</select>";
        
        return html;
    }

    public String checkSelectionSearch(ArrayList<String> options, ArrayList<String> selections)
    {
        
        String html = "<input type=\"text\" id=\"searchBar\" onkeyup=\"myFunction()\" placeholder=\"-Search options-\">";
        
        html += "<div class='form-checkbox' id='myUL'>";
        for(String i: options)
        {
            html += "<label style='display: flex; align-items: center;'><input type='checkbox' class='checks' id='" + i + "' name='" + i + "' value='" + i + "'";
            if(selections.contains(i))
            {
                html += " checked";
            }    
            html += " onchange='this.form.submit()'>" + i + "</label>";
        }
        html += "</div>";

        html += """
                <script>
                function myFunction() {
                    var input, filter, ul, labels;
                    input = document.getElementById("searchBar");
                    filter = input.value.toUpperCase();
                    ul = document.getElementById("myUL");
                    labels = ul.getElementsByTagName("label");
                    for (i = 0; i < labels.length; i++) {
                        var label = labels[i];
                        var txtValue = label.textContent || label.innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            label.style.display = "flex";
                        } else {
                            label.style.display = "none";
                        }
                    }
                }

                const scrollBox = document.getElementById("myUL");

                scrollBox.addEventListener("scroll", () => {
                localStorage.setItem("scrollBoxPosition", scrollBox.scrollTop);
                });

                window.addEventListener("load", () => {
                const scrollPosition = localStorage.getItem("scrollBoxPosition");
                if (scrollPosition) {
                    scrollBox.scrollTop = parseInt(scrollPosition, 10);
                }
                });
                </script>
                """;
        return html;
    }

    
}
