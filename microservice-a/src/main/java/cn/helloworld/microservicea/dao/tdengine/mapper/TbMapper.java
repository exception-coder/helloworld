package cn.helloworld.microservicea.dao.tdengine.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author zhangkai
 */
@Mapper
public interface TbMapper {

    @Select("select * from tb")
    Map<String,Object> findAll();

}
