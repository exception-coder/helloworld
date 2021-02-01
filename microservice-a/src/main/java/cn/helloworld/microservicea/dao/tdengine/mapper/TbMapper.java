package cn.helloworld.microservicea.dao.tdengine.mapper;

import cn.helloworld.microservicea.entity.tdengine.Tb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zhangkai
 */
@Mapper
public interface TbMapper {

    /**
     * 检索 tb 表所有记录
     *
     * @return
     */
    @Select("select * from tb")
    List<Tb> findAll();

}
