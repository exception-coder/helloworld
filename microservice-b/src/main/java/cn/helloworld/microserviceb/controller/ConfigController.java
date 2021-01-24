package cn.helloworld.microserviceb.controller;

import cn.helloworld.microserviceb.properties.NacosProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangkai
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Autowired
    private NacosProperties nacosProperties;

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @RequestMapping("/get")
    public boolean get() {
        return useLocalCache;
    }

    @RequestMapping("/get-bean")
    public NacosProperties nacosProperties(){
        return nacosProperties;
    }
}