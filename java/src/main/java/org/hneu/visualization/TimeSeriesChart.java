package org.hneu.visualization;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TimeSeriesChart extends JFrame {

    private Dataset<Row> joinedSalesTovarsDatas;

    public TimeSeriesChart(String applicationTitle, String chartTitle, Dataset<Row> joinedSalesTovarsDatas, int yearFrom, int yearTo) {
        super(applicationTitle);
        this.joinedSalesTovarsDatas = joinedSalesTovarsDatas;
        // Create dataset
        XYDataset dataset = createDataset(yearFrom, yearTo);
        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                chartTitle, // Chart
                "Year", // X-Axis Label
                "Quantity", // Y-Axis Label
                dataset, true, true, true);

        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(255,228,196));

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private XYDataset createDataset(int yearFrom, int yearTo) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries hleb = new TimeSeries( "Cherniy hleb" );
        TimeSeries bulka = new TimeSeries( "Bulka" );
        TimeSeries baton = new TimeSeries( "Baton" );

        List<Row> rdd = joinedSalesTovarsDatas.javaRDD().collect();

        for (Row row : rdd) {
            if ((Integer.parseInt(row.get(0).toString()) >= yearFrom) && (Integer.parseInt(row.get(0).toString()) <= yearTo)) {
                if (row.get(1).equals("Cherniy hleb")) {
                    hleb.add(new Year((Integer) row.get(0)), Double.parseDouble(row.get(2).toString()));
                    System.out.println("Added hleb" + Double.parseDouble(row.get(2).toString()));
                }
                if (row.get(1).equals("Bulka molochnaya")) {
                    bulka.add(new Year((Integer) row.get(0)), Double.parseDouble(row.get(2).toString()));
                    System.out.println("Added bulka" + Double.parseDouble(row.get(2).toString()));

                }
                if (row.get(1).equals("Baton")) {
                    baton.add(new Year((Integer) row.get(0)), Double.parseDouble(row.get(2).toString()));
                    System.out.println("Added baton" + Double.parseDouble(row.get(2).toString()));
                }
            }
        }

        dataset.addSeries( hleb );
        dataset.addSeries( bulka );
        dataset.addSeries( baton );

        return dataset;
    }
}
