package cn.helloworld.microservicea.framework.db.jpa;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.framework.qual.QualifierForLiterals;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *
 * 数据源配置
 *
 * @author 42548
 */
@Slf4j
@Configuration
public class DataSourceConfig {


    @Primary
    @Bean(name = "tdengineDataSource")
    @ConfigurationProperties("spring.datasource.druid.tdengine")
    public DataSource tdengineDataSource(){
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }


}
