package cn.helloworld.user.authorize.dao.tdengine.mapper;

import cn.helloworld.user.authorize.entity.tdengine.Tb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
