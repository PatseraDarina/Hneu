package org.hneu.visualization;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

public class PieChart extends ApplicationFrame {

    private Dataset<Row> joinedDataset;

    /**
     * Default constructor.
     *
     * @param title  the frame title.
     */
    public PieChart(String appTitle, String title, Dataset<Row> joinedDataset, int yearFrom, int yearTo) {
        super(appTitle);
        PieDataset dataset = createDataset(joinedDataset, yearFrom, yearTo);

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart(
                title,  // chart title
                dataset,             // dataset
                true,               // include legend
                true,
                false
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(255,228,196));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 204, 204));
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));
        plot.setNoDataMessage("No data available");

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @param joinedDataset joined data of products.
     *
     * @return A sample dataset.
     */
    private PieDataset createDataset(Dataset<Row> joinedDataset, int yearFrom, int yearTo) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<Row> rdd = joinedDataset.javaRDD().collect();
        Double hlebAmount = 0d;
        Double bulkaAmount = 0d;
        Double batonAmount = 0d;
        String hleb = "Cherniy hleb";
        String bulka = "Bulka molochnaya";
        String baton = "Baton";

        for (Row row : rdd) {
            if ((Integer.parseInt(row.get(0).toString()) >= yearFrom) && (Integer.parseInt(row.get(0).toString()) <= yearTo)) {
                if (row.get(1).equals("Cherniy hleb")) {
                    hlebAmount += Integer.parseInt(row.get(2).toString());
                }
                if (row.get(1).equals("Bulka molochnaya")) {
                    bulkaAmount += Integer.parseInt(row.get(2).toString());
                }
                if (row.get(1).equals("Baton")) {
                    batonAmount += Integer.parseInt(row.get(2).toString());
                }
            }
        }
        Double generalAmount = hlebAmount + bulkaAmount + batonAmount;
        dataset.setValue(hleb, calculatePercent(generalAmount, hlebAmount));
        dataset.setValue(bulka, calculatePercent(generalAmount, bulkaAmount));
        dataset.setValue(baton, calculatePercent(generalAmount, batonAmount));
        return dataset;
    }

    private Double calculatePercent(Double generalAmount, Double partialAmount) {
        return partialAmount * 100 / generalAmount;
    }
}
