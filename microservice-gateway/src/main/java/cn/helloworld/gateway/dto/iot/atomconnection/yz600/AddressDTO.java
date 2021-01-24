package cn.helloworld.gateway.dto.iot.atomconnection.yz600;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangkai
 */
@Builder
@Data
public class AddressDTO {

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
