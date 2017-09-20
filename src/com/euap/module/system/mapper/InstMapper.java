package com.euap.module.system.mapper;

import com.euap.module.system.entity.Inst;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 机构信息表
 *
 * @tableName BASE_INST
 * @auther www.hygdsoft.com
 * @date 2017年09月19日
 */
public interface InstMapper {

    /**
     * 插入
     */
    int insert(Inst record);

    /**
     * 根据主键删除
     */
    int deleteByPrimaryKey(@Param("instId") String instId);

    /**
     * 根据条件删除
     */
    int delete(Inst record);

    /**
     * 根据主键更新
     */
    int updateByPrimaryKey(Inst record);

    /**
     * 根据主键选择更新
     */
    int updateByPrimaryKeySelective(Inst record);

    /**
     * 根据主键查询
     */
    Inst selectByPrimaryKey(@Param("instId") String instId);

    /**
     * 根据条件统计
     */
    int count(Inst record);

    /**
     * 条件查询
     */
    List<Inst> select(@Param("record") Inst record, @Param("order") String order);

    /**
     * 分页查询
     */
    List<Inst> select(RowBounds rowBounds, @Param("record") Inst record, @Param("order") String order);

}