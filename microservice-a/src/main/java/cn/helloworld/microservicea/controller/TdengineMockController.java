package cn.helloworld.microservicea.controller;

import cn.helloworld.microservicea.service.feign.TdengineService;
import com.alibaba.fastjson.JSON;
import com.taosdata.jdbc.TSDBConnection;
import com.taosdata.jdbc.TSDBDriver;
import lombok.AllArgsConstructor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author zhangkai
 */
@AllArgsConstructor
@RequestMapping("/tdengine")
@RestController
public class TdengineMockController {


    private final DataSource tdengineDataSource;

    private final TdengineService tdengineService;

    @GetMapping("/mock")
    private Object mock() throws SQLException, ClassNotFoundException {
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://47.75.196.191:6041/db?user=dev&password=tddevP@ssw0RD";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        conn.createStatement().executeQuery("select * from db.tb");
        ResultSet resultSet = tdengineDataSource.getConnection().createStatement().executeQuery("select * from tb");

        resultSet = tdengineDataSource.getConnection().prepareStatement("select * from tb").executeQuery();

        Timestamp ts = null;
        int temperature = 0;
        float humidity = 0;
        while(resultSet.next()){
            ts = resultSet.getTimestamp(1);
            temperature = resultSet.getInt(2);
            humidity = resultSet.getFloat("humidity");
            System.out.printf("%s, %d, %s\n", ts, temperature, humidity);
        }






// Create a QueryRunner that will use connections from
// the given DataSource
        QueryRunner run = new QueryRunner(tdengineDataSource);

// Execute the query and get the results back from the handler
        Map<String,Object> resultMap = run.query(
                "select * from tb where 1 = ?", (ResultSet rs)->{
                    if (!rs.next()) {
                        return null;
                    }
                    Map<String,Object> rsMap = new HashMap<>(9);

                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i =0;i<meta.getColumnCount();i++){
                        rsMap.put(meta.getColumnName(i),rs.getObject(i));
                    }
                    return rsMap;
                },1);

        System.out.printf(JSON.toJSONString(resultMap));


       return  tdengineService.restSql("select * from tb");

    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://47.75.196.191:6041/db?user=dev&password=tddevP@ssw0RD";
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");
        Connection conn = DriverManager.getConnection(jdbcUrl, connProps);


        Statement stmt = conn.createStatement();
// create database
        stmt.executeUpdate("create database if not exists db");
// use database
        stmt.executeUpdate("use db");
// create table
        stmt.executeUpdate("create table if not exists tb (ts timestamp, temperature int, humidity float)");

        // insert data
        int affectedRows = stmt.executeUpdate("insert into tb values(now, 23, 10.3) (now + 1s, 20, 9.3)");

        ResultSet resultSet = stmt.executeQuery("select * from tb");
        Timestamp ts = null;
        int temperature = 0;
        float humidity = 0;
        while(resultSet.next()){
            ts = resultSet.getTimestamp(1);
            temperature = resultSet.getInt(2);
            humidity = resultSet.getFloat("humidity");
            System.out.printf("%s, %d, %s\n", ts, temperature, humidity);
        }


    }
}
