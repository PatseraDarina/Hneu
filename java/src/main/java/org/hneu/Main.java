package org.hneu;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import org.hneu.visualization.AppForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private SparkSession sparkSession;
    private JavaSparkContext jsc;

    public Main() {
        sparkSession = SparkSession.builder()
                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.tovars")
                .getOrCreate();

        jsc = new JavaSparkContext(sparkSession.sparkContext());
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

    private Dataset<Row> getGroupData() {
        JavaMongoRDD<Document> javaRDDTovar = MongoSpark.load(jsc);
        JavaMongoRDD<Document> javaRDDData = (JavaMongoRDD<Document>) getDatas(jsc, sparkSession);
        JavaMongoRDD<Document> javaRDDSales = (JavaMongoRDD<Document>) getSales(jsc, sparkSession);

        Dataset<Row> sales = javaRDDSales.toDF();
        Dataset<Row> tovars = javaRDDTovar.toDF();
        Dataset<Row> datas = javaRDDData.toDF();

        List<String> joinTovar = new ArrayList<>();
        joinTovar.add("tovarId");
        List<String> joinData = new ArrayList<>();
        joinData.add("dataId");

        Dataset<Row> joinedSalesTovars = tovars.join(sales,scala.collection.JavaConversions.asScalaBuffer(joinTovar));
        Dataset<Row> joinedSalesTovarsDatas = datas.join(joinedSalesTovars, scala.collection.JavaConversions.asScalaBuffer(joinData));

        joinedSalesTovarsDatas = joinedSalesTovarsDatas.groupBy("year", "tovar").sum("quantity");
        return joinedSalesTovarsDatas;
    }

    public static void main(String[] args) {
        Main main = new Main();
        Dataset<Row> dataset = main.getGroupData();
        new AppForm(dataset);
//
//        TimeSeriesChart timeSeriesChart = new TimeSeriesChart("Hleb", "Product sales statistic");
//        timeSeriesChart.pack();
//        RefineryUtilities.centerFrameOnScreen(timeSeriesChart);
//        timeSeriesChart.setVisible(true);

//        Log.getInstance().addTarget(new PrintStreamLogTarget());
//        final PieChart demo = new PieChart("Product sales pie chart");
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
    }
}

