package demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static tech.tablesaw.aggregate.AggregateFunctions.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.PieTrace;
import tech.tablesaw.plotly.traces.Trace;
import tech.tablesaw.table.TableSliceGroup;

public class safetyconcern {
    static Connection connection = null;
    static String databasename = "";
    static String DB_URL = "jdbc:mysql://localhost:3306/sys";

    static String username = "root";
    static String password = "401111";

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, username, password);

        // safety_concern
        Table safety_concern = null;
        {
            try (Statement stmt = connection.createStatement()) {
                String sql = "SELECT * FROM safety_concern";
                try (ResultSet results = stmt.executeQuery(sql)) {
                    safety_concern = Table.read().db(results, "safety_concern");
                    System.out.println(safety_concern.structure());
                }
            }

        }

        // pie chart
        PieTrace trace = carspec.pie(safety_concern, "maint");
        PieTrace trace2 = carspec.pie(safety_concern, "safety");
        PieTrace trace3 = carspec.pie(safety_concern, "acceptability");
        Layout layout = Layout.builder().title("Distribution of maintenance").build();
        Layout layout2 = Layout.builder().title("Distribution of safety level").build();
        Layout layout3 = Layout.builder().title("Distribution of acceptability").build();
        Plot.show(new Figure(layout, trace));
        Plot.show(new Figure(layout2, trace2));
        Plot.show(new Figure(layout3, trace3));

        // histogram for maint
        Layout Hlayout = Layout.builder()
                .title("Distribution of safety concern")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        TableSliceGroup groups = safety_concern.splitOn("maint");
        Table t11 = groups.get(0).asTable();
        Table t12 = groups.get(1).asTable();
        Table t13 = groups.get(2).asTable();
        Table t14 = groups.get(3).asTable();
        HistogramTrace trace11 = buyingconcern.Mainthistogram(t11, "maint", 0);
        HistogramTrace trace12 = buyingconcern.Mainthistogram(t12, "maint", 1);
        HistogramTrace trace13 = buyingconcern.Mainthistogram(t13, "maint", 2);
        HistogramTrace trace14 = buyingconcern.Mainthistogram(t14, "maint", 3);
        Plot.show(new Figure(Hlayout, trace11, trace12, trace13, trace14));

        // histogram for safety
        TableSliceGroup groups2 = safety_concern.splitOn("safety");
        Table t21 = groups2.get(0).asTable();
        Table t22 = groups2.get(1).asTable();
        Table t23 = groups2.get(2).asTable();
        HistogramTrace trace21 = Safetyhistogram(t21, "safety", 0);
        HistogramTrace trace22 = Safetyhistogram(t22, "safety", 1);
        HistogramTrace trace23 = Safetyhistogram(t23, "safety", 2);
        Plot.show(new Figure(Hlayout, trace21, trace22, trace23));

        // histogram for acceptability
        TableSliceGroup groups3 = safety_concern.splitOn("acceptability");
        Table t31 = groups3.get(0).asTable();
        Table t32 = groups3.get(1).asTable();
        Table t33 = groups3.get(2).asTable();
        Table t34 = groups3.get(3).asTable();
        HistogramTrace trace31 = buyingconcern.Accephistogram(t31, "acceptability", 0);
        HistogramTrace trace32 = buyingconcern.Accephistogram(t32, "acceptability", 1);
        HistogramTrace trace33 = buyingconcern.Accephistogram(t33, "acceptability", 2);
        HistogramTrace trace34 = buyingconcern.Accephistogram(t34, "acceptability", 3);
        Plot.show(new Figure(Hlayout, trace31, trace32, trace33, trace34));

        // bar chart
        Table summaryTable = safety_concern.summarize("maint", "safety", "acceptability", sum).by("acceptability");

        Layout Blayout = Layout.builder()
                .title("Safety Concern")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        String[] numberColNames = { "SUM [maint]", "SUM [safety]", "SUM [acceptability]" };
        String[] colors = { "#85144b", "#FF4136", "463E3F" };

        Trace[] traces = new Trace[3];
        for (int i = 0; i < 3; i++) {
            String name = numberColNames[i];
            BarTrace Btrace = BarTrace
                    .builder(summaryTable.categoricalColumn("acceptability"), summaryTable.numberColumn(name))
                    .orientation(BarTrace.Orientation.VERTICAL)
                    .marker(Marker.builder().color(colors[i]).build())
                    .showLegend(true)
                    .name(name)
                    .build();
            traces[i] = Btrace;
        }
        Plot.show(new Figure(Blayout, traces));
    }

    private static HistogramTrace Safetyhistogram(Table t, String column, int i) {
        String[] name = { "LOW", "MEDIUM", "HIGH" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

}
