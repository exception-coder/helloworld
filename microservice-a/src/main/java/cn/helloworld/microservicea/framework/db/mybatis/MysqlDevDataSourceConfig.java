package cn.helloworld.microservicea.framework.db.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
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
@MapperScan(basePackages = "cn.helloworld.microservicea.mybatis.plus.generator.basic.mapper", sqlSessionTemplateRef  = "devSqlSessionTemplate")
public class MysqlDevDataSourceConfig {


    @Bean(name = "devSqlSessionFactory")
    @Primary
    public SqlSessionFactory devSqlSessionFactory(@Qualifier("devDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/mysql/dev/*.xml"));
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