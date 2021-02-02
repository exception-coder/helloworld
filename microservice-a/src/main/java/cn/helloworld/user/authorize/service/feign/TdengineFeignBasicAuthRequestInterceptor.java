package cn.helloworld.user.authorize.service.feign;


import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author zhangkai
 */
public class TdengineFeignBasicAuthRequestInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization","Basic ZGV2OnRkZGV2UEBzc3cwUkQ=");
    }
}