package cn.helloworld.user.authorize.controller;

import cn.helloworld.user.authorize.service.SentinelService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangkai
 */
@AllArgsConstructor
@RestController
@RequestMapping("/sentinel")
public class SentinelController {

    private final SentinelService sentinelService;

    @GetMapping("/hello/{name}")
    public String helloworld(@PathVariable("name") String name){
        return sentinelService.sayHello(name);
    }

}
