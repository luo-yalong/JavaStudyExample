package com.lyl.mybatisplus_elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * (NbaPlayer)表实体类
 *
 * @author 罗亚龙
 * @since 2021-12-21 14:45:57
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NbaPlayer extends Model<NbaPlayer> implements Serializable{
    private static final long serialVersionUID = 453104363646966396L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id; 

    /**
     * countryEn
     */
    private String countryEn; 

    /**
     * teamName
     */
    private String teamName; 

    /**
     * birthDay
     */
    private BigInteger birthDay; 

    /**
     * country
     */
    private String country; 

    /**
     * teamCityEn
     */
    private String teamCityEn; 

    /**
     * code
     */
    private String code; 

    /**
     * displayAffiliation
     */
    private String displayAffiliation; 

    /**
     * displayName
     */
    private String displayName; 

    /**
     * schoolType
     */
    private String schoolType; 

    /**
     * teamConference
     */
    private String teamConference; 

    /**
     * teamConferenceEn
     */
    private String teamConferenceEn; 

    /**
     * weight
     */
    private String weight; 

    /**
     * teamCity
     */
    private String teamCity; 

    /**
     * playYear
     */
    private Integer playYear; 

    /**
     * jerseyNo
     */
    private String jerseyNo; 

    /**
     * teamNameEn
     */
    private String teamNameEn; 

    /**
     * draft
     */
    private Integer draft; 

    /**
     * displayNameEn
     */
    private String displayNameEn; 

    /**
     * birthDayStr
     */
    private String birthDayStr; 

    /**
     * heightValue
     */
    private Double heightValue; 

    /**
     * position
     */
    private String position; 

    /**
     * age
     */
    private Integer age; 

    /**
     * playerId
     */
    private String playerId; 

}

