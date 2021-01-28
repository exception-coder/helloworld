package cn.helloworld.microservicea.mybatis.plus.generator.basic.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 泡泡熊
 * @since 2021-01-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TenantInfo implements Serializable {

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
