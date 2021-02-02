package cn.helloworld.user.authorize.framework.mvc;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
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

    // `RequestMapping` 派生注解集合
    private static final List<Class> REQUESTMAPPING_DERIVED = Lists.newArrayList(GetMapping.class,
            PutMapping.class,
            DeleteMapping.class,
            PatchMapping.class,
            PostMapping.class);


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
                        // TODO: 2021/2/1 路径可为路径数组 暂时只获取第一个路径
                        AtomicReference<String> atomicPath = new AtomicReference<>();
                        // 忽略 requestMapping 获取为空的情况
                        if (requestMapping != null) {
                            Arrays.stream(requestMapping.value())
                                    .findFirst()
                                    .ifPresent(path -> atomicPath.set(path));
                        }

                        Method[] methods = ReflectionUtils.getAllDeclaredMethods(controller.getClass());
                        for (Method method : methods) {
                            // 获取方法上的 `org.springframework.web.bind.annotation.RequestMapping` 注解及其派生注解
                            ApiDTO apiDTO = getRequestMappingDerivedClass(method);
                            // 只解析 `RequestMapping`及其派生注解声明的方法
                            if (apiDTO != null) {
                                log.info("controller path:{}", atomicPath.get()==null?apiDTO.getPath()[0]:atomicPath.get()+apiDTO.getPath()[0]);
                            }
                        }
                    }
                }

        );
    }


    private ApiDTO getRequestMappingDerivedClass(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            // 尝试从 `RequestMapping` 派生注解中获取 `RequestMapping` 注解
            for (Class aClass : REQUESTMAPPING_DERIVED) {
                Annotation annotation = method.getAnnotation(aClass);
                if (annotation != null) {
                    // 从`RequestMapping`派生注解上获取 `RequestMapping`注解
                    return ApiDTO.builder()
                            .path(
                                    ((String[]) AnnotationUtils.getValue(annotation))
                            )
                            .requestMethod(
                                    ((RequestMethod[]) AnnotationUtils.getValue(annotation, "method"))
                            )
                            .build();
                }else {
                    return null;
                }
            }
        } else {
            return ApiDTO.builder()
                    .path(requestMapping.value())
                    .requestMethod(requestMapping.method())
                    .build();
        }
        return null;
    }
}