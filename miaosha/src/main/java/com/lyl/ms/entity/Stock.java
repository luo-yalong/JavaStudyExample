package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (Stock)表实体类
 *
 * @author makejava
 * @since 2022-01-11 20:15:27
 */
@Data
@Accessors(chain = true)
public class Stock implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    //名称
    private String name;
    //库存
    private Integer count;
    //已售
    private Integer sale;
    //乐观锁、版本号
    private Integer version;
}

