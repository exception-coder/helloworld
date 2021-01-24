package cn.helloworld.microserviceb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhangkai
 */
@Service
@Slf4j
public class HelloWorldService {

    @Value("${server.port}")
    private int serverPort;

    public String sayHello(){
        log.info("sayHello 调用成功 ");
        return "hello microserviceB "+serverPort;
    }

}
