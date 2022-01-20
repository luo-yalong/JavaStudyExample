package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (Stock)表实体类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Stock extends Model<Stock> implements Serializable{
    private static final long serialVersionUID = -17172133476317770L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * 名称
     */
    private String name; 

    /**
     * 库存
     */
    private Integer count; 

    /**
     * 已售
     */
    private Integer sale; 

    /**
     * 乐观锁、版本号
     */
    @Version
    private Integer version; 

}

