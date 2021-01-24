package cn.helloworld.microserviceb.controller;

import cn.helloworld.microserviceb.service.HelloWorldService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangkai
 */
@AllArgsConstructor
@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    @GetMapping(
            "/say-hello"
    )
    public String sayHello(){
        return
        helloWorldService.sayHello();
    }
}
