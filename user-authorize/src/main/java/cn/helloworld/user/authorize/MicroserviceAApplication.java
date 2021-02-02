package cn.helloworld.user.authorize;

import cn.helloworld.user.authorize.framework.mvc.ControllerUrlService;
import cn.helloworld.user.authorize.properties.NacosProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * `@EnableFeignClients` 开启 feign 扫描支持
 * @author zhangkai
 */
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

    @Bean
    public ControllerUrlService controllerUrlService(){
        return new ControllerUrlService();
    }


}
