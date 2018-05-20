package org.hneu.visualization;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import scala.collection.JavaConversions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAnalysing extends JFrame {
    private JPanel panel1;
    private JTable hlebTable;
    private JButton lineChartBtn;
    private JButton pieChartBtn;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JScrollPane jScrollPane;

    public ProductAnalysing() {
        fillTable();
        this.setSize(1000, 540);
        this.add(new JScrollPane(hlebTable));
        this.add(pieChartBtn);
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

    private void fillTable() {
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
        Dataset<Row> datas = javaRDDData.toDF();

        List<String> joinTovar = new ArrayList<>();
        joinTovar.add("tovarId");
        List<String> joinData = new ArrayList<>();
        joinData.add("dataId");

        Dataset<Row> joinedSalesTovars = tovars.join(sales, JavaConversions.asScalaBuffer(joinTovar));
        Dataset<Row> joinedSalesTovarsDatas = datas.join(joinedSalesTovars, JavaConversions.asScalaBuffer(joinData));

        joinedSalesTovarsDatas = joinedSalesTovarsDatas.groupBy("year", "tovar").sum("quantity");
        //DefaultTableModel model = (DefaultTableModel) hlebTable.getModel();
        List<Row> rdd = joinedSalesTovarsDatas.javaRDD().collect();
        String[] columnNames = new String[]{"Year", "Product", "Quantity"};
        Object[][] data = new Object[rdd.size()][3];
        //model.addRow(new Object[]{"Year", "Product", "Quantity"});
        for (int i = 0; i < rdd.size(); i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = rdd.get(i).get(j);
            }
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        ;
        hlebTable.setModel(model);
    }

    public static void main(String[] args) {
        new ProductAnalysing().setVisible(true);
    }


}
