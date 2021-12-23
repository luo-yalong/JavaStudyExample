package com.lyl.mybatisplus_elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (User)表实体类
 *
 * @author 罗亚龙
 * @since 2021-12-21 16:19:34
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class User extends Model<User> implements Serializable{
    private static final long serialVersionUID = 228408039644156209L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("id")
    private Integer customerId;

    /**
     * name
     */
    private String name;

    /**
     * sex
     */
    private Integer sex;
}

