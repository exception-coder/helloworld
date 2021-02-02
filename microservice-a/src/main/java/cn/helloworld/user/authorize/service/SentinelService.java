package cn.helloworld.user.authorize.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

/**
 * @author 42548
 */
@Service
public class SentinelService {

    /**
     * SentinelResource 注解用来标识资源是否被限流、降级。
     *
     * @param name
     * @return
     */
    @SentinelResource(value = "sayHello",blockHandler = "exceptionHandler", fallback = "helloFallback")
    public String sayHello(String name) {
        String illegalName = "熊泡泡";
        if(illegalName.equals(name)){
            throw new RuntimeException("熊泡泡呢称禁用");
        }
        return "Hello, " + name;
    }

    /**
     * Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数.
     *
     * @param name
     * @return
     */
    public String helloFallback(String name) {
        return String.format("回调函数 Halooooo %s", name);
    }

    /**
     * Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
     *
     * @param name
     * @param ex
     * @return
     */
    public String exceptionHandler(String name, BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        return "限流降级 Oops, error occurred at " + name;
    }
}
