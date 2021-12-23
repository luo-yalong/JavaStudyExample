package com.lyl.mybatisplus_elasticsearch.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (NbaPlayer)表数据库访问层
 *
 * @author 罗亚龙
 * @since 2021-12-21 14:45:58
 */
@Mapper
@Repository
public interface NbaPlayerDao extends BaseMapper<NbaPlayer> {
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<NbaPlayer> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<NbaPlayer> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<NbaPlayer> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<NbaPlayer> entities);
}

