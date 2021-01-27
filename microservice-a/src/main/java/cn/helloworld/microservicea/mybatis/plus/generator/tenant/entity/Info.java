package cn.helloworld.microservicea.mybatis.plus.generator.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

/**
 * <p>
 * 
 * </p>
 *
 * @author 泡泡熊
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tenant_info")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录账户
     */
    private String account;

    /**
     * 密码
     */
    private String pwd;

    private String privateKey;

    private String publicKey;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;


}
