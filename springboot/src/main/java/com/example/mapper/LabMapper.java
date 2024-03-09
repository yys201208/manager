package com.example.mapper;

import com.example.entity.Lab;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作admin相关数据接口
*/
public interface LabMapper {

    /**
      * 新增
    */
    int insert(Lab lab);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Lab lab);

    /**
      * 根据ID查询
    */
    Lab selectById(Integer id);

    /**
      * 查询所有
    */
    List<Lab> selectAll(Lab lab);


}