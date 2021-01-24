package cn.helloworld.microservicea.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhangkai
 */
@FeignClient(name = "microservice-b")
public interface MicroseviceBService {

    /**
     * say hello
     * @return
     */
    @GetMapping(
            "/say-hello"
    )
    String sayHello();
}
