package org.hneu;


import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.bson.BSONObject;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

public class Main {

        public static void main(String[] args) {
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
            mongodbConfig.set("spark.mongodb.input.uri", "mongodb://127.0.0.1/hleb.hlebCollect");

            // Create an RDD backed by the MongoDB collection.
            JavaPairRDD<Object, BSONObject> documents = jsc.newAPIHadoopRDD(
                    mongodbConfig,            // Configuration
                    MongoInputFormat.class,   // InputFormat: read from a live cluster.
                    Object.class,             // Key class
                    BSONObject.class          // Value class
            );

            DimManufacturer dimManufacturer = new DimManufacturer();
            dimManufacturer.setManufacturer("Х/з \"Салтівський\" ");

            List<DimManufacturer> manufacturers = new LinkedList<>();
            manufacturers.add(dimManufacturer);

            JavaRDD<DimManufacturer> manufacturerJavaRDD = jsc.parallelize(manufacturers);

            SQLContext sqlContext = new SQLContext(jsc);

//            spark-submit —driver-library-path /home/ubuntu/hadoop-2.9.0/lib/native —class org.hneu.Main ./target/hneu-1.0-SNAPSHOT.jar

            Dataset dataset = sqlContext.createDataFrame(manufacturerJavaRDD, DimManufacturer.class);
            dataset.createOrReplaceTempView("manufacturer");

            MongoSpark.save(dataset);

           // JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);

            //JavaRDD<Document> filterRDD = rdd.filter(document -> document.get("hlebCollect", Hleb.class).getKodTovaru() == 1);
            //System.out.println(filterRDD.count());

            jsc.close();
        }
}

