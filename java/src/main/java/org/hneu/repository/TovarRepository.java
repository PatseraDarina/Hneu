package org.hneu.repository;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.hneu.domain.DimTovar;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TovarRepository {

    String[] hlebName = new String[] {"Baton", "Bulka molochnaya", "Cherniy hleb", "Keks vanilnuy"};

    private void fill(List<DimTovar> tovars) {
        double price;
        for (int i = 0; i < 10_000; i++) {
            DimTovar dimTovar = new DimTovar();
            dimTovar.setId(i);
            price = new Random().nextInt(20) + 1;
            dimTovar.setPurchasePrice(price);
            dimTovar.setPrice(price + 5);
            dimTovar.setTovar(hlebName[new Random().nextInt(hlebName.length - 1)]);
            tovars.add(dimTovar);
        }
    }

    private void addAll(JavaSparkContext jsc) {
        List<DimTovar> tovars = new LinkedList<>();
        fill(tovars);
        JavaRDD<DimTovar> tovarJavaRDD = jsc.parallelize(tovars);
        SQLContext sqlContext = new SQLContext(jsc);
        Dataset dataset = sqlContext.createDataFrame(tovarJavaRDD, DimTovar.class);
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
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        new TovarRepository().addAll(jsc);
    }
}
