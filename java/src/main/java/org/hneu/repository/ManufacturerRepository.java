package org.hneu.repository;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.hneu.domain.DimManufacturer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ManufacturerRepository {

    String[] manufacturerNames = new String[]{"Saltovskiy", "Kulinichi"};

    private void fill(List<DimManufacturer> manufacturers) {
        for (int i =0; i < 10_000; i++) {
            DimManufacturer dimManufacturer = new DimManufacturer();
            dimManufacturer.setId(i);
            dimManufacturer.setManufacturer(manufacturerNames[new Random().nextInt(manufacturerNames.length)]);
            manufacturers.add(dimManufacturer);
        }
    }

    private void addAll(JavaSparkContext jsc) {
        List<DimManufacturer> manufacturers = new LinkedList<>();
        fill(manufacturers);
        JavaRDD<DimManufacturer> manufacturerJavaRDD = jsc.parallelize(manufacturers);
        SQLContext sqlContext = new SQLContext(jsc);
        Dataset dataset = sqlContext.createDataFrame(manufacturerJavaRDD, DimManufacturer.class);
        MongoSpark.save(dataset);
    }

    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir", "D:\\Spark");

        /* Create the SparkSession.
         * If config arguments are passed from the command line using --conf,
         * parse args for the values to set.
         */
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.manufacturers")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.manufacturers")
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        new ManufacturerRepository().addAll(jsc);
    }
}
