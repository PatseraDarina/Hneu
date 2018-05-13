package org.hneu.repository;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.hneu.domain.FactSale;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SaleRepository implements Serializable {

    private void fill(List<FactSale> sales) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        int day;
        int month;
        int year;
        String date;

        for (int i = 0; i < 10_000; i++) {
//            day = new Random().nextInt(26) + 1;
//            month = new Random().nextInt(11) + 1;
//            year = new Random().nextInt(8) + 2010;
            //date = day + "/" + month + "/" + year;
            date = "10/02/2018";
            FactSale factSale = new FactSale();
            factSale.setDataId(new Random().nextInt(10_000));
            factSale.setTovarId(new Random().nextInt(10_000));
            factSale.setManufacturerId(new Random().nextInt(10_000));
            factSale.setDate(sdf.parse(date));
            sales.add(factSale);
        }
    }

    private void addAll(JavaSparkContext jsc) throws ParseException {
        List<FactSale> sales = new LinkedList<>();
        fill(sales);
        JavaRDD<FactSale> saleJavaRDD = jsc.parallelize(sales);
        SQLContext sqlContext = new SQLContext(jsc);
        Dataset dataset = sqlContext.createDataFrame(saleJavaRDD, FactSale.class);
        MongoSpark.save(dataset);
    }

    public static void main(String[] args) throws ParseException {
        System.setProperty("hadoop.home.dir", "D:\\Spark");

        /* Create the SparkSession.
         * If config arguments are passed from the command line using --conf,
         * parse args for the values to set.
         */
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.sales")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.sales")
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        new SaleRepository().addAll(jsc);
    }
}