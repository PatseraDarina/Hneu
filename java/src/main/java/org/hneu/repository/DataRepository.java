package org.hneu.repository;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.hneu.domain.DimData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataRepository {

    private void fill(List<DimData> dates) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        Date startDate = sdf.parse("31-08-2010 10:20:56");
        Date endDate = sdf.parse("09-09-2017 10:20:56");
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int i = 0;
        for (LocalDateTime dateTime = startDateTime; dateTime.isBefore(endDateTime); dateTime = dateTime.plusDays(1)) {
            DimData dimData = new DimData();
            dimData.setDate(dateTime.toString());
            dimData.setDayMonth(dateTime.getDayOfMonth());
            dimData.setNumMonth(dateTime.getMonthValue());
            dimData.setYear(dateTime.getYear());
            dimData.setDataId(i++);
            dates.add(dimData);
        }
        DimData dimData = new DimData();
        dates.add(dimData);

    }

    private void addAll(JavaSparkContext jsc) throws ParseException {
        List<DimData> datas = new LinkedList<>();
        fill(datas);
        JavaRDD<DimData> dataJavaRDD = jsc.parallelize(datas);
        SQLContext sqlContext = new SQLContext(jsc);
        Dataset dataset = sqlContext.createDataFrame(dataJavaRDD, DimData.class);
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
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/hlebDB.datas")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/hlebDB.datas")
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        new DataRepository().addAll(jsc);
    }
}
