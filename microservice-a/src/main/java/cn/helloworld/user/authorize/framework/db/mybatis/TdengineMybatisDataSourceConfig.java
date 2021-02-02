package cn.helloworld.user.authorize.framework.db.mybatis;

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
@MapperScan(basePackages = "cn.helloworld.microservicea.dao.tdengine.mapper", sqlSessionTemplateRef  = "tdengineSqlSessionTemplate")
public class TdengineMybatisDataSourceConfig {



    @Bean(name = "tdengineSqlSessionFactory")
    public SqlSessionFactory tdengineSqlSessionFactory(@Qualifier("tdengineDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/tdengine/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "tdengineTransactionManager")
    public DataSourceTransactionManager tdengineTransactionManager(@Qualifier("tdengineDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "tdengineSqlSessionTemplate")
    public SqlSessionTemplate devSqlSessionTemplate(@Qualifier("tdengineSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
