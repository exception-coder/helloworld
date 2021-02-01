package cn.helloworld.microservicea.entity.tdengine;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangkai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Tb  implements Serializable {

    private int temperature;

    private float humidity;

    private Date ts;
}
