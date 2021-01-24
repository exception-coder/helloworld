package cn.helloworld.microservicea.service;

import org.springframework.stereotype.Service;

/**
 * @author zhangkai
 */
@Service
public class HelloWorldService {

    public String sayHello(){
        return "hello microserviceA";
    }

}
