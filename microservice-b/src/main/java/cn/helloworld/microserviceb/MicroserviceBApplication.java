package cn.helloworld.microserviceb;

import cn.helloworld.microserviceb.properties.NacosProperties;
import com.zhongkerd.commons.util.SnowFlake;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangkai
 */
@EnableConfigurationProperties(NacosProperties.class)
@SpringBootApplication
public class MicroserviceBApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceBApplication.class, args);
    }

    @Bean
    public SnowFlake snowFlake(){
        return new SnowFlake(3L,3L);
    }

}
