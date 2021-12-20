package com.lyl.mp_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Integer version;

}

