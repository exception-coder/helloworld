package cn.helloworld.microservicea.controller;

import cn.helloworld.microservicea.service.HelloWorldService;
import cn.helloworld.microservicea.service.feign.MicroseviceBService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nimbusds.jose.util.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author zhangkai
 */
@AllArgsConstructor
@RestController
@Slf4j
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    private final MicroseviceBService microseviceBService;

    @GetMapping(
            "/say-hello"
    )
    public String sayHello(){
        return
        helloWorldService.sayHello();
    }

    /**
     *  hello feign
     *
     * @return
     */
    @GetMapping("/hello-feign")
    public String helloFeign(){
        log.info("准备调用服务b");
        return microseviceBService.sayHello();
    }

    @PostMapping("/post")
    public JSONObject post(@RequestBody String requestBody){
        return JSONObject.parseObject(requestBody);
    }

}
