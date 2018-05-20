package org.hneu.visualization;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AppForm extends JFrame {
    private JTable hlebTable;
    private JButton btnTimeSeries;
    private JButton btnPie;
    private JPanel panel;
    private JFormattedTextField tfFrom;
    private JFormattedTextField tfTo;
    private Dataset<Row> dataset;

    public AppForm(Dataset<Row> dataset) {
        this.dataset = dataset;
        this.tfFrom.setValue("2000");
        this.tfTo.setValue("2018");
        this.setTitle("Hleb");
        fillTable();
        setContentPane(panel);
        setSize(500, 460);
        setVisible(true);
        btnTimeSeries.addActionListener(e -> {
            int yearFrom = Integer.parseInt(tfFrom.getText());
            int yearTo = Integer.parseInt(tfTo.getText());
            String yearsTitle = yearFrom + "-" + yearTo;

            TimeSeriesChart timeSeriesChart = new TimeSeriesChart("Hleb", "Product sales statistic " + yearsTitle,
                    dataset, yearFrom, yearTo);
            timeSeriesChart.pack();
            RefineryUtilities.centerFrameOnScreen(timeSeriesChart);
            timeSeriesChart.setVisible(true);
        });
        btnPie.addActionListener(e -> {
            int yearFrom = Integer.parseInt(tfFrom.getText());
            int yearTo = Integer.parseInt(tfTo.getText());
            String yearsTitle = yearFrom + "-" + yearTo;

            PieChart demo = new PieChart("Hleb", "Product sales pie chart " + yearsTitle, dataset,
                    yearFrom, yearTo);
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setVisible(true);
        });
    }

    private void fillTable() {
        List<Row> rdd = dataset.javaRDD().collect();
        String[] columnNames = new String[]{"Year", "Product", "Quantity"};
        Object[][] data = new Object[rdd.size()][3];
        for (int i = 0; i < rdd.size(); i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = rdd.get(i).get(j);
            }
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        hlebTable.setModel(model);
    }
}
