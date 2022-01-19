package com.lyl.ms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyl.ms.entity.StockOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (StockOrder)表数据库访问层
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Mapper
@Repository
public interface StockOrderDao extends BaseMapper<StockOrder> {
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<StockOrder> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<StockOrder> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<StockOrder> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<StockOrder> entities);
}

