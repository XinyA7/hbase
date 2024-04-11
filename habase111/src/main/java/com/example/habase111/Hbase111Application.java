package com.example.habase111;

import net.minidev.json.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.Name;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping(path = "/api")
public class Hbase111Application {

    public static Connection connection;
    public static Admin admin;

    public static void main(String[] args) {
        SpringApplication.run(Hbase111Application.class, args);
    }

    @PostMapping(path = "/getMovie")
    public JSONObject getMovies(@RequestBody Map<String, String> DataInfo) throws IOException{
        int pageSizeNum =  Integer.parseInt(DataInfo.get("pageSize"));
        Table table = connection.getTable(TableName.valueOf("movies"));
        Table ratingTable = connection.getTable(TableName.valueOf("ratings"));
        Filter filter = new PageFilter(pageSizeNum + 1);
        Scan scan = new Scan();
        scan.setFilter(filter);
        scan.setStartRow(DataInfo.get("startRow").getBytes());
        ResultScanner result = table.getScanner(scan);
        String newStartRow = "1";
        Map<String, Object> map = new HashMap<String, Object>();
        for(Result r:result){
            Map<String, Object> mapInfo = new HashMap<String, Object>();
            String movieId = new String(r.getRow());
            String title = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("title")));
            String genres = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("genres")));

            SingleColumnValueFilter ratingFilter = new SingleColumnValueFilter(
                    Bytes.toBytes("ratingInfo"),
                    Bytes.toBytes("movieId"),
                    CompareFilter.CompareOp.EQUAL,
                    Bytes.toBytes(movieId)
            );
            Scan Ratingscan = new Scan();
            Ratingscan.setFilter(ratingFilter);
            ResultScanner ratingRs = ratingTable.getScanner(Ratingscan);
            int count=0;
            float sum=0,avg=0;
            for(Result ratingR:ratingRs){
                String rating = Bytes.toString(ratingR.getValue(Bytes.toBytes("ratingInfo"),Bytes.toBytes("rating")));
                count += 1;
                sum += Float.parseFloat(rating);
            }
            avg = sum / count;

            mapInfo.put("rating", avg);
            mapInfo.put("title", title);
            mapInfo.put("genres", genres);

            map.put(movieId, mapInfo);
            newStartRow = new String(r.getRow());
        }
        if (map.size() > pageSizeNum){
            map.remove(newStartRow);
        }
        map.put("startRow", newStartRow);
        table.close();
        JSONObject json = new JSONObject(map);
        return json;
    }

    @PostMapping(path = "/SearchMovieByName")
    public Map<String, Object> SearchMovieByName(@RequestBody Map<String, String> DataInfo) throws IOException{
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapInfo = new HashMap<String, Object>();
        Table table = connection.getTable(TableName.valueOf("movies"));
        Table ratingTable = connection.getTable(TableName.valueOf("ratings"));
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes("movieInfo"),
                Bytes.toBytes("title"),
                CompareFilter.CompareOp.EQUAL,
                new SubstringComparator(DataInfo.get("name"))
        );
        Scan scan = new Scan();
        scan.setFilter(filter);
        ResultScanner rs = table.getScanner(scan);
        for(Result r:rs){
            String movieId = new String(r.getRow());
            String title = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("title")));
            String genres = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("genres")));

            SingleColumnValueFilter ratingFilter = new SingleColumnValueFilter(
                    Bytes.toBytes("ratingInfo"),
                    Bytes.toBytes("movieId"),
                    CompareFilter.CompareOp.EQUAL,
                    Bytes.toBytes(movieId)
            );
            Scan Ratingscan = new Scan();
            Ratingscan.setFilter(ratingFilter);
            ResultScanner ratingRs = ratingTable.getScanner(Ratingscan);
            int count=0;
            float sum=0,avg=0;
            for(Result ratingR:ratingRs){
                String rating = Bytes.toString(ratingR.getValue(Bytes.toBytes("ratingInfo"),Bytes.toBytes("rating")));
                count += 1;
                sum += Float.parseFloat(rating);
            }
            avg = sum / count;
            mapInfo.put("rating", avg);
            mapInfo.put("title", title);
            mapInfo.put("genres", genres);
            map.put(movieId, mapInfo);
        }
        table.close();

        return map;
    }
    @PostMapping(path = "/SearchMovieByGenres")
    public Map<String, Object> SearchMovieByRating(@RequestBody Map<String, String> DataInfo) throws IOException{
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapInfo = new HashMap<String, Object>();
        Table table = connection.getTable(TableName.valueOf("movies"));
        Table nameTable = connection.getTable(TableName.valueOf("movies"));
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes("movieInfo"),
                Bytes.toBytes("genres"),
                CompareFilter.CompareOp.EQUAL,
                new SubstringComparator(DataInfo.get("genres"))
        );
        Scan scan = new Scan();
        scan.setFilter(filter);
        ResultScanner rs = table.getScanner(scan);
        for(Result r:rs){
            String movieId = new String(r.getRow());
            String title = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("title")));
            String genres = Bytes.toString(r.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("name")));

            SingleColumnValueFilter nameFilter = new SingleColumnValueFilter(
                    Bytes.toBytes("ratingInfo"),
                    Bytes.toBytes("movieId"),
                    CompareFilter.CompareOp.EQUAL,
                    Bytes.toBytes(movieId)
            );
            Scan Namescan = new Scan();
            Namescan.setFilter(nameFilter);
            ResultScanner nameRs = nameTable.getScanner(Namescan);
            int count=0;
            float sum=0,avg=0;
            for(Result ratingR:nameRs){
                String name = Bytes.toString(ratingR.getValue(Bytes.toBytes("movieInfo"),Bytes.toBytes("name")));
                count += 1;
                sum += Float.parseFloat(name);
            }
            avg = sum / count;
            mapInfo.put("rating", avg);
            mapInfo.put("title", title);
            mapInfo.put("genres", genres);
            map.put(movieId, mapInfo);
        }
        table.close();

        return map;
    }

    @Component
    public class Run{
        @PostConstruct
        public static void init() throws IOException {
            Configuration config = HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum", "192.168.48.130");
            config.set("hbase.zookeeper.property.clientPort", "2181");
            connection = ConnectionFactory.createConnection(config);
            admin = connection.getAdmin();
        }

        @PreDestroy
        public static void close(){
            try{
                if(admin != null){
                    admin.close();
                }
                if(null != connection){
                    connection.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
