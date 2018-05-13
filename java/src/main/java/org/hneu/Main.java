package org.hneu;


import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.spark.MongoSpark;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.bson.BSONObject;
import org.jfree.chart.ChartFactory;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import java.util.LinkedList;
import java.util.List;

public class Main extends ApplicationFrame {

    public Main(String title) {
        super(title);
    }

    private static void fill(List<DimManufacturer> manufacturers) {
        for (int i =0; i < 100_000; i++) {
            DimManufacturer dimManufacturer = new DimManufacturer();
            dimManufacturer.setManufacturer("Х/з \"Салтівський\" " + i);
            dimManufacturer.setDimManufacturer(i);
            manufacturers.add(dimManufacturer);
        }
    }

    private static XYDataset createDataSet(JavaRDD<Row> filterRdd) {
        DefaultXYDataset ds = new DefaultXYDataset();

        ChartFactory.createTimeSeriesChart("TimeSeries title", "AxisLabel",
                "valueAxis", new DefaultXYDataset(), true, true, true);

        TimeSeries timeSeries = new TimeSeries("Jan-Dec2017");
        List<Row> filterList = filterRdd.collect();
        int j = 0;
        for (Row row : filterList) {
            double[][] series = new double[2][13];
            List<String> items = row.getList(0);
            for (int i = 1; i < 12; i++) {
                //timeSeries.add(new Month(i, Integer.parseInt(items.get(0))), new Double(items.get(i)));
                series[0][i] = (double)i;
                series[1][i] = new Double(items.get(i));
            }
            ds.addSeries("Series-" + j, series);
            j = j + 1;
        }
        return ds;
    }


        public static void main(String[] args) {

            System.setProperty("hadoop.home.dir", "D:\\ХНЕУ\\winutil");

            /* Create the SparkSession.
             * If config arguments are passed from the command line using --conf,
             * parse args for the values to set.
             */
            SparkSession spark = SparkSession.builder()
                    .master("local")
                    .appName("MongoSparkConnectorIntro")
                    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hleb.manufacturers")
                    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hleb.manufacturers")
                    .getOrCreate();

            // Create a JavaSparkContext using the SparkSession's SparkContext object
            JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

            // Set configuration options for the MongoDB Hadoop Connector.
            Configuration mongodbConfig = new Configuration();

            // MongoInputFormat allows us to read from a live MongoDB instance.
            // We could also use BSONFileInputFormat to read BSON snapshots.
            mongodbConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");

            // MongoDB connection string naming a collection to use.
            // If using BSON, use "mapred.input.dir" to configure the directory
            // where BSON files are located instead.
            mongodbConfig.set("spark.mongodb.input.uri", "mongodb://127.0.0.1/hleb.manufacturers");

            // Create an RDD backed by the MongoDB collection.
            JavaPairRDD<Object, BSONObject> documents = jsc.newAPIHadoopRDD(
                    mongodbConfig,            // Configuration
                    MongoInputFormat.class,   // InputFormat: read from a live cluster.
                    Object.class,             // Key class
                    BSONObject.class          // Value class
            );

            List<DimManufacturer> manufacturers = new LinkedList<>();

            //fill(manufacturers);

            JavaRDD<DimManufacturer> manufacturerJavaRDD = jsc.parallelize(manufacturers);

            SQLContext sqlContext = new SQLContext(jsc);
//
////            spark-submit —driver-library-path /home/ubuntu/hadoop-2.9.0/lib/native —class org.hneu.Main ./target/hneu-1.0-SNAPSHOT.jar
//
            Dataset dataset = sqlContext.createDataFrame(manufacturerJavaRDD, DimManufacturer.class);
            dataset.createOrReplaceTempView("manufacturers");
//
            //MongoSpark.save(dataset);

            //JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);
            Dataset<Row> dfc = MongoSpark.load(jsc).toDF();
            JavaRDD<Row> rdd = dfc.javaRDD();
            JavaRDD<Row> filterRdd = rdd.filter(row -> {
                if ("7".equals(row.getList(1).get(0).toString()))
                    return true;
                return false;
            });

            System.out.println(filterRdd.count());

            //JavaMongoRDD<Document> filterRdd = rdd.withPipeline(singletonList(Document.parse("{ $match: { dimManufacturer : 99998 } }")));

//            JFreeChart chart = ChartFactory.createTimeSeriesChaSrt("TimeSeries title", "AxisLabel",
//                    "valueAxis", new DefaultXYDataset(), true, true, true);
//            ChartPanel chartPanel = new ChartPanel(chart);
//            chartPanel.setPreferredSize(new Dimension(560, 370));
//            chartPanel.setMouseZoomable(true, false);
//            JFrame jFrame = new JFrame();
//            jFrame.setContentPane(chartPanel);
//            jFrame.setVisible(true);
        }
}

