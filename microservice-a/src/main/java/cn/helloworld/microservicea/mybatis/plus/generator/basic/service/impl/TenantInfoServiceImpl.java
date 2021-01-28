package cn.helloworld.microservicea.mybatis.plus.generator.basic.service.impl;

import cn.helloworld.microservicea.mybatis.plus.generator.basic.entity.TenantInfo;
import cn.helloworld.microservicea.mybatis.plus.generator.basic.mapper.TenantInfoMapper;
import cn.helloworld.microservicea.mybatis.plus.generator.basic.service.ITenantInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 泡泡熊
 * @since 2021-01-28
 */
@Service
public class TenantInfoServiceImpl extends ServiceImpl<TenantInfoMapper, TenantInfo> implements ITenantInfoService {

}
