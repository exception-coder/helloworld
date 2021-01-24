package cn.helloworld.microservicea;

import cn.helloworld.microservicea.properties.NacosProperties;
import org.hibernate.dialect.MySQL57Dialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhangkai
 */
// 开启 feign 扫描支持
@EnableFeignClients
@EnableConfigurationProperties(NacosProperties.class)
@SpringBootApplication
public class MicroserviceAApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceAApplication.class, args);
    }

//    @Bean
//    public SnowFlake snowFlake(){
//        return new SnowFlake(3L,3L);
//    }

}
