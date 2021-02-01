package cn.helloworld.microservicea.controller;

import cn.helloworld.microservicea.dao.tdengine.mapper.TbMapper;
import cn.helloworld.microservicea.service.feign.TdengineService;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.taosdata.jdbc.TSDBDriver;
import lombok.AllArgsConstructor;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * @author zhangkai
 */
@AllArgsConstructor
@RequestMapping("/tdengine")
@RestController
public class TdengineMockController {


    private final DataSource tdengineDataSource;

    private final TdengineService tdengineService;

    private final DataSourceTransactionManager dataSourceTransactionManager;

    private final TbMapper tenantInfoMapper;

    @GetMapping("/mock")
    private Object mock() throws SQLException, ClassNotFoundException {
        Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
        String jdbcUrl = "jdbc:TAOS-RS://k8s-node1:6041/db?user=dev&password=tddevP@ssw0RD";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        conn.createStatement().executeQuery("select * from db.tb");
        ResultSet resultSet = tdengineDataSource.getConnection().createStatement().executeQuery("select * from tb");

        PreparedStatement preparedStatement = tdengineDataSource.getConnection().prepareStatement("select * from tb");
        resultSet = preparedStatement.executeQuery();

        Timestamp ts = null;
        int temperature = 0;
        float humidity = 0;
        while(resultSet.next()){
            ts = resultSet.getTimestamp(1);
            temperature = resultSet.getInt(2);
            humidity = resultSet.getFloat("humidity");
            System.out.printf("%s, %d, %s\n", ts, temperature, humidity);
        }

    RSA rsa = new RSA();
     DataSource  devdataSource = dataSourceTransactionManager.getDataSource();

       preparedStatement =  devdataSource.getConnection().prepareStatement("delete from tenant_info where id = ?");
       preparedStatement.setBigDecimal(1,new BigDecimal(1));
       preparedStatement.executeUpdate();

    preparedStatement =  devdataSource.getConnection().prepareStatement("INSERT INTO tenant_info (account, pwd, id, private_key, public_key, tenant_id, tenant_name) VALUES (?, ?, ?, ?, ?, ?, ?)");
    preparedStatement.setString(1,"zhangkai");
        preparedStatement.setString(2,DigestUtil.md5Hex("zhangkai2021"));
        preparedStatement.setBigDecimal(3,new BigDecimal(1));
        preparedStatement.setString(4,rsa.getPrivateKeyBase64());
        preparedStatement.setString(5,rsa.getPublicKeyBase64());
        preparedStatement.setString(6,"zhangk");
        preparedStatement.setString(7,"泡泡熊");
        preparedStatement.execute();


        tdengineService.restSql("select * from db.tb");

        return tenantInfoMapper.findAll();

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
