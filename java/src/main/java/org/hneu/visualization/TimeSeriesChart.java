package org.hneu.visualization;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeriesChart extends ApplicationFrame {

    public static String APP_NAME = "TIME_SERIES_EXAMPLE";
    public static String APP_MASTER = "local";

    public TimeSeriesChart(final String title) {
        super(title);
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
        chartPanel.setMouseZoomable(true, false);
        // chartPanel.setB
        setContentPane(chartPanel);
    }

    private JavaRDD<Document> getDatas(JavaSparkContext jscTovar, SparkSession sparkSession) {
        Map<String, String> readDatas = new HashMap<>();
        readDatas.put("collection", "datas");
        readDatas.put("readPreference.name", "secondaryPreferred");
        ReadConfig readConfig = ReadConfig.create(jscTovar).withOptions(readDatas);

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jscData = new JavaSparkContext(sparkSession.sparkContext());
        return MongoSpark.load(jscData, readConfig);
    }

    private JavaRDD<Document> getSales(JavaSparkContext jsc, SparkSession sparkSession) {
        Map<String, String> readDatas = new HashMap<>();
        readDatas.put("collection", "sales");
        readDatas.put("readPreference.name", "secondaryPreferred");
        ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readDatas);

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jscData = new JavaSparkContext(sparkSession.sparkContext());
        return MongoSpark.load(jscData, readConfig);
    }

    private XYDataset createDataset() {
        final TimeSeries series = new TimeSeries("Jan-Dec 2015");
        /* Create the SparkSession.
         * If config arguments are passed from the command line using --conf,
         * parse args for the values to set.
         */

        SparkSession sparkSession = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(sparkSession.sparkContext());

        JavaMongoRDD<Document> javaRDDTovar = MongoSpark.load(jsc);
        JavaMongoRDD<Document> javaRDDData = (JavaMongoRDD<Document>) getDatas(jsc, sparkSession);
        JavaMongoRDD<Document> javaRDDSales = (JavaMongoRDD<Document>) getSales(jsc, sparkSession);

        Dataset<Row> sales = javaRDDSales.toDF();
        Dataset<Row> tovars = javaRDDTovar.toDF();

        List<String> joinClolist = new ArrayList<>();
        joinClolist.add("tovarId");

        Dataset<Row> joinedData = tovars.join(sales,scala.collection.JavaConversions.asScalaBuffer(joinClolist));

        joinedData.show();

       // javaRDDSales.foreach(document -> System.out.println(document.toString()));
        //JavaRDD<Row> rdd = dfc.javaRDD();
        JavaRDD<Document> filterRddData = javaRDDData.filter(row -> {
            if ((int) row.get("year") == 2015)
                return true;
            else
                if ((int) row.get("year") == 2017)
            return true;
        else
            return false;
        });
        JavaRDD<Document> filterTovar = javaRDDTovar.filter(row ->
               row.get("tovar").equals("Baton")
        );

//        filterTovar.foreach(document -> System.out.println(document.toString()));
//        filterRddData.foreach(document -> System.out.println(document.toString()));

        JavaPairRDD<String, Iterable<Document>> tovarIterableJava = javaRDDTovar.groupBy(document -> document.get("tovar").toString());

        tovarIterableJava.foreach(stringIterableTuple2 -> System.out.println(stringIterableTuple2.toString()));

//        List<Document> listData = filterRdd.collect();
//        List<Document> listTovar = filterTovar.collect();
//        for (Document hleb : listTovar) {
//            for (Document data : listData) {
//                if (hleb.get("tovar"))
//                //series.addOrUpdate(new Month(i, (Integer) document.get("year")), (Integer) document.get("dayMonth"));
//                series.addOrUpdate(new Month(i, (Integer) document.get("year")), new Random().nextInt(10));
//            }
//        }
        DefaultXYDataset ds = new DefaultXYDataset();
        List<Document> listTovar = filterRddData.collect();
        int j = 0;
        for (Document document : listTovar) {
            double[][] seris = new double[2][13];
            //List<String> items = document.getList(0);
            for(int i = 1 ; i <= 12 ; i++) {
                seris[0][i] = (double)i;
                seris[1][i] = Double.parseDouble(document.get("year").toString());
            }
            ds.addSeries("Series-" + j, seris);
            j = j + 1;
        }
        return ds;
        //return new TimeSeriesCollection(series);
    }

    private JFreeChart createChart(final XYDataset dataset) {
        return ChartFactory.createTimeSeriesChart("TimeSeries -Temperatures vs Months (2015) ", "Months (2015)",
                "Avg. Temperature", dataset, false, false, false);
    }

    public static void main(final String[] args) {
        final String title = "Time Series Management";
        final TimeSeriesChart demo = new TimeSeriesChart(title);
        demo.pack();
        RefineryUtilities.positionFrameRandomly(demo);
        demo.setVisible(true);
    }
}
