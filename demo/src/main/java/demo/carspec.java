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

public class carspec {

    static Connection connection = null;
    static String databasename = "";
    static String DB_URL = "jdbc:mysql://localhost:3306/sys";

    static String username = "root";
    static String password = "401111";

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, username, password);

        // car_spec
        Table car_spec = null;
        {
            try (Statement stmt = connection.createStatement()) {
                String sql = "SELECT * FROM car_spec";
                try (ResultSet results = stmt.executeQuery(sql)) {
                    car_spec = Table.read().db(results, "car_spec");
                    System.out.println(car_spec.structure());
                }
            }

        }

        // pie chart
        PieTrace trace = pie(car_spec, "doors");
        PieTrace trace2 = pie(car_spec, "personas");
        PieTrace trace3 = pie(car_spec, "lug_boot");
        Layout layout = Layout.builder().title("Number of doors").build();
        Layout layout2 = Layout.builder().title("Number of personas").build();
        Layout layout3 = Layout.builder().title("Number of lug_boot").build();
        Plot.show(new Figure(layout, trace));
        Plot.show(new Figure(layout2, trace2));
        Plot.show(new Figure(layout3, trace3));

        // histogram for doors
        Layout Hlayout = Layout.builder()
                .title("Distribution of car specifications")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        TableSliceGroup groups = car_spec.splitOn("doors");
        Table t11 = groups.get(0).asTable();
        Table t12 = groups.get(1).asTable();
        Table t13 = groups.get(2).asTable();
        Table t14 = groups.get(3).asTable();
        HistogramTrace trace11 = Doorshistogram(t11, "doors", 0);
        HistogramTrace trace12 = Doorshistogram(t12, "doors", 1);
        HistogramTrace trace13 = Doorshistogram(t13, "doors", 2);
        HistogramTrace trace14 = Doorshistogram(t14, "doors", 3);
        Plot.show(new Figure(Hlayout, trace11, trace12, trace13, trace14));

        // histogram for personas
        TableSliceGroup groups2 = car_spec.splitOn("personas");
        Table t21 = groups2.get(0).asTable();
        Table t22 = groups2.get(1).asTable();
        Table t23 = groups2.get(2).asTable();
        HistogramTrace trace21 = Personashistogram(t21, "personas", 0);
        HistogramTrace trace22 = Personashistogram(t22, "personas", 1);
        HistogramTrace trace23 = Personashistogram(t23, "personas", 2);
        Plot.show(new Figure(Hlayout, trace21, trace22, trace23));

        // histogram for lug_boot
        TableSliceGroup groups3 = car_spec.splitOn("lug_boot");
        Table t31 = groups3.get(0).asTable();
        Table t32 = groups3.get(1).asTable();
        Table t33 = groups3.get(2).asTable();
        HistogramTrace trace31 = Lugboothistogram(t31, "lug_boot", 0);
        HistogramTrace trace32 = Lugboothistogram(t32, "lug_boot", 1);
        HistogramTrace trace33 = Lugboothistogram(t33, "lug_boot", 2);
        Plot.show(new Figure(Hlayout, trace31, trace32, trace33));

        // bar chart
        Table summaryTable = car_spec.summarize("doors", "personas", "lug_boot", sum).by("lug_boot");

        Layout Blayout = Layout.builder()
                .title("Car Specification")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        String[] numberColNames = { "SUM [doors]", "SUM [personas]", "SUM [lug_boot]" };
        String[] colors = { "#85144b", "#FF4136", "463E3F" };

        Trace[] traces = new Trace[3];
        for (int i = 0; i < 3; i++) {
            String name = numberColNames[i];
            BarTrace Btrace = BarTrace
                    .builder(summaryTable.categoricalColumn("lug_boot"), summaryTable.numberColumn(name))
                    .orientation(BarTrace.Orientation.VERTICAL)
                    .marker(Marker.builder().color(colors[i]).build())
                    .showLegend(true)
                    .name(name)
                    .build();
            traces[i] = Btrace;
        }
        Plot.show(new Figure(Blayout, traces));

    }

    public static PieTrace pie(Table t, String column) {
        t = t.countBy(t.categoricalColumn(column));
        return PieTrace.builder(t.categoricalColumn(column), t.numberColumn("Count")).build();
    }

    private static HistogramTrace Doorshistogram(Table t, String column, int i) {
        String[] name = { "2 doors", "3 doors", "4 doors", "5 doors" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00", "#FFA500" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

    private static HistogramTrace Personashistogram(Table t, String column, int i) {
        String[] name = { "2 personas", "4 personas", "5 personas" };
        String[] colors = { "#FF4136", "#7FDBFF", "FFFF00" };

        HistogramTrace trace = HistogramTrace.builder(t.nCol(column))
                .name(name[i])
                .opacity(.75)
                .nBinsY(24)
                .marker(Marker.builder().color(colors[i]).build())
                .build();
        return trace;

    }

    private static HistogramTrace Lugboothistogram(Table t, String column, int i) {
        String[] name = { "SMALL", "MEDIUM", "BIG" };
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
