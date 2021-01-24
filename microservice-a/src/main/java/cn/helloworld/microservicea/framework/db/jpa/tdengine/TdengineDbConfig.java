package cn.helloworld.microservicea.framework.db.jpa.tdengine;

import com.zhongkerd.cloud.commons.framework.jpa.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author 42548
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // repository包名
        basePackages = "cn.helloworld.microservicea.dao.tdengine",
        // 实体管理bean名称
        entityManagerFactoryRef = "tdengineEntityManagerFactory",
        // 事务管理bean名称
        transactionManagerRef = "tdengineTransactionManager",
        repositoryBaseClass = BaseRepositoryImpl.class
)
public class TdengineDbConfig {

    @Primary
    @Bean(name = "tdengineJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.tdengine")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean(name = "tdengineEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("tdengineDataSource") DataSource tdengineDataSource
            , @Qualifier("tdengineJpaProperties") JpaProperties jpaProperties, EntityManagerFactoryBuilder builder) {
        return builder
                // 设置数据源
                .dataSource(tdengineDataSource)
                // 设置jpa配置
                .properties(jpaProperties.getProperties())
                // 设置实体包名
                .packages("cn.helloworld.microservicea.entity.tdengine")
                // 设置持久化单元名，用于 @PersistenceContext 注解获取 EntityManager 时指定数据源
                .persistenceUnit("tdenginePersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "tdengineEntityManager")
    public EntityManager entityManager(@Qualifier("tdengineEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    @Primary
    @Bean(name = "tdengineTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("tdengineEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }


}