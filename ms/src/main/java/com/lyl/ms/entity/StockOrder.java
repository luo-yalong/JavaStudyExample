package com.lyl.ms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (StockOrder)表实体类
 *
 * @author 罗亚龙
 * @since 2022-01-16 14:15:05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class StockOrder extends Model<StockOrder> implements Serializable{
    private static final long serialVersionUID = 769803661818838703L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * 库存id
     */
    private Integer sid; 

    /**
     * 商品名称
     */
    private String name; 

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String create_time; 

}

