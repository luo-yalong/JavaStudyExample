package com.lyl.mp_demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * (User)表实体类
 *
 * @author 罗亚龙
 * @since 2021-12-20 13:33:24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class User extends Model<User> implements Serializable {
    private static final long serialVersionUID = 856369833317810682L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 1：男  2：女
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 创建时间
     */
    private String created;

    /**
     * 修改时间
     */
    private String modify;

    /**
     * 版本
     */
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

}

