package cn.helloworld.microserviceb.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author 42548
 */
@Slf4j
@Data
@RefreshScope
@ConfigurationProperties(prefix = "sys.info")
public class NacosProperties {

  private String name;

}
