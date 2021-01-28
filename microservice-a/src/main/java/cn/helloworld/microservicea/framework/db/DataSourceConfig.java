package cn.helloworld.microservicea.framework.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author zhangkai
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "tdengineDataSource")
    @ConfigurationProperties("spring.datasource.druid.tdengine")
    public DataSource tdengineDataSource(){
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }


    @Bean(name = "devDataSource")
    @ConfigurationProperties("spring.datasource.druid.mysql.dev")
    public DataSource devDataSource(){
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }
}
