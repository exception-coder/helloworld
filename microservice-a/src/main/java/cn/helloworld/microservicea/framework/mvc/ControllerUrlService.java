package cn.helloworld.microservicea.framework.mvc;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author zhangkai
 */
@Slf4j
public class ControllerUrlService {

    /**
     * @param event
     */
    @EventListener
    public void applicationRunListener(ApplicationStartedEvent event) {


            // 从 `ApplicationContext` 获取所有包含 `Controller` 注解的类
            Map<String, Object> controllers = event.getApplicationContext().getBeansWithAnnotation(Controller.class);
            // `Controller` 对象集合
            List<Object> list = Lists.newArrayList();
            controllers.forEach((controllerKey, controller) -> {

                        // 判断当前`controller`是否存在`Controller`集合中 存在则表示已经处理 不再进行处理
                        if (!list.contains(controller)) {
                            list.add(controller);

                            RequestMapping requestMapping = controller.getClass().getAnnotation(RequestMapping.class);

                            // 完整的 controller 路径
                            AtomicReference<String> atomicPath = new AtomicReference<>();
                            // TODO: 2021/2/1 路径可为路径数组 暂时只获取第一个路径
                            Arrays.stream(requestMapping.value())
                                    .findFirst()
                                    .ifPresent(path -> atomicPath.set(path));

                            Method[] methods = ReflectionUtils.getAllDeclaredMethods(controller.getClass());

                            for (Method method : methods) {
                                // 获取方法上的 `org.springframework.web.bind.annotation.RequestMapping` 注解
                                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);

                                // 只解析 `RequestMapping`及其派生注解声明的方法
                                if (methodRequestMapping != null) {
                                    Arrays.stream(methodRequestMapping.value())
                                            .findFirst()
                                            .ifPresent(path -> atomicPath.set(atomicPath.get()==null?path:atomicPath.get()+path));
                                    log.info("contorller path:{}",atomicPath.get());
                                }


                            }
                        }
                    }

            );
    }


}