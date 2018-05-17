package org.hneu.domain;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FactSale implements Serializable {

    private int tovarId;
    private int dataId;
    private int manufacturerId;
    private int cost;
    private int quantity;

    public int getTovarId() {
        return tovarId;
    }

    public void setTovarId(int tovarId) {
        this.tovarId = tovarId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.datas")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.datas")
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        Dataset<Row> dfc = MongoSpark.load(jsc).toDF();
        JavaMongoRDD<Document> documentJavaMongoRDD = MongoSpark.load(jsc);
        //JavaRDD<Row> rdd = dfc.javaRDD();
        JavaRDD<Document> filterRdd = documentJavaMongoRDD.filter(row ->
                ((int)row.get("year") > 2015) && ((int)row.get("year") < 2017)
        );

        filterRdd.foreach(document -> System.out.println(document.toString()));
    }
}
