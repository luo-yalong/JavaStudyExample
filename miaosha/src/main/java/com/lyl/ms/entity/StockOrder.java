package com.lyl.ms.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (StockOrder)表实体类
 *
 * @author makejava
 * @since 2022-01-11 20:15:27
 */
@Data
@Accessors(chain = true)
public class StockOrder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //库存id
    private Integer sid;
    //商品名称
    private String name;
    //创建时间
    private Date createTime;

}

