package cn.helloworld.user.authorize.framework.mvc;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

/**
 * @author zhangkai
 */
@Data
@Builder
public class ApiDTO implements Serializable {

    private RequestMethod[] requestMethod;

    private String[] path;
}
