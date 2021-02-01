package cn.helloworld.microservicea.service.feign;

import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhangkai
 */
@FeignClient(name = "tdengine",url = "k8s-node1:6041",configuration = TdengineService.FeignConfig.class)
public interface TdengineService {

    /**
     * @return
     */
    @PostMapping(
            "/rest/sql"
    )
    String restSql(@RequestBody String sql);

    @Configuration
    class FeignConfig {
        @Bean
        Logger.Level feignLevel() {
            return Logger.Level.FULL;
        }

        @Bean
        public TdengineFeignBasicAuthRequestInterceptor tdengineFeignBasicAuthRequestInterceptor() {
            return new TdengineFeignBasicAuthRequestInterceptor();
        }
    }
}
