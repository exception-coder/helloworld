package cn.helloworld.microservicea.dao.tdengine.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    Map<String,Object> findAll();

}
