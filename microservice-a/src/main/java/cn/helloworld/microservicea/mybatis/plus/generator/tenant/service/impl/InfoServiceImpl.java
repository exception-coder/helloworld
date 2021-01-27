package cn.helloworld.microservicea.mybatis.plus.generator.tenant.service.impl;

import cn.helloworld.microservicea.mybatis.plus.generator.tenant.entity.Info;
import cn.helloworld.microservicea.mybatis.plus.generator.tenant.mapper.InfoMapper;
import cn.helloworld.microservicea.mybatis.plus.generator.tenant.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 泡泡熊
 * @since 2021-01-27
 */
@Service
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {

}
