package cn.helloworld.microservicea.framework.db.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author zhangkai
 */
@Configuration
@MapperScan(basePackages = "cn.helloworld.microservicea.dao.mybatis.mapper.dev", sqlSessionTemplateRef  = "devSqlSessionTemplate")
public class DevDataSourceConfig {



    @Bean(name = "devDataSource")
    @ConfigurationProperties("spring.datasource.druid.mysql.dev")
    public DataSource devDataSource(){
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }

    @Bean(name = "devSqlSessionFactory")
    @Primary
    public SqlSessionFactory devSqlSessionFactory(@Qualifier("devDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/dev/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "devTransactionManager")
    @Primary
    public DataSourceTransactionManager devTransactionManager(@Qualifier("devDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "devSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate devSqlSessionTemplate(@Qualifier("devSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}