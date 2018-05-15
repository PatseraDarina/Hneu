package org.hneu.repository;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.hneu.domain.FactSale;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DataRepository {


    private void fill(List<FactSale> sales) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String startDate = "2010-02-12";
        String endDate = "2017-10-3";
        calendar.setTime(sdf.parse(startDate));
        calendar.add(Calendar.DATE, 1);
        Date newDate = calendar.getTime();

        int startDay = 10;
        int startMonth;
        int startYear = 2010;

        int endDay = 5;
        int endMonth;
        int endYear = 2018;
        String date;

        for (int i = startYear; i < endYear; i++) {
            for (int j = 1; j < endDay; j++) {
                startDay += j;
            }
        }

        for (int i = 0; i < 10_000; i++) {
            FactSale factSale = new FactSale();
            factSale.setDataId(new Random().nextInt(10_000));
            factSale.setTovarId(new Random().nextInt(10_000));
            factSale.setManufacturerId(new Random().nextInt(10_000));
            factSale.setCost(new Random().nextInt());
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
}
