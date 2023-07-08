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

public class buyingconcern {
    static Connection connection = null;
    static String databasename = "";
    static String DB_URL = "jdbc:mysql://localhost:3306/sys";

    static String username = "root";
    static String password = "401111";

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, username, password);

        // buying_concern
        Table buying_concern = null;
        {
            try (Statement stmt = connection.createStatement()) {
                String sql = "SELECT * FROM buying_concern";
                try (ResultSet results = stmt.executeQuery(sql)) {
                    buying_concern = Table.read().db(results, "buying_concern");
                    System.out.println(buying_concern.structure());
                }
            }

        }

        // pie chart
        PieTrace trace = carspec.pie(buying_concern, "buying");
        PieTrace trace2 = carspec.pie(buying_concern, "maint");
        PieTrace trace3 = carspec.pie(buying_concern, "acceptability");
        Layout layout = Layout.builder().title("Distribution of buying").build();
        Layout layout2 = Layout.builder().title("Distribution of maintenance").build();
        Layout layout3 = Layout.builder().title("Distribution of acceptability").build();
        Plot.show(new Figure(layout, trace));
        Plot.show(new Figure(layout2, trace2));
        Plot.show(new Figure(layout3, trace3));

        // histogram for buying
        Layout Hlayout = Layout.builder()
                .title("Distribution of buying concern")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        TableSliceGroup groups = buying_concern.splitOn("buying");
        Table t11 = groups.get(0).asTable();
        Table t12 = groups.get(1).asTable();
        Table t13 = groups.get(2).asTable();
        Table t14 = groups.get(3).asTable();
        HistogramTrace trace11 = Buyinghistogram(t11, "buying", 0);
        HistogramTrace trace12 = Buyinghistogram(t12, "buying", 1);
        HistogramTrace trace13 = Buyinghistogram(t13, "buying", 2);
        HistogramTrace trace14 = Buyinghistogram(t14, "buying", 3);
        Plot.show(new Figure(Hlayout, trace11, trace12, trace13, trace14));

        // histogram for maintenance
        TableSliceGroup groups2 = buying_concern.splitOn("maint");
        Table t21 = groups2.get(0).asTable();
        Table t22 = groups2.get(1).asTable();
        Table t23 = groups2.get(2).asTable();
        Table t24 = groups2.get(3).asTable();
        HistogramTrace trace21 = Mainthistogram(t21, "maint", 0);
        HistogramTrace trace22 = Mainthistogram(t22, "maint", 1);
        HistogramTrace trace23 = Mainthistogram(t23, "maint", 2);
        HistogramTrace trace24 = Mainthistogram(t24, "maint", 3);
        Plot.show(new Figure(Hlayout, trace21, trace22, trace23, trace24));

        // histogram for acceptability
        TableSliceGroup groups3 = buying_concern.splitOn("acceptability");
        Table t31 = groups3.get(0).asTable();
        Table t32 = groups3.get(1).asTable();
        Table t33 = groups3.get(2).asTable();
        Table t34 = groups3.get(3).asTable();
        HistogramTrace trace31 = Accephistogram(t31, "acceptability", 0);
        HistogramTrace trace32 = Accephistogram(t32, "acceptability", 1);
        HistogramTrace trace33 = Accephistogram(t33, "acceptability", 2);
        HistogramTrace trace34 = Accephistogram(t34, "acceptability", 3);
        Plot.show(new Figure(Hlayout, trace31, trace32, trace33, trace34));

        // bar chart
        Table summaryTable = buying_concern.summarize("buying", "maint", "acceptability", sum).by("acceptability");

        Layout Blayout = Layout.builder()
                .title("Buying Concern")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        String[] numberColNames = { "SUM [buying]", "SUM [maint]", "SUM [acceptability]" };
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

    private static HistogramTrace Buyinghistogram(Table t, String column, int i) {
        String[] name = { "LOW", "MEDIUM", "HIGH", "VERY HIGH" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00", "#FFA500" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

    public static HistogramTrace Mainthistogram(Table t, String column, int i) {
        String[] name = { "LOW", "MEDIUM", "HIGH", "VERY HIGH" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00", "#FFA500" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

    public static HistogramTrace Accephistogram(Table t, String column, int i) {
        String[] name = { "UNACCEPTABLE", "ACCEPTABLE", "VERY GOOD", "GOOD" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00", "#FFA500" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

}
